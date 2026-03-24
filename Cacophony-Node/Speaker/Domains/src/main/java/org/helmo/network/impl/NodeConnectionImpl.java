package org.helmo.network.impl;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.network.NodeConnection;

/**
 * Implémentation de l'interface NodeConnection pour gérer une connexion TCP avec un Node.
 */
public class NodeConnectionImpl implements NodeConnection {
    private static final Logger LOGGER = LogManager.getLogger();

    private final String domain;
    private final Socket socket;

    public NodeConnectionImpl(String domain, Socket socket) {
        this.domain = domain;
        this.socket = socket;
    }

    @Override
    public void sendMessage(String line) {
        try {
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            writer.print(line );
        } catch (IOException e) {
            LOGGER.error("Erreur envoi vers {}", domain, e);
        }
    }

    public BufferedReader getReader() throws IOException {
        return new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.error("Erreur envoi vers {}", domain, e);
        }
    }

    @Override
    public String getDomain() {
        return domain;
    }
}
