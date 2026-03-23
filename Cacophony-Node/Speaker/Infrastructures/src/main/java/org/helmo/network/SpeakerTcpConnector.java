package org.helmo.network;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.helmo.network.impl.NodeConnectionImpl;

/**
 * Gestion des connexions TCP unicast entre le Speaker et les Nodes.
 * À implémenter : ouverture/fermeture de connexions, envoi/réception de messages.
 */
public class SpeakerTcpConnector {
    private final Map<String, NodeConnection> connections;

    public SpeakerTcpConnector() {
        this.connections = new ConcurrentHashMap<>();
    }

    /**
     * Ouvre une connexion TCP avec un Node.
     * @param domain
     * @param ip
     * @param port
     */
    public void connectToNode(String domain, String ip, int port) {
        try {
            Socket socket = new Socket(ip, port);
            NodeConnection connection = new NodeConnectionImpl(domain, socket);
            connections.put(domain, connection);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Envoie un message à un Node via la connexion TCP.
     * @param domain
     * @param message
     */
    public void sendToNode(String domain, byte[] message) {
        NodeConnection connection = connections.get(domain);
        if (connection != null) {
            connection.sendMessage(message);
        } else {
            System.err.println("No connection found for domain: " + domain);
        }
    }

    /**
     * Ferme la connexion TCP avec un Node.
     * @param domain
     */
    public void closeConnection(String domain) {
        NodeConnection connection = connections.remove(domain);
        if (connection != null) {
            connection.close();
        }
    }
}
