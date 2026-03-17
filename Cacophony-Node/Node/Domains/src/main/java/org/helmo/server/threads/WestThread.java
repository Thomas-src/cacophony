package org.helmo.server.threads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Config;
import org.helmo.MessageBuilder;
import org.helmo.client.ClientSession;
import org.helmo.handler.CommandMenu;
import org.helmo.services.RandomService;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/*
Echange avec clients
 */
public class WestThread extends Thread {
    private static final Logger LOGGER = LogManager.getLogger();

    private final Socket socket;
    private final CommandMenu menu;
    private final Config config;

    private PrintWriter toClient;

    public WestThread(Socket socket, CommandMenu menu, Config config) {
        this.socket = socket;
        this.menu = menu;
        this.config = config;
        setName("WestThread " + socket.getRemoteSocketAddress() + ":" + socket.getLocalPort());
    }

    @Override
    public void run() {
        try (
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        ) {
            toClient = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            LOGGER.info("Client connecté : {}", socket.getInetAddress().getHostAddress());

            String randomChallenge = RandomService.generateRandomString(22);
            toClient.println(MessageBuilder.buildWelcome(config.getCurrentDomain(), randomChallenge));

            ClientSession session = new ClientSession(randomChallenge);
            session.setWestThread(this);
            String line;
            while (session.isShouldKeepOpen() && (line = fromClient.readLine()) != null) {
                LOGGER.info("MESSAGE FROM {} => {}", socket.getInetAddress(), line);
                String response = menu.execute(line, session);

                LOGGER.info("MESSAGE OUT {} => {}", socket.getInetAddress(), response);
                toClient.println(response);
            }

        } catch (IOException e) {
            LOGGER.info("Client {} disconnected", socket.getInetAddress());
        } finally {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                LOGGER.error("Erreur fermeture socket", e);
            }

            LOGGER.info("Fin de session pour {}", socket.getInetAddress());
        }
    }
    public synchronized void send(String message) {
        if (toClient != null && !socket.isClosed()) {
            toClient.print(message);
        }
    }

}
