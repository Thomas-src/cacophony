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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class JsonConfigReaderNode implements IConfigReaderRepository {
    private static final Gson gson = new Gson();
    private static final Path configPath = findConfigFile();
    private static final Logger LOGGER = LogManager.getLogger();

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

    @Override
    public Config getConfig() throws ConfigReaderException {
        try (BufferedReader reader = Files.newBufferedReader(configPath)){
            ConfigDto dto = gson.fromJson(reader, ConfigDto.class);

            if (dto == null) {
                LOGGER.info("Configuration vide ou invalide");
                throw new ConfigReaderException("Configuration vide ou invalide");
            }

            return JsonConfigMapper.ConfigfromDto(dto);
        }  catch (IOException ex) {
            LOGGER.error("Impossible de charger la configuration. Tried path: " + configPath.toAbsolutePath(), ex);
            throw new ConfigReaderException("Impossible de charger la configuration");
        }
    }
}
