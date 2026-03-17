package org.helmo;

import org.helmo.exceptions.ConfigurationLoadException;
import org.helmo.exceptions.InvalidAdvertMessageException;
import org.helmo.exceptions.NetworkConnectionException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.helmo.network.SpeakerMulticastListener;

/**
 * Point d'entrée principal de l'application Speaker.
 * À compléter : initialisation, chargement config, démarrage listeners, gestion du cycle de vie.
 */
public class SpeakerApplication {
    public static void main(String[] args) {

        try {
            // Charger la configuration
            SpeakerAppConfig config = loadConfiguration();

            // Valider la configuration avant toute utilisation
            if (config == null || config.getMulticastAddress() == null || config.getMulticastPort() <= 0 || config.getNetworkInterface() == null) {
                throw new ConfigurationLoadException("The configuration is invalid. Please check the configuration file.");
            }

            // Initialiser la vue console
            SpeakerConsoleView consoleView = new SpeakerConsoleView();
            consoleView.displayConfiguration(
                config.getMulticastAddress(),
                config.getMulticastPort(),
                config.getNetworkInterface(),
                config.getConfiguredDomains().size()
            );
            consoleView.displayInitializationStatus("Services initialized successfully.");
            consoleView.log("Application started.");

            // Démarrer le listener UDP multicast
            SpeakerMulticastListener multicastListener = new SpeakerMulticastListener((advert, sourceIp) -> {
                if (advert == null) {
                    throw new InvalidAdvertMessageException("The received advert message is invalid.");
                }
                System.out.println("Advert received from " + sourceIp + ": " + advert);
            });

            Thread listenerThread = new Thread(() -> {
                try {
                    multicastListener.startListening();
                } catch (Exception e) {
                    throw new NetworkConnectionException("Error while starting the multicast listener.", e);
                }
            });
            listenerThread.start();

            // Gérer l'arrêt propre
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    multicastListener.stop();
                    listenerThread.join();
                    consoleView.displayShutdownMessage();
                } catch (InterruptedException e) {
                    System.err.println("Error during listener shutdown: " + e.getMessage());
                }
            }));
        } catch (ConfigurationLoadException | JsonSyntaxException | IllegalArgumentException e) {
            System.err.println("Error during application startup: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    private static SpeakerAppConfig loadConfiguration() {
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get("config-speaker.json")));
            Gson gson = new Gson();
            return gson.fromJson(jsonContent, SpeakerAppConfig.class);
        } catch (IOException e) {
            throw new ConfigurationLoadException("Error while loading the configuration: config-speaker.json", e);
        } catch (JsonSyntaxException e) {
            throw new ConfigurationLoadException("The configuration file contains invalid JSON syntax.", e);
        }
    }
}
