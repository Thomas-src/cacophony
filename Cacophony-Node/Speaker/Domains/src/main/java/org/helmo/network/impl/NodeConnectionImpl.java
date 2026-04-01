package org.helmo.network.impl;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.domain.Domain;
import org.helmo.exceptions.HandlerException;
import org.helmo.network.NodeConnection;
import org.helmo.network.SpeakerTcpConnector;

/**
 * Implémentation de l'interface NodeConnection pour gérer une connexion TCP avec un Node.
 */
public class NodeConnectionImpl extends Thread implements NodeConnection  {
    private static final Logger LOGGER = LogManager.getLogger();

    private final Domain domain;
    private final Socket socket;
    private final PrintWriter writer;
    private Thread readerThread;



    public NodeConnectionImpl(Domain domain, Socket socket, String ip) throws IOException {
        this.domain = domain;
        this.socket = socket;
        this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

    }

    @Override
    public void start() {
        if (readerThread != null && readerThread.isAlive()) return;

        readerThread = new Thread(() -> {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.isBlank()) {
                        String ip = socket.getInetAddress().getHostAddress();

                        LOGGER.info("[{}] reçu : {}", domain.getName(), line);
                    }
                }

            } catch (IOException e) {
                LOGGER.info("Connexion fermée avec {}", domain.getName());
            }
        });

        readerThread.setDaemon(true);
        readerThread.start();
    }

    public void execute(String message, String sourceIp) throws HandlerException {

        Domain sourceDomain = SpeakerTcpConnector.getDomain(sourceIp);
        String key = sourceDomain.getAesKey();
        try {
            String decryptedMessage = cryptoProvider.decrypt(message,key);

            LOGGER.info("Message crypted, {}", message);
            LOGGER.info("Message decrypted, {}", decryptedMessage);

            if (tryExecute(decryptedMessage)) {
                LOGGER.info("Exécution de la commande avec le msg décrypté");
                return;
            }

        } catch (Exception e) {
            LOGGER.warn("Decryption failed: {}", e.getMessage());
        }


        LOGGER.warn("Command {} not found", message);
    }

    @Override
    public void sendMessage(String line) {
        writer.println(line);
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.error("Erreur fermeture du socket {}", domain, e);
        }
    }
    @Override
    public boolean isAlive() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }

    @Override
    public String getDomain() {
        return domain.getName();
    }
}
