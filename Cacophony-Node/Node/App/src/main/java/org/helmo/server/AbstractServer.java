package org.helmo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Config;
import org.helmo.Exception.ServerException;
import org.helmo.MessageBuilder;
import org.helmo.client.ClientSession;
import org.helmo.handler.ClientHandler;
import org.helmo.handler.CommandMenu;
import org.helmo.services.RandomService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public abstract class AbstractServer implements IServer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Config config;

    // Services
    private final CommandMenu menu;
    private boolean stop = false;
    private boolean isStarted = false;
    private boolean isConnected = false;
    ServerSocket listener;
    Socket client;

    public AbstractServer(CommandMenu menu, Config config) {
        this.menu = menu;
        this.config = config;
    }


    @Override
    public void run() {
        try {
            listener = createServerSocket(config.getUnicastPort());
            isStarted = true;

            LOGGER.info(config.toString());
            LOGGER.info("Server listening on port {}", config.getUnicastPort());

            while (!stop) {
                client = listener.accept();
                isConnected = true;
                LOGGER.info("Accepted connection from {}", client.getInetAddress().getHostName());

                ClientHandler clientHandler = new ClientHandler(client, menu, config);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }

        }catch (IOException e) {
            LOGGER.error("Erreur lors de la connexion", e);
        }
    }


    @Override
    public void stopServer() {
        stop = true;
        try {
            if (listener != null) {
                listener.close();
            }
        } catch (IOException ex) {
            LOGGER.error("Une erreur s'est produite lors de l'arrêt du serveur",ex);
        }
    }

    abstract ServerSocket createServerSocket(int port) throws ServerException;
}