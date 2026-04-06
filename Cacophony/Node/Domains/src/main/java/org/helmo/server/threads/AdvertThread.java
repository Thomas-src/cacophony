package org.helmo.server.threads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Config;
import org.helmo.MessageBuilder;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Thread responsable de la diffusion périodique (multicast) de l'existence du domaine sur le réseau.
 * Permet aux autres nœuds de découvrir ce serveur automatiquement.
 */
public class AdvertThread extends Thread {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Config config;

    private final String advert;

    /**
     * Initialise le thread d'annonce avec la configuration spécifiée.
     * @param config La configuration contenant les paramètres réseau et du domaine.
     */
    public AdvertThread(Config config) {
        this.config = config;
        this.advert =  MessageBuilder.buildAdvert(config.relayPort(), config.currentDomain());
        setName("AdvertThread");
    }

    @Override
    public void run() {
        try (ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()) {
            try (MulticastSocket socket = new MulticastSocket(null)) {

                // L'objectif ici est d'éviter que setNetworkInterface() (setsockopt) fasse échouer le thread sous Windows.
                // On bind sur INADDR_ANY et on laisse l'OS choisir si la configuration explicite d'interface échoue.
                socket.setReuseAddress(true);
                socket.bind(new InetSocketAddress(0));

                InetAddress group = InetAddress.getByName(config.multicastAddress());
                if (!group.isMulticastAddress()) {
                    LOGGER.error("L'adresse spécifiée n'est pas une adresse multicast valide: {}", config.multicastAddress());
                    return;
                }

                NetworkInterface networkInterface = resolveNetworkInterface(config.networkInterface());
                InetAddress ifaceIpv4 = pickIpv4Address(networkInterface);

                LOGGER.info("Adresse multicast: {}", group.getHostAddress());
                LOGGER.info("Port multicast: {}", config.multicastPort());
                if (networkInterface != null) {
                    LOGGER.info("Interface sélectionnée: {} (displayName={})", networkInterface.getName(), networkInterface.getDisplayName());
                } else {
                    LOGGER.warn("Aucune interface multicast explicite n'a pu être sélectionnée. L'OS choisira l'interface.");
                }
                if (ifaceIpv4 != null) {
                    LOGGER.info("IPv4 choisie pour setInterface(): {}", ifaceIpv4.getHostAddress());
                }

                boolean interfaceConfigured = false;
                if (networkInterface != null) {
                    try {
                        socket.setNetworkInterface(networkInterface);
                        LOGGER.info("Interface réseau configurée via setNetworkInterface(): {}", networkInterface.getName());
                        interfaceConfigured = true;
                    } catch (SocketException e) {
                        // Problème connu sous Windows sur certaines configurations.
                        LOGGER.warn("setNetworkInterface() a échoué (setsockopt): {}. Tentative via setInterface().", e.getMessage());
                    }
                }

                if (!interfaceConfigured && ifaceIpv4 != null) {
                    try {
                        socket.setInterface(ifaceIpv4);
                        LOGGER.info("Interface réseau configurée via setInterface(): {}", ifaceIpv4.getHostAddress());
                    } catch (SocketException e) {
                        LOGGER.warn("setInterface() a échoué: {}. Fallback: OS default.", e.getMessage());
                    }
                }

                Runnable task = () -> {
                    byte[] buffer = advert.getBytes(StandardCharsets.UTF_8);

                    DatagramPacket packet = new DatagramPacket(
                            buffer,
                            buffer.length,
                            group,
                            config.multicastPort()
                    );

                    try {
                        socket.send(packet);
                        LOGGER.info("Paquet envoyé avec succès à {}: {}", group.getHostAddress(), advert.trim());
                    } catch (IOException e) {
                        LOGGER.error("Erreur lors de l'envoi du paquet: {}", e.getMessage());
                    }

                };

                scheduler.scheduleAtFixedRate(task, 15, 30, TimeUnit.SECONDS);

                CountDownLatch latch = new CountDownLatch(1);
                try {
                    // On garde le thread vivant tant que le serveur tourne.
                    latch.await();
                } catch (InterruptedException e) {
                    LOGGER.error("Thread interrompu: {}", e.getMessage());
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                LOGGER.error("Erreur inattendue: {}", e.getMessage());
            }
        }
    }

    /**
     * Résout l'interface réseau à utiliser pour le multicast.
     * @param configuredName Le nom de l'interface configurée (peut être null).
     * @return L'interface réseau sélectionnée ou null si aucune n'est trouvée.
     * @throws SocketException En cas d'erreur lors de l'accès aux interfaces réseau.
     *
     */
    private static NetworkInterface resolveNetworkInterface(String configuredName) throws SocketException {
        NetworkInterface configured = null;
        if (configuredName != null && !configuredName.isBlank()) {
            configured = NetworkInterface.getByName(configuredName);
            if (configured == null) {
                LOGGER.warn("Interface réseau configurée introuvable: {}. Bascule en sélection automatique.", configuredName);
            } else if (isNotUsableForMulticast(configured)) {
                LOGGER.warn("Interface réseau configurée inutilisable pour multicast: {}. Bascule en sélection automatique.", configuredName);
                configured = null;
            }
        }

        if (configured != null) return configured;

        // Sélection automatique: première interface UP, non loopback, non virtuelle, supportant multicast et avec une IPv4.
        Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
        while (ifaces.hasMoreElements()) {
            NetworkInterface ni = ifaces.nextElement();
            if (isNotUsableForMulticast(ni)) continue;
            InetAddress ipv4 = pickIpv4Address(ni);
            if (ipv4 == null) continue;
            return ni;
        }

        return null;
    }

    /**
     * Vérifie si une interface réseau est inutilisable pour le multicast.
     * @param ni L'interface à tester.
     * @return true si l'interface ne peut pas être utilisée.
     * @throws SocketException En cas d'erreur d'accès aux propriétés de l'interface.
     *
     */
    private static boolean isNotUsableForMulticast(NetworkInterface ni) throws SocketException {
        if (ni == null) return true;
        if (!ni.isUp()) return true;
        if (!ni.supportsMulticast()) return true;
        if (ni.isLoopback()) return true;
        return ni.isVirtual();
    }

    /**
     * Sélectionne une adresse IPv4 valide sur une interface donnée.
     * @param ni L'interface réseau.
     * @return Une InetAddress IPv4, ou null si aucune n'est trouvée.
     *
     *
     */
    private static InetAddress pickIpv4Address(NetworkInterface ni) {
        if (ni == null) return null;
        InetAddress best = null;
        for (Enumeration<InetAddress> e = ni.getInetAddresses(); e.hasMoreElements(); ) {
            InetAddress addr = e.nextElement();
            if (!(addr instanceof Inet4Address)) continue;
            if (addr.isLoopbackAddress()) continue;
            // On évite si possible les adresses link-local 169.254.x.x
            if (best == null) best = addr;
            if (!addr.isLinkLocalAddress()) return addr;
        }
        return best;
    }
}