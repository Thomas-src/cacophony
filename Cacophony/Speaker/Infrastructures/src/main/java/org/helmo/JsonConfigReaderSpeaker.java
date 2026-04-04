package org.helmo;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.dto.ConfigDto;
import org.helmo.exceptions.ConfigReaderException;
import org.helmo.mapper.JsonConfigMapper;
import org.helmo.repository.IConfigReaderRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Implémentation de la lecture de configuration pour le Speaker à partir d'un fichier JSON.
 * Cette classe lit le fichier config-speaker.json et le convertit en objet Config.
 */
public class JsonConfigReaderSpeaker  implements IConfigReaderRepository {
    private static final Gson gson = new Gson();
    private static final Path configPath = findConfigFile();
    private static final Logger LOGGER = LogManager.getLogger();

    private static Path findConfigFile() {
        String fileName = "config-speaker.json";

        // Case 1: Running from 'Node' subdirectory (e.g., Gradle's 'run' task)
        Path pathFromNodeDir = Paths.get(fileName);
        if (Files.exists(pathFromNodeDir)) {
            return pathFromNodeDir;
        }

        // Case 2: Running from project root (e.g., IDE)
        Path pathFromRoot = Paths.get("Speaker", fileName);
        if (Files.exists(pathFromRoot)) {
            return pathFromRoot;
        }

        // Fallback to original path as a last resort
        return Paths.get("../Speaker/config-speaker.json");
    }

    /**
     * Récupère la configuration du Speaker.
     *
     * @return L'objet Config contenant l'adresse multicast, le port et l'interface réseau
     * @throws ConfigReaderException Si une erreur survient lors de la lecture de la configuration
     */
    @Override
    public SpeakerConfig getConfig() throws ConfigReaderException {
         try (BufferedReader reader = Files.newBufferedReader(configPath)){
            ConfigDto dto = gson.fromJson(reader, ConfigDto.class);

            if (dto == null) {
                throw new IOException("Configuration vide ou invalide");
            }

            return JsonConfigMapper.configfromDto(dto);

        }catch (IOException ex) {
            throw new ConfigReaderException("Impossible de charger la configuration. Tried path: " + configPath.toAbsolutePath(), ex);
        }
    }
}
