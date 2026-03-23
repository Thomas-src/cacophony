package org.helmo.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import org.helmo.domain.AdvertMessage;

/**
 * Listener UDP multicast pour la découverte des Nodes (écoute des paquets ADVERT).
 * À implémenter : réception, parsing, notification de nouveaux Nodes.
 */
public class SpeakerMulticastListener {
    private static final String MULTICAST_ADDRESS = "224.224.200.200";

    public void startListening() {
    }

    public interface AdvertListener {
        void onAdvertReceived(AdvertMessage advert, String sourceIp);
    }

    private final AdvertListener listener;
    private MulticastSocket socket;
    private boolean running;

    public SpeakerMulticastListener(AdvertListener listener) {
        this.listener = listener;
    }

    public void start() {
        try {
            socket = new MulticastSocket(4446); // Port par défaut pour ADVERT
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS); // Adresse multicast
            socket.joinGroup(group);
            running = true;

            new Thread(
                    () -> {
                        while (running) {
                            try {
                                byte[] buffer = new byte[1024];
                                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                                socket.receive(packet);

                                String message =
                                        new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                                String sourceIp = packet.getAddress().getHostAddress();

                                // Parsing du message ADVERT (simplifié ici)
                                AdvertMessage advert = new AdvertMessage(message);
                                listener.onAdvertReceived(advert, sourceIp);
                            } catch (IOException e) {
                                if (running) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    })
                    .start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        running = false;
        if (socket != null) {
            try {
                socket.leaveGroup(InetAddress.getByName(MULTICAST_ADDRESS));
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
