package org.helmo.network.impl;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.domain.Domain;
import org.helmo.network.NodeConnection;

/**
 * Implémentation de l'interface NodeConnection pour gérer une connexion TCP avec un Node.
 */
public class NodeConnectionImpl implements NodeConnection {
    private static final Logger LOGGER = LogManager.getLogger();

    private final Domain domain;
    private final Socket socket;
    private PrintWriter writer;

    public NodeConnectionImpl(Domain domain, Socket socket) throws IOException {
        this.domain = domain;
        this.socket = socket;
        try {
            this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        } catch (IOException e) {
            LOGGER.error("Erreur création writer vers {}", domain, e);
        }
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
