package org.helmo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Config;
import org.helmo.Exception.ServerException;
import org.helmo.server.threads.TaskThread;
import org.helmo.server.threads.WestThread;
import org.helmo.services.RandomService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstraite représentant un serveur générique.
 * <p>
 * Cette classe fournit l'implémentation de base d'un serveur qui écoute les connexions
 * entrantes sur un port unicast. Elle gère plusieurs threads pour traiter les connexions
 * clients simultanées et maintient les sessions actives.
 * <p>
 * Les classes dérivées doivent implémenter la méthode {@code createServerSocket} pour
 * fournir l'implémentation spécifique de création de socket serveur.
 */
public abstract class AbstractServer implements IServer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<WestThread> westThreads = new ArrayList<>();

    private final Config config;
    private final ActiveSession activeSession;
    private final TaskThread taskThread;
    private boolean stop = false;
    private boolean isStarted = false;
    private ServerSocket listener;

    /**
     * Construit un nouveau serveur abstrait avec la configuration et les composants spécifiés.
     *
     * @param config        la configuration du serveur contenant les paramètres de connexion
     * @param taskThread    le thread de tâches pour gérer les opérations asynchrones
     * @param activeSession le gestionnaire des sessions actives
     */
    public AbstractServer(Config config, TaskThread taskThread, ActiveSession activeSession) {
        this.config = config;
        this.taskThread = taskThread;
        this.activeSession = activeSession;
    }


    /**
     * Démarre le serveur et écoute les connexions entrantes.
     * <p>
     * Cette méthode crée un socket serveur sur le port unicast configuré et accepte
     * les connexions clients en boucle. Pour chaque connexion acceptée, un nouveau
     * thread WestThread est créé et démarré pour gérer la communication avec le client.
     * <p>
     * La méthode s'exécute jusqu'à ce que le serveur soit arrêté via la variable {@code stop}.
     */
    @Override
    public void run() {
        try {
            listener = createServerSocket(config.unicastPort());
            isStarted = true;

            LOGGER.info(config.toString());
            LOGGER.info("Server listening on port {}", config.unicastPort());

            while (!stop) {
                Socket client = listener.accept();
                LOGGER.info("Accepted connection from {}", client.getInetAddress().getHostName());
                WestThread thread = new WestThread(client, config, taskThread,activeSession, new RandomService());
                thread.start();
                this.westThreads.add(thread);
            }

        } catch (IOException e) {
            LOGGER.error("Erreur lors de la connexion", e);
        }
    }

/* Méthode pour arrêter le message
    public void stopServer() {
        if (isStarted) {
            stop = true;
            this.westThreads.forEach(WestThread::stopThread);
            LOGGER.info("Serveur fermé");
            try {
                if (listener != null) {
                    listener.close();
                }
            } catch (IOException ex) {
                LOGGER.error("Une erreur s'est produite lors de l'arrêt du serveur", ex);
            }
            isStarted = false;
        }

    }
*/

    /**
     * Crée un socket serveur sur le port spécifié.
     * <p>
     * Cette méthode abstraite doit être implémentée par les classes dérivées pour
     * fournir l'implémentation spécifique de création de socket serveur (par exemple,
     * socket standard, socket SSL, etc.).
     *
     * @param port le numéro de port sur lequel le serveur doit écouter
     * @return le socket serveur créé et configuré
     * @throws ServerException si une erreur se produit lors de la création du socket serveur
     */
    abstract ServerSocket createServerSocket(int port) throws ServerException;
}