package org.helmo.network;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.domain.Domain;
import org.helmo.crypto.CryptoService;
import org.helmo.handler.CommandMenu;

// todo Utile ? je pense que le traffic est seulement en udp


/**
 * Gestion des connexions TCP unicast entre le Speaker et les Nodes.
 * À implémenter : ouverture/fermeture de connexions, envoi/réception de messages.
 */
public class SpeakerTcpConnector implements ConnectionListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, NodeTCPConnection> connections = new ConcurrentHashMap<>();
    private final CryptoService cryptoService;
    private final CommandMenu tcpMenu;
    public SpeakerTcpConnector(CryptoService cryptoService, CommandMenu tcpMenu) {
        this.cryptoService = cryptoService;
        this.tcpMenu = tcpMenu;
    }

    /**
     * Ouvre une connexion TCP avec un Node.
     *
     * @param domain
     * @param ip
     * @param port
     */
    public void connectToNode(Domain domain, String ip, int port) {
        if (containsNode(domain)) {
            LOGGER.info("{} déjà connecté", domain.getName());
            return;
        }
        try {
            Socket socket = new Socket(ip, port);
            NodeTCPConnection conn = create(domain, socket);
            connections.put(domain.getName(), conn);
            LOGGER.info("Connecté à {} ({}:{})", domain.getName(), ip, port);
        } catch (IOException e) {
            LOGGER.error("Échec connexion à {}:{} — {}", ip, port, e.getMessage());
        }
    }

    private NodeTCPConnection create(Domain domain, Socket socket) throws IOException {
        NodeTCPConnection conn = new NodeTCPConnection(domain, socket, tcpMenu, cryptoService, this);
        conn.start();
        return conn;
    }

    /**
     * Envoie un message à un Node via la connexion TCP.
     *
     * @param domainName
     * @param message
     */
    public void sendToNode(String domainName, String message) {
        NodeTCPConnection connection = connections.get(domainName);
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

    @Override
    public void onConnectionClosed(String domainName) {
        connections.remove(domainName);
        LOGGER.info("[{}] retiré des connexions actives", domainName);
    }
}
