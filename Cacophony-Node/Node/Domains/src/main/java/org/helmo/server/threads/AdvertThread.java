package org.helmo.server.threads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Config;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

// Classe généré par IA TODO la réécrire
public class AdvertThread extends Thread {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Config config;

    public AdvertThread(Config config) {
        this.config = config;
        setName("AdvertThread");
    }

    @Override
    public void run() {
        try (MulticastSocket socket = new MulticastSocket()) {

            NetworkInterface networkInterface = NetworkInterface.getByName(config.networkInterface()); // TODO Changer dans la config pour mettre votre networkInterface
            socket.setNetworkInterface(networkInterface);

            InetAddress group = InetAddress.getByName(config.multicastAddress());

            while (!Thread.currentThread().isInterrupted()) {

                String advert = "ADVERT " +
                        config.unicastPort() + " " +
                        config.currentDomain() + "\r\n";

                byte[] buffer = advert.getBytes(StandardCharsets.UTF_8);

                DatagramPacket packet = new DatagramPacket(
                        buffer,
                        buffer.length,
                        group,
                        config.multicastPort()
                );

                socket.send(packet);

                LOGGER.info("ADVERT envoyé : {}", advert.trim());

                Thread.sleep(30000);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}