package org.helmo;


import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.TagReaderException;
import org.helmo.Exception.TagSaveException;
import org.helmo.Exception.UserReaderException;
import org.helmo.client.Client;
import org.helmo.tag.InternalTag;
import org.helmo.dto.TagDto;
import org.helmo.dto.TagsDto;
import org.helmo.mapper.JsonTagMapper;
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

/**
 * Implémentation de ITagRepository stockant les tags dans un fichier JSON local.
 */
public class JsonTagRepository implements ITagRepository {
    private static final String FILE_NAME = "Node/tags.json";
    private static final Gson gson = new Gson();
    private static final Path configPath = Paths.get(FILE_NAME);
    private static final Logger LOGGER = LogManager.getLogger();

    // Verrou JVM unique pour serialiser toutes les lectures/ecritures tags.json.
    private static final Object TAGS_FILE_LOCK = new Object();

    private final UserManagerService userService;
    private final Config config;

    /**
     * Initialise une nouvelle instance de JsonTagRepository.
     *
     * @param config      La configuration de l'application.
     * @param userService Le service de gestion des utilisateurs.
     */
    public JsonTagRepository(Config config, UserManagerService userService){
        Objects.requireNonNull(config);
        this.config = config;
        this.userService = userService;
    }

    /**
     * Récupère la liste de tous les tags depuis le fichier JSON.
     *
     * @return Une liste d'InternalTag.
     * @throws TagReaderException Si une erreur survient lors de la lecture ou du parsing.
     */
    @Override
    public List<InternalTag> getTags() throws TagReaderException {
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
                    return JsonTagMapper.mapTags(dto, userMap, config.currentDomain());
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

    /**
     * Crée un nouveau tag et y abonne automatiquement son créateur.
     *
     * @param tag    Le nom du tag à créer.
     * @throws TagSaveException Si le tag ne peut pas être sauvegardé.
     */
    @Override
    public void createTag(String fullusername, String tag) throws TagSaveException {
        synchronized (TAGS_FILE_LOCK) {
            try {
                ensureStorage();

                List<TagDto> existingTagDtos;

                // 1. Lire le fichier
                try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                    TagsDto existingDto = gson.fromJson(reader, TagsDto.class);
                    if (existingDto == null || existingDto.tags() == null) {
                        existingTagDtos = new ArrayList<>();
                    } else {
                        existingTagDtos = new ArrayList<>(existingDto.tags());
                    }
                }

                // 2. Vérifier si le tag existe déjà
                for (TagDto tagDto : existingTagDtos) {
                    if (tagDto.tag().equals(tag)) {
                        LOGGER.error("Le tag existe déjà : {}", tag);
                        return;
                    }
                }

                // 3. Créer la liste de subscribers (le créateur est auto-abonné)
                List<String> subscribers = new ArrayList<>();
                subscribers.add(fullusername);

                // 4. Créer le TagDto (record immuable)
                TagDto newTag = new TagDto(tag, subscribers);

                // 5. Ajouter à la liste
                existingTagDtos.add(newTag);

                // 6. Réécrire le fichier
                TagsDto updatedDto = new TagsDto(existingTagDtos);
                String json = gson.toJson(updatedDto);
                Files.writeString(configPath, json);

            } catch (IOException | JsonParseException ex) {
                LOGGER.error("Erreur lors de la création du tag", ex);
                throw new TagSaveException("Erreur lors de la création du tag");
            }
        }
    }

    /**
     * Ajoute un utilisateur à la liste des abonnés d'un tag spécifique.
     *
     * @param userFullName Le client à ajouter.
     * @param tag    Le nom du tag.
     * @throws TagSaveException Si une erreur survient lors de la mise à jour du fichier.
     */
    @Override
    public void addUserToTag(String userFullName, String tag) throws TagSaveException {
        synchronized (TAGS_FILE_LOCK) {
            try {
                ensureStorage();

                List<TagDto> existingTagDtos;

                // 1. Lire le fichier
                try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                    TagsDto existingDto = gson.fromJson(reader, TagsDto.class);
                    if (existingDto == null || existingDto.tags() == null) {
                        existingTagDtos = new ArrayList<>();
                    } else {
                        existingTagDtos = new ArrayList<>(existingDto.tags());
                    }
                }

                boolean found = false;

                // 2. Trouver le bon tag
                for (int i = 0; i < existingTagDtos.size(); i++) {
                    TagDto tagDto = existingTagDtos.get(i);

                    if (tagDto.tag().equals(tag)) {

                        List<String> subscribers = tagDto.subscribers() != null
                                ? new ArrayList<>(tagDto.subscribers())
                                : new ArrayList<>();

                        // 3. Ajouter l'utilisateur s'il n'est pas déjà abonné
                        if (!subscribers.contains(userFullName)) {
                            subscribers.add(userFullName);
                        }

                        // 4. Recréer le TagDto (record immuable)
                        TagDto updatedTag = new TagDto(
                                tagDto.tag(),
                                subscribers
                        );

                        existingTagDtos.set(i, updatedTag);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    LOGGER.error("Tag non trouvé : {}", tag);
                    return;
                }

                // 5. Réécrire le fichier
                TagsDto updatedDto = new TagsDto(existingTagDtos);
                String json = gson.toJson(updatedDto);
                Files.writeString(configPath, json);

            } catch (IOException | JsonParseException ex) {
                LOGGER.error("Erreur lors de l'ajout d'un utilisateur au tag", ex);
                throw new TagSaveException("Erreur lors de l'ajout d'un utilisateur au tag");
            }
        }
    }

    /**
     * S'assure que le fichier de stockage et ses répertoires parents existent.
     *
     * @throws IOException Si la création du répertoire ou du fichier échoue.
     */
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
