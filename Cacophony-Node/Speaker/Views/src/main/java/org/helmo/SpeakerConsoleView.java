package org.helmo;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Vue console pour le Speaker : affichage des logs, états, messages reçus/envoyés.
 */
public class SpeakerConsoleView {
    private static final Logger LOGGER = Logger.getLogger(SpeakerConsoleView.class.getName());

    public SpeakerConsoleView() {
        configureLogger();
    }

    private void configureLogger() {
        try {
            // Créer le répertoire "logs" s'il n'existe pas pour éviter les problèmes de fichier verrouillé
            java.nio.file.Path logsDir = java.nio.file.Paths.get("logs");
            if (!java.nio.file.Files.exists(logsDir)) {
                java.nio.file.Files.createDirectories(logsDir);
            }

            // Utiliser un nom de fichier unique pour éviter les conflits de verrouillage
            String logFileName = "logs/speaker-" + System.currentTimeMillis() + ".log";
            System.out.println("Logger will write to: " + logFileName); // Debug message
            FileHandler fileHandler = new FileHandler(logFileName, true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setUseParentHandlers(false); // Désactiver les logs dans la console par défaut
        } catch (IOException e) {
            LOGGER.severe("Error configuring logger: " + e.getMessage());
        } catch (SecurityException e) {
            LOGGER.severe("Security exception while configuring logger: " + e.getMessage());
        }
    }

    public void displayInitializationStatus(String status) {
        String logMessage = "[SpeakerConsoleView] Status: " + status;
        System.out.println(logMessage);
        LOGGER.info(logMessage);
    }

    public void log(String message) {
        String logMessage = "[SpeakerConsoleView] Log: " + message;
        System.out.println(logMessage);
        LOGGER.info(logMessage);
    }

    public void displayShutdownMessage() {
        String logMessage = "[SpeakerConsoleView] Status: Listener stopped successfully.";
        System.out.println(logMessage);
        LOGGER.info(logMessage);
    }

    /**
     *
     * @param multicastAddr
     * @param multicastPort
     * @param networkInterface
     * @param domainsConfigured
     */
    public void displayConfiguration(String multicastAddr, int multicastPort, String networkInterface, int domainsConfigured) {
        // TODO: Améliorer la présentation de la configuration, par exemple en utilisant un format de tableau.
        String configMessage = "===== CONFIGURATION =====\n" +
                "Multicast address   : " + multicastAddr + "\n" +
                "Multicast port      : " + multicastPort + "\n" +
                "Network interface   : " + networkInterface + "\n" +
                "Configured domains  : " + domainsConfigured + "\n" +
                "============================";
        System.out.println(configMessage);
        LOGGER.info(configMessage);
    }

    // TODO: Ajouter des méthodes pour afficher les messages de manière plus structurée.
    // public void displayReceivedMessage(String from, String to, String content) { ... }
    // public void displaySentMessage(String to, String content) { ... }
}
