package org.helmo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.services.IOnboarding;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class Server implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger();

    // Services
    IOnboarding onboardingService;

    private static final int PORT = 5002; // 130013 ?
    private boolean stop = false;
    private boolean isSarted = false;
    private boolean isConnected = false;
    ServerSocket listener;
    Socket client;

    public Server(IOnboarding onboardingService) {
        this.onboardingService = onboardingService;
    }


    @Override
    public void run() {
        try {
            listener = new ServerSocket(PORT);
            isSarted = true;

            LOGGER.info("Server listening on port " + PORT);

            while (!stop) {
                client = listener.accept();
                isConnected = true;
                LOGGER.info("Accepted connection from " + client.getInetAddress().getHostName());

                BufferedReader fromClient = new BufferedReader(
                        new InputStreamReader(client.getInputStream(), Charset.forName("UTF-8")));

                PrintWriter toClient = new PrintWriter(
                        new OutputStreamWriter(client.getOutputStream(), Charset.forName("UTF-8")), true);

                toClient.println("WELCOME node1.swilabus.xyz 1234567890123456789012");

                String line;

                while ((line = fromClient.readLine()) != null) {
                    LOGGER.info("MESSAGE - " + line);

                    if (line.toLowerCase().startsWith("ONBOARDING")) {
                        onboardingService.onNewOnboarding(line);
                    }

                }

                client.close();
                isConnected = false;
            }

        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

}