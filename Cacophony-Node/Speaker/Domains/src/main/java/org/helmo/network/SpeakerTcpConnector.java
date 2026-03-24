package org.helmo.network;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.helmo.SpeakerDomainService;
import org.helmo.network.impl.NodeConnectionImpl;
import org.helmo.security.CryptoService;

// todo Utile ? je pense que le traffic est seulement en udp
/**
 * Gestion des connexions TCP unicast entre le Speaker et les Nodes.
 * À implémenter : ouverture/fermeture de connexions, envoi/réception de messages.
 */
public class SpeakerTcpConnector {
    private final Map<String, NodeConnection> connections = new ConcurrentHashMap<>();
    private final CryptoService cryptoService;
    public SpeakerTcpConnector(CryptoService cryptoService) {
        this.cryptoService = cryptoService ;
    }

    /**
     * Ouvre une connexion TCP avec un Node.
     * @param domain
     * @param ip
     * @param port
     */
    public void connectToNode(String domain, String ip, int port, byte[] aesKey) {
        try {
            Socket socket = new Socket(ip, port);
            NodeConnection connection = new NodeConnectionImpl(domain, socket);
            // todo decrypt message
           // cryptoService.decrypt(contenu, aeskey)
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
    public void sendToNode(String domain, String message) {
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
