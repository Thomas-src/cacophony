package org.helmo.network.impl;

import java.io.IOException;
import java.net.Socket;
import org.helmo.network.NodeConnection;

/**
 * Implémentation de l'interface NodeConnection pour gérer une connexion TCP avec un Node.
 */
public class NodeConnectionImpl implements NodeConnection {
    private final String domain;
    private final Socket socket;

    public NodeConnectionImpl(String domain, Socket socket) {
        this.domain = domain;
        this.socket = socket;
    }

    @Override
    public void sendMessage(byte[] message) {
        try {
            socket.getOutputStream().write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDomain() {
        return domain;
    }
}
