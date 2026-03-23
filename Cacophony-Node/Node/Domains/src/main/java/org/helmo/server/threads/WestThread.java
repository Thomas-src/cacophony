package org.helmo.server.threads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Config;
import org.helmo.Exception.HandlerException;
import org.helmo.MessageBuilder;
import org.helmo.client.ClientSession;
import org.helmo.services.RandomService;
import org.helmo.task.Task;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static org.helmo.task.TypeTask.SEND_MESSAGE;

/*
Echange avec clients
 */
public class WestThread extends Thread {
    private static final Logger LOGGER = LogManager.getLogger();

    private final Socket socket;
    private final Config config;
    private final TaskThread taskThread;

    private PrintWriter toClient;

    public WestThread(Socket socket, Config config, TaskThread taskThread) {
        this.socket = socket;
        this.config = config;
        this.taskThread = taskThread;
        setName("WestThread");
    }

    @Override
    public void run() {
        try (
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        ) {
            toClient = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            LOGGER.info("Client connecté : {}", socket.getInetAddress().getHostAddress());

            String randomChallenge = RandomService.generateRandomString(22);
            toClient.println(MessageBuilder.buildWelcome(config.currentDomain(), randomChallenge));

            ClientSession session = new ClientSession(randomChallenge, this, config.currentDomain());
            String line;
            while (session.isShouldKeepOpen() && (line = fromClient.readLine()) != null) {
                LOGGER.info("MESSAGE FROM {} => {}", socket.getInetAddress(), line);

                /*
                Ajouter id, type, Sender, receiver, Détails
                 */
                taskThread.addTask(new Task("1234", SEND_MESSAGE , session, "", line));
            }
        } catch (HandlerException e) {
            LOGGER.info("Une erreur s'est produite",e);
            toClient.println(MessageBuilder.buildError());
        }
         catch (IOException e) {
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
            toClient.println(message);
        }
    }

    public void stopThread() {
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.error("Erreur fermeture socket", e);
        }
    }
}
