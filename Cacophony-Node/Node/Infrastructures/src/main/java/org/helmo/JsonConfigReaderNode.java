package org.helmo;


import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.helmo.repository.ConfigReaderException;
import org.helmo.repository.ConfigRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;


public class JsonConfigReaderNode implements ConfigRepository {
    private static final String FILE_NAME = "Node/config-node.json";
    private final Gson gson;
    private final Path configPath;
    private static final Logger LOGGER = (Logger) LogManager.getLogger();
    public JsonConfigReaderNode() {
        this.gson = new Gson();
        this.configPath = Paths.get(FILE_NAME);
    }

    @Override
    public Config getConfig() throws ConfigReaderException {
        try (BufferedReader reader = Files.newBufferedReader(configPath)){
            ConfigDto dto = gson.fromJson(reader, ConfigDto.class);

            if (dto == null) {
                LOGGER.info("Configuration vide ou invalide");
                throw new ConfigReaderException("Configuration vide ou invalide");
            }

            return JsonMapper.fromDto(dto);
        }  catch (IOException ex) {
            LOGGER.severe("Impossible de charger la configuration\n"+ ex);
            throw new ConfigReaderException("Impossible de charger la configuration");
        }
    }
}
