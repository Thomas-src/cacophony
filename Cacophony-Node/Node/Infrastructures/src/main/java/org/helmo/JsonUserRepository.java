package org.helmo;


import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.repository.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;


public class JsonUserRepository implements IUserRepository {
    private final Config config;

    private static final String FILE_NAME = "Node/users.json";
    private static final Gson gson = new Gson();
    private static final Path configPath = Paths.get(FILE_NAME);
    private static final Logger LOGGER = LogManager.getLogger();


    public JsonUserRepository(Config config){
        Objects.requireNonNull(config);
        this.config = config;
    }

    @Override
    public List<User> getUsers() throws UserReaderException {
        // todo si le fichier n'existe pas le créer
        try (BufferedReader reader = Files.newBufferedReader(configPath)){
            UsersDto dto = gson.fromJson(reader, UsersDto.class);

            if (dto == null) {
                LOGGER.info("Fichier users vide ou invalide");
                throw new UserReaderException("Fichier users vide ou invalide");
            }

            return JsonMapper.mapUsers(dto, config.getCurrentDomain());
        }  catch (IOException ex) {
            LOGGER.error("Impossible de charger le fichier users", ex);
            throw new UserReaderException("Impossible de charger le fichier users");
        }
    }

    @Override
    public void saveUsers(User user) throws UserSaveException {

    }
}
