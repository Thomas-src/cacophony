package org.helmo.server.threads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Config;
import org.helmo.server.ActiveSession;
import org.helmo.task.SpeakerTask;
import org.helmo.task.Task;
import org.helmo.task.TypeTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/*
Receptions des messages du speaker
 */
public class EastThread extends Thread {
    private static final Logger LOGGER = LogManager.getLogger();

    private final Config config;
    private final TaskThread taskThread;
    private final ActiveSession activeSession;

    public EastThread(Config config, TaskThread taskThread, ActiveSession activeSession) {
        this.config = config;
        this.taskThread = taskThread;
        this.activeSession = activeSession;
        setName("EastThread");
    }

    @Override
    public void run() {
        int relayPort = config.relayPort();

        try (ServerSocket serverSocket = new ServerSocket(relayPort)) {
            while (!Thread.currentThread().isInterrupted()) {
                Socket speaker = serverSocket.accept();
                LOGGER.info("EastThread démarré");
                // taskThread.addTask(createTask(line));
                Thread reader = new Thread(() -> read(speaker));
                reader.setDaemon(true);
                reader.start();
                // créer un thread de lecture par connexion

            }
        } catch (IOException e) {
            LOGGER.error("Erreur création socket: {}", e.getMessage());
        }
    }

    private void read(Socket socket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
            LOGGER.info("lecture du socket");
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    LOGGER.info("POST reçu: {}", line);
                    createTask(line);
                }
            }
        } catch (IOException e) {
            LOGGER.info("Speaker déconnecté");
        }
    }

    private void createTask(String line) {
        String id = UUID.randomUUID().toString();
        this.taskThread.addTask(new Task(id, TypeTask.POST, new SpeakerTask(config.currentDomain()), line));
    }

}
