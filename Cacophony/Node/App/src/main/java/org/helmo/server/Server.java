package org.helmo.server;


import org.helmo.Config;
import org.helmo.Exception.ServerException;
import org.helmo.server.threads.TaskThread;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Implémentation concrète d'un serveur pour gérer les connexions réseau.
 * <p>
 * Cette classe étend AbstractServer et fournit l'implémentation spécifique
 * pour la création de sockets serveur. Elle gère la configuration du serveur,
 * les threads de tâches et les sessions actives.
 */
public class Server extends AbstractServer {
    /**
     * Construit un nouveau serveur avec la configuration et les composants spécifiés.
     *
     * @param config la configuration du serveur
     * @param taskThread le thread de gestion des tâches
     * @param activeSession la gestion des sessions actives
     */
    public Server(Config config, TaskThread taskThread, ActiveSession activeSession) {
        super(config, taskThread, activeSession);
    }

    /**
     * Crée un socket serveur sur le port spécifié.
     * <p>
     * Cette méthode initialise un nouveau ServerSocket qui écoute sur le port donné.
     * En cas d'erreur lors de la création du socket, une ServerException est levée.
     *
     * @param port le numéro de port sur lequel le serveur doit écouter
     * @return le ServerSocket créé et prêt à accepter des connexions
     * @throws ServerException si une erreur se produit lors de la création du socket serveur
     */
    @Override
    public ServerSocket createServerSocket(int port) throws ServerException {
        try {
            return new ServerSocket(port);
        } catch (IOException e) {
            throw new ServerException(e.toString());
        }
    }
}