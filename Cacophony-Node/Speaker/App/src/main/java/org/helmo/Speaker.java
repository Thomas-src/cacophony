package org.helmo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.handler.CommandMenu;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;

public class Speaker extends  Thread{
    private static final Logger LOGGER = LogManager.getLogger();
    private final SpeakerConfig config;
    private boolean isStarted = false;
    private boolean groupJoined = false;
    private final CommandMenu commandMenu;

    public Speaker(SpeakerConfig speakerConfig, CommandMenu commandMenu) {
        this.config = speakerConfig;
        this.commandMenu =commandMenu;
    }
    @Override
    public void run() {
        try(MulticastSocket socket =  new MulticastSocket(config.multicastPort())) {
            InetAddress group = InetAddress.getByName(config.multicastAdress());
            NetworkInterface networkInterface = getValidNetworkInterface(networkInterfaceName);
            socket.joinGroup(new java.net.InetSocketAddress(group, config.multicastPort()), networkInterface);
            groupJoined = true;
            isStarted = true;

            while (isStarted) {
                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String message = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                    //  String sourceIp = packet.getAddress().getHostAddress();


                    LOGGER.info(message);
                } catch (IOException e) {
                    LOGGER.error("Le serveur s'est arrêté:",e);
                }
            }


        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }
}
