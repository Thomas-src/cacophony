package org.helmo.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import org.helmo.domain.AdvertMessage;

/**
 * Listener UDP multicast pour la découverte des Nodes (écoute des paquets ADVERT).
 * À implémenter : réception, parsing, notification de nouveaux Nodes.
 */
public class SpeakerMulticastListener {
    private final String multicastAddress;
    private final int multicastPort;
    private final String networkInterfaceName;
    private final AdvertListener listener;
    private MulticastSocket socket;
    private volatile boolean running;
    private volatile boolean groupJoined;

    public interface AdvertListener {
        void onAdvertReceived(AdvertMessage advert, String sourceIp);
    }

    public SpeakerMulticastListener(String multicastAddress, int multicastPort, String networkInterface, AdvertListener listener) {
        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;
        this.networkInterfaceName = networkInterface;
        this.listener = listener;
        this.groupJoined = false;
    }

    public void startListening() throws IOException {
        socket = new MulticastSocket(multicastPort);
        InetAddress group = InetAddress.getByName(multicastAddress);
        NetworkInterface networkInterface = getValidNetworkInterface(networkInterfaceName);
        socket.joinGroup(new java.net.InetSocketAddress(group, multicastPort), networkInterface);
        groupJoined = true;
        running = true;

        while (running) {
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                String sourceIp = packet.getAddress().getHostAddress();

                AdvertMessage advert = new AdvertMessage(message);
                listener.onAdvertReceived(advert, sourceIp);
            } catch (IOException e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            try {
                if (groupJoined) {
                    InetAddress group = InetAddress.getByName(multicastAddress);
                    NetworkInterface networkInterface = NetworkInterface.getByName(networkInterfaceName);
                    socket.leaveGroup(new java.net.InetSocketAddress(group, multicastPort), networkInterface);
                    groupJoined = false;
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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
            System.err.println("Warning: Network interface '" + interfaceName + "' not found or not valid for IPv4 multicast.");
            System.err.println("Attempting to find a suitable network interface automatically...");
            netIf = findSuitableNetworkInterface();
        }

        if (netIf == null) {
            throw new IOException("No suitable network interface found for multicast. Available interfaces:\n" + listNetworkInterfaces());
        }

        System.out.println("Using network interface: " + netIf.getName() + " (" + netIf.getDisplayName() + ")");
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