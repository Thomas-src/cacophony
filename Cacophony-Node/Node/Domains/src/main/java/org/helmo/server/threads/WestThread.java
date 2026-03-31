package org.helmo.server.threads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Config;
import org.helmo.Exception.HandlerException;
import org.helmo.MessageBuilder;
import org.helmo.server.ActiveSession;
import org.helmo.task.Task;
import org.helmo.task.TypeTask;
import org.helmo.task.ClientSession;
import org.helmo.services.RandomService;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;


/**
 * Gère les échanges réseau avec un client spécifique (Interface Ouest).
 * Cette classe lit les commandes entrantes, les transforme en tâches et envoie les réponses.
 */
public class WestThread extends Thread {
    private static final Logger LOGGER = LogManager.getLogger();

    private final Socket socket;
    private final Config config;
    private final TaskThread taskThread;
    private final ActiveSession activeSession;

    private PrintWriter toClient;

    /**
     * Initialise un nouveau thread de communication pour un client.
     *
     * @param socket        Le socket de connexion du client.
     * @param config        La configuration du serveur.
     * @param taskThread    Le thread de traitement des tâches.
     * @param activeSession Le gestionnaire des sessions actives.
     */
    public WestThread(Socket socket, Config config, TaskThread taskThread, ActiveSession activeSession) {
        this.socket = socket;
        this.config = config;
        this.taskThread = taskThread;
        this.activeSession = activeSession;
        setName("WestThread");
    }

    /**
     * Boucle principale du thread : gère l'accueil, la lecture des messages
     * et la fermeture de la connexion.
     */
    @Override
    public void run() {
        ClientSession session = null;
        try (BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
            toClient = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            LOGGER.info("Client connecté : {}", socket.getInetAddress().getHostAddress());

            String randomChallenge = RandomService.generateRandomString(22);
            toClient.println(MessageBuilder.buildWelcome(config.currentDomain(), randomChallenge));

            session = new ClientSession(randomChallenge, this, config.currentDomain(), activeSession);
            String line;
            while (session.isShouldKeepOpen() && (line = fromClient.readLine()) != null) {
                LOGGER.info("MESSAGE FROM {} => {}", socket.getInetAddress(), line);

                taskThread.addTask(createTask(line, session));
            }
        } catch (IllegalArgumentException e) {
            LOGGER.info("Commande inconnue", e);
            toClient.println(MessageBuilder.buildError());
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

            if (session != null) {
                activeSession.remove(session.getUserFullName());
            }
            LOGGER.info("Fin de session pour {}", socket.getInetAddress());
        }
    }

    /**
     * Génère un identifiant unique pour une tâche.
     *
     * @return Un UUID sous forme de chaîne de caractères.
     */
    private String creatId() {

        return UUID.randomUUID().toString();

    }

    /**
     * Analyse une ligne de texte pour créer la tâche correspondante.
     *
     * @param line    La ligne brute reçue du client.
     * @param session La session associée au client.
     * @return Une instance de Task prête à être traitée.
     */
    private Task createTask(String line, ClientSession session) {
        String input = line.trim();

        for (TypeTask type : TypeTask.values()) {
            Matcher matcher = type.getPattern().matcher(input);

            if (matcher.matches()) {

                return new Task(creatId(), type, session, line);
            }
        }
        throw new IllegalArgumentException("Commande inconnue : " + input);
    }


    /**
     * Envoie un message au client de manière synchronisée.
     *
     * @param message Le message à envoyer.
     */
    public synchronized void send(String message) {
        if (toClient != null && !socket.isClosed()) {
            toClient.println(message);
        }
    }

    /*
    public void stopThread() {
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.error("Erreur fermeture socket", e);
        }
    }
     */

}
