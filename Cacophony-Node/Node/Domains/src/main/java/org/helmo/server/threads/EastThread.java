package org.helmo.server.threads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Config;
import org.helmo.crypto.CryptoService;
import org.helmo.crypto.exceptions.DecryptionException;
import org.helmo.server.ActiveSession;
import org.helmo.task.SpeakerTask;
import org.helmo.task.Task;
import org.helmo.task.TypeTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Gère la réception des messages provenant du Speaker.
 * Ce thread écoute sur un port spécifique, déchiffre les messages reçus
 * et les transforme en tâches pour le système.
 */
public class EastThread extends Thread {
    private static final Logger LOGGER = LogManager.getLogger();

    private final Config config;
    private final TaskThread taskThread;
    private final ActiveSession activeSession;
    private PrintWriter writer;
    private Socket speakerSocket;
    private final CryptoService cryptoService;

    /**
     * Initialise une nouvelle instance de EastThread.
     *
     * @param config        La configuration du serveur.
     * @param taskThread    Le thread de gestion des tâches.
     * @param activeSession La session active actuelle.
     * @param cryptoService Le service utilisé pour le déchiffrement.
     */
    public EastThread(Config config, TaskThread taskThread, ActiveSession activeSession, CryptoService cryptoService) {
        this.config = config;
        this.taskThread = taskThread;
        this.activeSession = activeSession;
        this.cryptoService = cryptoService;
        setName("EastThread");
    }

    /**
     * Démarre le serveur d'écoute pour le Speaker.
     * Accepte les connexions entrantes et lance un thread de lecture pour chaque socket.
     */
    @Override
    public void run() {
        int relayPort = config.relayPort();

        try (ServerSocket serverSocket = new ServerSocket(relayPort)) {
            while (!Thread.currentThread().isInterrupted()) {
                Socket speaker = serverSocket.accept();
                LOGGER.info("Speaker connecté");

                this.speakerSocket = speaker;
                this.writer = new PrintWriter(speaker.getOutputStream(), true);

                Thread reader = new Thread(() -> read(speaker));
                reader.setDaemon(true);
                reader.start();

            }
        } catch (IOException e) {
            LOGGER.error("Erreur création socket: {}", e.getMessage());
        }
    }

    /**
     * Lit les messages entrants sur le socket, les déchiffre et crée les tâches correspondantes.
     *
     * @param socket Le socket de communication avec le Speaker.
     */
    private void read(Socket socket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
            LOGGER.info("lecture du socket");
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    LOGGER.info("Message chiffré reçu: {}", line);
                    LOGGER.info("Longueur message reçu: {}", line.length());
                    try {
                        LOGGER.info("Msg avec clé: {}", config.base64AES());
                        String messageDecrypted = cryptoService.decrypt(line, config.base64AES());
                        LOGGER.info("Message déchiffré: {}", messageDecrypted);
                        createTask(messageDecrypted);
                    } catch (DecryptionException e) {
                        LOGGER.error("Échec du déchiffrement: {}", e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.info("Speaker déconnecté");
        }
    }

    /**
     * Crée une nouvelle tâche de type POST à partir du message déchiffré.
     *
     * @param line Le contenu du message déchiffré.
     */
    public void createTask(String line) {
        String id = UUID.randomUUID().toString();
        LOGGER.info("createTask: {}", line);
        this.taskThread.addTask(new Task(id, TypeTask.POST, new SpeakerTask(config.currentDomain()), line));
    }

    /**
     * Envoie un message non-chiffré au Speaker connecté.
     *
     * @param contextstr Le message à envoyer.
     */
    public void sendUncryptedMessage(String contextstr) {
        if (writer != null) {
            LOGGER.info("Envoi au speaker : {}", contextstr);
            writer.println(contextstr);
        } else {
            LOGGER.warn("Aucune connexion au speaker !");
        }
    }
}
