package org.helmo;


import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.ConfigReaderException;
import org.helmo.dto.ConfigDto;
import org.helmo.mapper.JsonConfigMapper;
import org.helmo.repository.IConfigReaderRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;


/**
 * Implémentation de IConfigReaderRepository pour la lecture de fichiers de configuration JSON.
 */
public class JsonConfigReaderNode implements IConfigReaderRepository {
    private static final Gson gson = new Gson();
    private static final Path configPath = findConfigFile();
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Recherche le fichier de configuration dans les répertoires parents probables.
     * @return Le chemin vers le fichier de configuration trouvé.
     */
    private static Path findConfigFile() {
        String fileName = "config-node.json";

        // Case 1: Running from 'Node' subdirectory (e.g., Gradle's 'run' task)
        Path pathFromNodeDir = Paths.get(fileName);
        if (Files.exists(pathFromNodeDir)) {
            return pathFromNodeDir;
        }

        // Case 2: Running from project root (e.g., IDE)
        Path pathFromRoot = Paths.get("Node", fileName);
        if (Files.exists(pathFromRoot)) {
            return pathFromRoot;
        }

        // Fallback to original path as a last resort
        return Paths.get("../Node/config-node.json");
    }

    /**
     * Lit et convertit le fichier JSON en objet Config.
     * @return L'objet de configuration.
     * @throws ConfigReaderException Si le fichier est introuvable ou invalide.
     */
    @Override
    public Config getConfig() throws ConfigReaderException {
        try (BufferedReader reader = Files.newBufferedReader(configPath)){
            ConfigDto dto = gson.fromJson(reader, ConfigDto.class);

            if (dto == null) {
                LOGGER.info("Configuration vide ou invalide");
                throw new ConfigReaderException("Configuration vide ou invalide");
            }

            // Valider l'interface réseau
            if (!isValidNetworkInterface(dto.networkInterface())) {
                LOGGER.error("L'interface réseau spécifiée '{}' n'est pas valide pour le multicast ou est inexistante.", dto.networkInterface());

                // Tenter de trouver une interface réseau appropriée
                String fallbackInterface = findSuitableNetworkInterface();
                if (fallbackInterface != null) {
                    LOGGER.info("Utilisation de l'interface réseau de secours : {}", fallbackInterface);
                    dto = new ConfigDto(
                        dto.currentDomain(),
                        dto.saltSizeInBytes(),
                        dto.multicastAddress(),
                        dto.multicastPort(),
                        dto.unicastPort(),
                        dto.relayPort(),
                        fallbackInterface,
                        dto.base64AES(),
                        dto.tls(),
                        dto.certificateName(),
                        dto.certificatePassword()
                    );
                } else {
                    throw new ConfigReaderException("L'interface réseau spécifiée n'est pas valide pour le multicast et aucune interface de secours n'a été trouvée.");
                }
            }

            return JsonConfigMapper.ConfigfromDto(dto);
        }  catch (IOException ex) {
            LOGGER.error("Impossible de charger la configuration. Tried path: " + configPath.toAbsolutePath(), ex);
            throw new ConfigReaderException("Impossible de charger la configuration");
        }
    }

    /**
     * Valide si l'interface réseau spécifiée est valide pour le multicast.
     *
     * @param networkInterface Le nom de l'interface réseau.
     * @return true si l'interface est valide, sinon false.
     */
    private static boolean isValidNetworkInterface(String networkInterface) {
        try {
            NetworkInterface netIf = NetworkInterface.getByName(networkInterface);
            return netIf != null && netIf.supportsMulticast() && !netIf.isLoopback() && !netIf.isVirtual();
        } catch (SocketException e) {
            LOGGER.warn("Erreur lors de la validation de l'interface réseau: {}", networkInterface, e);
            return false;
        }
    }

    /**
     * Tente de trouver une interface réseau valide pour le multicast si celle spécifiée est invalide.
     *
     * @return Le nom de l'interface réseau valide ou null si aucune n'est trouvée.
     */
    private static String findSuitableNetworkInterface() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface netIf = networkInterfaces.nextElement();
                if (netIf.supportsMulticast() && !netIf.isLoopback() && !netIf.isVirtual()) {
                    LOGGER.info("Interface réseau valide trouvée : {} ({})", netIf.getName(), netIf.getDisplayName());
                    return netIf.getName();
                }
            }
        } catch (SocketException e) {
            LOGGER.error("Erreur lors de la recherche d'une interface réseau valide", e);
        }
        LOGGER.warn("Aucune interface réseau valide trouvée pour le multicast.");
        return null;
    }
}
