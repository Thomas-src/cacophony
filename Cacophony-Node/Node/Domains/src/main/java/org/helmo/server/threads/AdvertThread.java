package org.helmo.server.threads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Config;
import org.helmo.MessageBuilder;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class AdvertThread extends Thread {
    private static final Logger LOGGER = LogManager.getLogger();
    private final int port;
    private final String host;
    private boolean running = true;

    private DatagramSocket socket;

    public AdvertThread(Config config) {
        this.port = config.relayPort();
        this.host = config.currentDomain();
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(host);

            while (running) {
                Thread.sleep(30000);
                String message = MessageBuilder.buildAdvert(port,host);
                byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);

                socket.send(packet);
                LOGGER.info("ADVERT envoyé : {}", message);
            }
        } catch (Exception e) {
            LOGGER.error("Erreur dans AdvertThread", e);
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                LOGGER.info("Socket UDP fermé proprement");
            }
        }
    }

    public void shutdown() {
        running = false;
        if (socket != null) {
            socket.close();
        }
    }
}