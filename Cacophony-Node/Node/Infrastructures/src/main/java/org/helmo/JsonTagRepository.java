package org.helmo;


import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.repository.*;
import org.helmo.services.UserManagerService;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


public class JsonTagRepository implements ITagRepository {
    private static final String FILE_NAME = "Node/tags.json";
    private static final Gson gson = new Gson();
    private static final Path configPath = Paths.get(FILE_NAME);
    private static final Logger LOGGER = LogManager.getLogger();

    private final UserManagerService userService;
    private final Config config;

    public JsonTagRepository(Config config, UserManagerService userService){
        Objects.requireNonNull(config);
        this.config = config;
        this.userService = userService;

    }

    @Override
    public List<Tag> getTags() throws TagReaderException {
        // todo si le fichier n'existe pas le créer
        try (BufferedReader reader = Files.newBufferedReader(configPath)){
            TagsDto dto = gson.fromJson(reader, TagsDto.class);

            if (dto == null) {
                LOGGER.info("Fichier tag vide ou invalide");
                throw new TagReaderException("Fichier tag vide ou invalide");
            }

            List<User> users = userService.getUsers(); // récupérer tous les users
            Map<String, User> userMap = users.stream().collect(Collectors.toMap(User::getName, u -> u));

            return JsonMapper.mapTags(dto, userMap, config.getCurrentDomain());
        }  catch (IOException ex) {
            LOGGER.error("Impossible de charger le fichier tag", ex);
            throw new TagReaderException("Impossible de charger le fichier tag");
        } catch (UserReaderException e) {
            LOGGER.error("Impossible de lire le fichier utilisateurs", e);
            throw new TagReaderException("Impossible de lire le fichier utilisateurs");
        }
    }

    @Override
    public void saveTag(Tag tag) throws TagSaveException {

    }

}
