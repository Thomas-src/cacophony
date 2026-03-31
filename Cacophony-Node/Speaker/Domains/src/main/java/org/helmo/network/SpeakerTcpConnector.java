package org.helmo.network;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.domain.Domain;
import org.helmo.handler.CommandMenu;
import org.helmo.network.impl.NodeConnectionImpl;

// todo Utile ? je pense que le traffic est seulement en udp

/**
 * Gestion des connexions TCP unicast entre le Speaker et les Nodes.
 * À implémenter : ouverture/fermeture de connexions, envoi/réception de messages.
 */
public class SpeakerTcpConnector {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, NodeConnection> connections = new ConcurrentHashMap<>();

    /**
     * Ouvre une connexion TCP avec un Node.
     *
     * @param domain
     * @param ip
     * @param port
     */
    public void connectToNode(Domain domain, String ip, int port) {
        try {
            if (containsNode(domain)) { // todo supprimer lorsque le client se deconnecte
                LOGGER.info("{} is already connected", domain);
                return;
            }

            Socket socket = new Socket(ip, port);
            NodeConnection connection = new NodeConnectionImpl(domain, socket);
            connections.put(domain.getName(), connection);
        } catch (IOException e) {
            LOGGER.error("Erreur lors de la création du socket {}", e.getMessage());
        }
    }

    /**
     * Envoie un message à un Node via la connexion TCP.
     *
     * @param domainName
     * @param message
     */
    public void sendToNode(String domainName, String message) {
        NodeConnection connection = connections.get(domainName);
        if (connection != null && connection.isAlive()) {
            connection.sendMessage(message);
        } else if (connection != null) {
            LOGGER.info("No connection found for domain: " + domainName);
            connections.remove(domainName);

        }
    }

    public boolean containsNode(Domain domain) {
        return connections.keySet()
                .stream()
                .anyMatch(d -> d.equals(domain.getName()));
    }

    /**
     * Ferme la connexion TCP avec un Node.
     */
    public void closeConnection(String domainName) {

            NodeConnection connection = connections.remove(domainName);
            if (connection != null) {
                connection.close();

        }
    }
}
