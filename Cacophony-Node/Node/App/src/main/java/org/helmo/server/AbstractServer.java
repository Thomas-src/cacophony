package org.helmo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Config;
import org.helmo.Exception.ServerException;
import org.helmo.handler.CommandMenu;
import org.helmo.server.threads.TaskThread;
import org.helmo.server.threads.WestThread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class AbstractServer implements IServer {
    private static final Logger LOGGER = LogManager.getLogger();

    private final Config config;
    private final TaskThread taskThread;
    private boolean stop = false;
    private boolean isStarted = false;
    private ServerSocket  listener;

    public AbstractServer(Config config,  TaskThread taskThread) {

        this.config = config;
        this.taskThread = taskThread;
    }


    @Override
    public void run() {
        try {
            listener = createServerSocket(config.getUnicastPort());
            isStarted = true;

            LOGGER.info(config.toString());
            LOGGER.info("Server listening on port {}", config.getUnicastPort());

            while (!stop) {
                Socket client = listener.accept();
                LOGGER.info("Accepted connection from {}", client.getInetAddress().getHostName());
                WestThread thread = new WestThread(client, config, taskThread);
                thread.start();
            }

        }catch (IOException e) {
            LOGGER.error("Erreur lors de la connexion", e);
        }
    }


    @Override
    public void stopServer() {
        if(isStarted) {
            stop = true;
            LOGGER.info("Serveur fermé");
            try {
                if (listener != null) {
                    listener.close();
                }
            } catch (IOException ex) {
                LOGGER.error("Une erreur s'est produite lors de l'arrêt du serveur",ex);
            }
            isStarted = false;
        }

    }

    abstract ServerSocket createServerSocket(int port) throws ServerException;
}