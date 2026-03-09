package org.helmo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Config;
import org.helmo.Exception.ConfigReaderException;
import org.helmo.MessageBuilder;
import org.helmo.handler.CommandMenu;
import org.helmo.handler.OnboardingHandler;
import org.helmo.repository.IConfigReaderRepository;
import org.helmo.services.RandomService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server implements IServer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IConfigReaderRepository configReader;

    // Services
    private final CommandMenu menu;
    private boolean stop = false;
    private boolean isStarted = false;
    private boolean isConnected = false;
    ServerSocket listener;
    Socket client;

    public Server(CommandMenu menu, IConfigReaderRepository configReader) {
        this.menu = menu;
        this.configReader = configReader;
    }


    @Override
    public void run() {
        try {
            Config config = configReader.getConfig();
            listener = new ServerSocket(config.getMulticastPort());
            isStarted = true;

            LOGGER.info(config.toString());
            LOGGER.info("Server listening on port " + config.getMulticastPort());

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

                while ((line = fromClient.readLine()) != null) {
                    LOGGER.info("MESSAGE FROM " + client.getInetAddress() + " => " + line);
                    menu.execute(line);
                }

                client.close();
                isConnected = false;
            }

        } catch (IOException e) {
            throw new RuntimeException();
        } catch (ConfigReaderException e) {
            LOGGER.info("Impossible de lire la config, fin du serveur");
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
}