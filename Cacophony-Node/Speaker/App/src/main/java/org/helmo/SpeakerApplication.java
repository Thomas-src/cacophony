package org.helmo;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.helmo.exceptions.ConfigurationLoadException;
import org.helmo.exceptions.NetworkConnectionException;
import org.helmo.network.SpeakerMulticastListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
            SpeakerMulticastListener multicastListener = new SpeakerMulticastListener(
                config.getMulticastAddress(),
                config.getMulticastPort(),
                config.getNetworkInterface(),
                (advert, sourceIp) -> {
                    if (advert == null) {
                        consoleView.log("Received null advert from " + sourceIp);
                        return;
                    }
                    consoleView.log("Advert received from " + sourceIp + ": " + advert);
                }
            );

            Thread listenerThread = new Thread(() -> {
                try {
                    multicastListener.startListening();
                } catch (IOException e) {
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
            // Construire les chemins possibles pour le fichier de configuration
            String basePath = System.getProperty("user.dir");
            String configPath = Paths.get(basePath, "Speaker", "config-speaker.json").toString();
            String alternativeConfigPath = Paths.get(basePath, "config-speaker.json").toString();

            // Vérifier si le fichier existe dans l'un des chemins
            String finalConfigPath;
            if (Files.exists(Paths.get(configPath))) {
                finalConfigPath = configPath;
            } else if (Files.exists(Paths.get(alternativeConfigPath))) {
                finalConfigPath = alternativeConfigPath;
            } else {
                throw new ConfigurationLoadException("Configuration file not found in expected locations.");
            }

            // Lire le contenu du fichier JSON
            String jsonContent = new String(Files.readAllBytes(Paths.get(finalConfigPath)));
            Gson gson = new Gson();
            return gson.fromJson(jsonContent, SpeakerAppConfig.class);
        } catch (IOException e) {
            throw new ConfigurationLoadException("Error while loading the configuration: Speaker/config-speaker.json", e);
        } catch (JsonSyntaxException e) {
            throw new ConfigurationLoadException("The configuration file contains invalid JSON syntax.", e);
        }
    }
}
