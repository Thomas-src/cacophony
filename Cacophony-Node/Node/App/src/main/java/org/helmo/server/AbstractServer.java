package org.helmo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Config;
import org.helmo.Exception.ServerException;
import org.helmo.server.threads.TaskThread;
import org.helmo.server.threads.WestThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractServer implements IServer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<WestThread> westThreads = new ArrayList<>();

    private final Config config;
    private final TaskThread taskThread;
    private boolean stop = false;
    private boolean isStarted = false;
    private ServerSocket listener;
    private ActiveSession activeSession;

    public AbstractServer(Config config, TaskThread taskThread, ActiveSession activeSession) {
        this.config = config;
        this.taskThread = taskThread;
        this.activeSession = activeSession;
    }


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
                WestThread thread = new WestThread(client, config, taskThread,activeSession);
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
    abstract ServerSocket createServerSocket(int port) throws ServerException;
}