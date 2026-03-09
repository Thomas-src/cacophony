package org.helmo;


import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.TagReaderException;
import org.helmo.Exception.TagSaveException;
import org.helmo.Exception.UserReaderException;
import org.helmo.client.Client;
import org.helmo.client.Tag;
import org.helmo.repository.*;
import org.helmo.services.UserManagerService;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class JsonTagRepository implements ITagRepository {
    private static final String FILE_NAME = "Node/tags.json";
    private static final Gson gson = new Gson();
    private static final Path configPath = Paths.get(FILE_NAME);
    private static final Logger LOGGER = LogManager.getLogger();

    // Verrou JVM unique pour serialiser toutes les lectures/ecritures tags.json.
    private static final Object TAGS_FILE_LOCK = new Object();

    private final UserManagerService userService;
    private final Config config;

    public JsonTagRepository(Config config, UserManagerService userService){
        Objects.requireNonNull(config);
        this.config = config;
        this.userService = userService;

    }

    @Override
    public List<Tag> getTags() throws TagReaderException {
        synchronized (TAGS_FILE_LOCK) {
            try {
                ensureStorage();
                try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                    TagsDto dto = gson.fromJson(reader, TagsDto.class);
                    if (dto == null) {
                        return List.of();
                    }

                    List<Client> clients = userService.getUsers();
                    Map<String, Client> userMap = clients.stream().collect(Collectors.toMap(Client::getName, u -> u));
                    return JsonMapper.mapTags(dto, userMap, config.getCurrentDomain());
                }
            } catch (IOException ex) {
                LOGGER.error("Impossible de charger le fichier tag", ex);
                throw new TagReaderException("Impossible de charger le fichier tag");
            } catch (JsonParseException ex) {
                LOGGER.error("Format JSON invalide pour le fichier tag", ex);
                throw new TagReaderException("Format JSON invalide pour le fichier tag");
            } catch (UserReaderException e) {
                LOGGER.error("Impossible de lire le fichier utilisateurs", e);
                throw new TagReaderException("Impossible de lire le fichier utilisateurs");
            }
        }
    }

    @Override
    public void saveTag(Tag tag) throws TagSaveException {
        synchronized (TAGS_FILE_LOCK) {
            try {
                ensureStorage();

                TagsDto existingDto;
                try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                    existingDto = gson.fromJson(reader, TagsDto.class);
                }

                List<TagDto> tagsDtos;
                if (existingDto == null || existingDto.tags() == null) {
                    tagsDtos = new ArrayList<>();
                } else {
                    tagsDtos = new ArrayList<>(existingDto.tags());
                }

                TagDto newTagDto = JsonMapper.toTagDto(tag);
                int existingIndex = tagsDtos.indexOf(newTagDto);
                if (existingIndex >= 0) {
                    tagsDtos.set(existingIndex, newTagDto);
                } else {
                    tagsDtos.add(newTagDto);
                }
                TagsDto dtoToSave = new TagsDto(tagsDtos);
                String json = gson.toJson(dtoToSave);
                Files.writeString(configPath, json);
            } catch (IOException ex) {
                LOGGER.error("Impossible de sauvegarder le fichier tag", ex);
                throw new TagSaveException("Impossible de sauvegarder le fichier tag");
            } catch (JsonParseException ex) {
                LOGGER.error("Format JSON invalide pour le fichier tag lors de la sauvegarde", ex);
                throw new TagSaveException("Format JSON invalide pour le fichier tag");
            }
        }
    }

    private void ensureStorage() throws IOException {
        if (Files.notExists(configPath)) {
            Path parent = configPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(configPath, gson.toJson(new TagsDto(List.of())));
        }
    }
}
