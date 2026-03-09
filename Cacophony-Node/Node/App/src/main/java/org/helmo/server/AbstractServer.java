package org.helmo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Config;
import org.helmo.MessageBuilder;
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
            LOGGER.info("Server listening on port " + config.getUnicastPort());

            while (!stop) {
                client = listener.accept();
                isConnected = true;
                LOGGER.info("Accepted connection from " + client.getInetAddress().getHostName());

                BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));

                PrintWriter toClient = new PrintWriter(new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8), true);

                String welcomeMessage =  MessageBuilder.buildWelcome(config.getCurrentDomain(), RandomService.generateRandomString(22));
                toClient.println(welcomeMessage);

//                toClient.println("WELCOME "+ config.getCurrentDomain() +" 1234567890123456789012");

                String line;

                try {
                    while ((line = fromClient.readLine()) != null) {
                        LOGGER.info("MESSAGE FROM " + client.getInetAddress() + " => " + line);
                        String message = menu.execute(line);
                        LOGGER.info("MESSAGE OUT " + client.getInetAddress() + " => " + line);
                        toClient.println(message);
                    }
                } catch (IOException e) {
                    LOGGER.info("Client "+client.getInetAddress()+" s'est déconnecté");
                } finally {
                    try {
                        client.close();
                    } catch (IOException ex) {
                        LOGGER.error("Erreur lors de la fermeture du client", ex);
                    }
                    isConnected = false;
                }

                client.close();
                isConnected = false;
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

    abstract ServerSocket createServerSocket(int port) throws IOException;
}