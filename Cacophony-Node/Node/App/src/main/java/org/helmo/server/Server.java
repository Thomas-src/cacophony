package org.helmo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Config;
import org.helmo.Exception.OnboardingException;
import org.helmo.Exception.ConfigReaderException;
import org.helmo.MessageBuilder;
import org.helmo.repository.IConfigReaderRepository;
import org.helmo.services.IOnboarding;
import org.helmo.services.RandomService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server implements IServer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IConfigReaderRepository configReader;

    // Services
    private final IOnboarding onboardingService;
    private boolean stop = false;
    private boolean isStarted = false;
    private boolean isConnected = false;
    ServerSocket listener;
    Socket client;

    public Server(IOnboarding onboardingService, IConfigReaderRepository configReader) {
        this.onboardingService = onboardingService;
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

//                String welcomeMessage =  MessageBuilder.buildWelcome(config.getCurrentDomain(), RandomService.generateRandomString(22));
//                toClient.println(welcomeMessage);

                toClient.println("WELCOME "+ config.getCurrentDomain() +" 1234567890123456789012");

                String line;

                while ((line = fromClient.readLine()) != null) {
                    LOGGER.info("MESSAGE FROM " + client.getInetAddress() + " => " + line);

                    if (line.toUpperCase().startsWith("ONBOARDING")) {
                        String response = onboardingService.onNewOnboarding(line);
                        toClient.println(response);
                        LOGGER.info("Response to an onboarding from server to " + client.getInetAddress() + " => " + response);
                    }

                }

                client.close();
                isConnected = false;
            }

        } catch (IOException e) {
            throw new RuntimeException();
        } catch (ConfigReaderException e) {
            LOGGER.info("Impossible de lire la config, fin du serveur");
        } catch (OnboardingException e) {
            throw new RuntimeException(e);
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