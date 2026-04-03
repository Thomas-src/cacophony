package org.helmo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.handler.CommandMenu;
import org.helmo.network.SpeakerTcpConnector;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public class Speaker extends Thread {
    private static final Logger LOGGER = LogManager.getLogger();
    private final SpeakerConfig speakerConfig;
    private final SpeakerTcpConnector speakerTcpConnector;
    private boolean isStarted = false;
    private boolean groupJoined = false;
    private final CommandMenu commandMenu;

    public Speaker(SpeakerConfig speakerConfig, CommandMenu commandMenu, SpeakerTcpConnector connector) {
        this.speakerConfig = speakerConfig;
        this.commandMenu = commandMenu;
        this.speakerTcpConnector = connector;
        setName("UDP:R");
    }

    @Override
    public void run() {
        try (MulticastSocket socket = new MulticastSocket(speakerConfig.multicastPort())) {
            InetAddress group = InetAddress.getByName(speakerConfig.multicastAdress());
            NetworkInterface networkInterface = getValidNetworkInterface(speakerConfig.networkInterface());
            socket.joinGroup(new InetSocketAddress(group, speakerConfig.multicastPort()), networkInterface);
            groupJoined = true;
            isStarted = true;

            while (isStarted) {
                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                    String sourceIp = packet.getAddress().getHostAddress();


                    LOGGER.info("[UDP] msg de {}: {}", sourceIp, message);
                    commandMenu.execute(message);

                } catch (IOException e) {
                    LOGGER.error("Le serveur s'est arrêté:", e);
                }
            }


        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    /*
    public void stop() {
        isStarted = false;
        if (socket != null && !socket.isClosed()) {
            try {
                if (groupJoined) {
                    InetAddress group = InetAddress.getByName(multicastAddress);
                    NetworkInterface networkInterface = NetworkInterface.getByName(networkInterfaceName);
                    // : Gérer les exceptions spécifiques pour leaveGroup.
                    socket.leaveGroup(new java.net.InetSocketAddress(group, multicastPort), networkInterface);
                    groupJoined = false;
                }
                socket.close();
            } catch (IOException e) {
                // : Utiliser un logger au lieu de printStackTrace.
                e.printStackTrace();
            }
        }
    }
*/

    /**
     * Obtient une interface réseau valide pour le multicast IPv4.
     * Tente d'utiliser l'interface spécifiée, sinon trouve automatiquement une interface appropriée.
     */
    private NetworkInterface getValidNetworkInterface(String interfaceName) throws IOException {
        NetworkInterface netIf = null;

        // Essayer d'obtenir l'interface par son nom
        if (interfaceName != null && !interfaceName.isEmpty()) {
            netIf = NetworkInterface.getByName(interfaceName);
        }

        // Si l'interface spécifiée n'existe pas ou n'est pas valide, trouver une interface appropriée
        if (netIf == null || !isValidMulticastInterface(netIf)) {
            LOGGER.warn("Network interface '{}' not found or not valid for IPv4 multicast.", interfaceName);
            LOGGER.warn("Attempting to find a suitable network interface automatically...");
            netIf = findSuitableNetworkInterface();
        }

        if (netIf == null) {
            LOGGER.error("No suitable network interface found for multicast. Available interfaces:\n{}", listNetworkInterfaces());
            throw new IOException("No suitable network interface found for multicast.");
        }

        LOGGER.info("Using network interface: {} ({})", netIf.getName(), netIf.getDisplayName());
        return netIf;
    }


    /**
     * Vérifie si une interface réseau est valide pour le multicast IPv4.
     */
    private boolean isValidMulticastInterface(NetworkInterface netIf) {
        try {
            if (netIf == null || !netIf.isUp() || !netIf.supportsMulticast()) {
                return false;
            }

            // Vérifier qu'elle a au moins une adresse IPv4
            Enumeration<InetAddress> addresses = netIf.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                    return true;
                }
            }
            return false;
        } catch (SocketException e) {
            // : Utiliser un logger pour cette exception.
            LOGGER.error("Failed to check network interface: {}", netIf.getName(), e);


            return false;
        }
    }

    /**
     * Trouve une interface réseau appropriée pour le multicast IPv4.
     */
    private NetworkInterface findSuitableNetworkInterface() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface netIf = interfaces.nextElement();
            if (isValidMulticastInterface(netIf)) {
                return netIf;
            }
        }
        return null;
    }

    /**
     * Liste toutes les interfaces réseau disponibles.
     */
    private String listNetworkInterfaces() {
        StringBuilder sb = new StringBuilder();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface netIf = interfaces.nextElement();
                sb.append("  - ").append(netIf.getName())
                        .append(" (").append(netIf.getDisplayName()).append(")")
                        .append(" - Up: ").append(netIf.isUp())
                        .append(", Multicast: ").append(netIf.supportsMulticast())
                        .append("\n");
            }
        } catch (SocketException e) {
            sb.append("Error listing network interfaces: ").append(e.getMessage());
        }
        return sb.toString();
    }
}
