package org.helmo.server.threads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Config;
import org.helmo.Exception.MessageReaderException;
import org.helmo.Exception.MessageSaveException;
import org.helmo.crypto.CryptoProvider;
import org.helmo.crypto.exceptions.DecryptionException;
import org.helmo.repository.IMessageRepository;
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
import java.util.List;
import java.util.UUID;

/**
 * Gère la réception des messages provenant du Speaker.
 * Ce thread écoute sur un port spécifique, déchiffre les messages reçus
 * et les transforme en tâches pour le système.
 */
public class EastThread extends Thread {
    private static final Logger LOGGER = LogManager.getLogger();
    private boolean isConnected = false;
    private final Config config;
    private final TaskThread taskThread;
    private PrintWriter writer;
    private Socket speakerSocket;
    private final CryptoProvider cryptoService;
    private final IMessageRepository messageRepository;
    private Thread currentReaderThread;

    /**
     * Initialise une nouvelle instance de EastThread.
     *
     * @param config        La configuration du serveur.
     * @param taskThread    Le thread de gestion des tâches.
     * @param cryptoService Le service utilisé pour le déchiffrement.
     */
    public EastThread(Config config, TaskThread taskThread, CryptoProvider cryptoService, IMessageRepository messageRepository) {
        this.config = config;
        this.taskThread = taskThread;
        this.cryptoService = cryptoService;
        this.messageRepository = messageRepository;
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
                isConnected = true;
                closeCurrentConnection();
                this.speakerSocket = speaker;
                this.writer = new PrintWriter(speaker.getOutputStream(), true);
                sendOldMessages();
                currentReaderThread = new Thread(() -> read(speaker));
                currentReaderThread.setDaemon(true);
                currentReaderThread.setName("ET:R");
                currentReaderThread.start();

            }
        } catch (IOException e) {
            LOGGER.error("Erreur création socket: {}", e.getMessage());
        } finally {
            isConnected = false;
        }
    }

    /**
     * Ferme la connexion actuelle avec le Speaker et interrompt le thread de lecture associé.
     */
    private void closeCurrentConnection() {
        if (currentReaderThread != null && currentReaderThread.isAlive()) {
            currentReaderThread.interrupt();
        }
        if (speakerSocket != null && !speakerSocket.isClosed()) {
            try {
                speakerSocket.close();
            } catch (IOException e) {
                LOGGER.warn("Fermeture ancienne connexion : {}", e.getMessage());
            }
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
                    LOGGER.info("Longueur message reçu: {}", line.length());
                    try {
                        String messageDecrypted = cryptoService.decrypt(line);
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
        this.taskThread.addTask(new Task(id, TypeTask.POST, new SpeakerTask(config.currentDomain()), line));
    }

    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Envoie un message non-chiffré au Speaker connecté.
     *
     * @param cryptedMessage Le message à envoyer.
     */
    public void sendMessage(String cryptedMessage) {
        if (writer != null) {
            LOGGER.info("Envoi au speaker msg");
            writer.println(cryptedMessage);
        } else {
            try {

                LOGGER.warn("Sepaker absent, sauvegarde du msg");
                messageRepository.saveMessage(cryptedMessage);
            } catch (MessageSaveException e) {
                LOGGER.error("Impossible de sauvegarde les msg: {}", e.getMessage());
            }
        }
    }

    private void sendOldMessages() {
        try {
            List<String> oldMessage = messageRepository.getAllAndDeleteMessages();
            for (String msg : oldMessage) {
                sendMessage(msg);
            }
        } catch (MessageReaderException e) {
            LOGGER.error("Erreur lors de l'envoi des messages en attente : {}", e.getMessage());
        }
    }
}
