package org.helmo.handler;

import org.apache.logging.log4j.*;
import org.helmo.Config;
import org.helmo.MessageBuilder;
import org.helmo.client.ClientSession;
import org.helmo.services.RandomService;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {
    private final static Logger LOGGER = LogManager.getLogger();

    private final Socket client;
    private final CommandMenu menu;
    private final Config config;

    public ClientHandler(Socket client, CommandMenu menu, Config config) {
        this.client = client;
        this.menu = menu;
        this.config = config;
    }

    @Override
    public void run() {

        try (
                BufferedReader fromClient = new BufferedReader(
                        new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));

                PrintWriter toClient = new PrintWriter(
                        new OutputStreamWriter(client.getOutputStream(), StandardCharsets.UTF_8), true)
        ) {

            String rdmString = RandomService.generateRandomString(22);

            String welcomeMessage =
                    MessageBuilder.buildWelcome(config.getCurrentDomain(), rdmString);

            toClient.println(welcomeMessage);

            ClientSession session = new ClientSession(rdmString);

            String line;

            while ((line = fromClient.readLine()) != null) {

                LOGGER.info("MESSAGE FROM {} => {}", client.getInetAddress(), line);

                String response = menu.execute(line, session);

                LOGGER.info("MESSAGE OUT {} => {}", client.getInetAddress(), response);

                toClient.println(response);
            }

        } catch (IOException e) {
            LOGGER.info("Client {} disconnected", client.getInetAddress());
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                LOGGER.error("Error closing client", e);
            }
        }
    }
}
