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
            // Ensure the logs directory exists
            java.nio.file.Path logsDir = java.nio.file.Paths.get("logs");
            if (!java.nio.file.Files.exists(logsDir)) {
                java.nio.file.Files.createDirectories(logsDir);
            }

            // Generate a unique log file name with timestamp to avoid file lock issues
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

    public void displayConfiguration(String multicastAddr, int multicastPort, String networkInterface, int domainsConfigured) {
        String configMessage = "===== CONFIGURATION =====\n" +
                "Multicast address   : " + multicastAddr + "\n" +
                "Multicast port      : " + multicastPort + "\n" +
                "Network interface   : " + networkInterface + "\n" +
                "Configured domains  : " + domainsConfigured + "\n" +
                "============================";
        System.out.println(configMessage);
        LOGGER.info(configMessage);
    }
}
