package org.helmo;


import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.UserReaderException;
import org.helmo.Exception.UserSaveException;
import org.helmo.client.Client;
import org.helmo.dto.UserDto;
import org.helmo.dto.UsersDto;
import org.helmo.mapper.JsonUserMapper;
import org.helmo.repository.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Implémentation de {@link IUserRepository} utilisant un fichier JSON pour le stockage persistant.
 * Cette classe gère la lecture et l'écriture des utilisateurs et de leurs métadonnées (abonnés, tags).
 */
public class JsonUserRepository implements IUserRepository {
    private final Config config;

    // Verrou JVM unique pour serialiser toutes les lectures/ecritures users.json.
    private static final Object USERS_FILE_LOCK = new Object();

    private static final String FILE_NAME = "Node/users.json";
    private static final Gson gson = new Gson();
    private static final Path configPath = Paths.get(FILE_NAME);
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Initialise une nouvelle instance du dépôt JSON.
     *
     * @param config La configuration du nœud pour la gestion des domaines.
     * @throws NullPointerException si la configuration est nulle.
     */
    public JsonUserRepository(Config config){
        Objects.requireNonNull(config);
        this.config = config;
    }

    /**
     * Récupère la liste de tous les utilisateurs stockés dans le fichier JSON.
     *
     * @return Une liste d'objets {@link Client}.
     * @throws UserReaderException Si le fichier est inaccessible ou corrompu.
     */
    @Override
    public List<Client> getUsers() throws UserReaderException {
        synchronized (USERS_FILE_LOCK) {
            try {
                ensureStorage();
                try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                    UsersDto dto = gson.fromJson(reader, UsersDto.class);
                    if (dto == null) {
                        return List.of();
                    }
                    return JsonUserMapper.mapUsers(dto, config.currentDomain());
                }
            } catch (IOException ex) {
                LOGGER.error("Impossible de charger le fichier users", ex);
                throw new UserReaderException("Impossible de charger le fichier users");
            } catch (JsonParseException ex) {
                LOGGER.error("Format JSON invalide pour le fichier users", ex);
                throw new UserReaderException("Format JSON invalide pour le fichier users");
            }
        }
    }

    @Override
    public void saveMessageForUser(String name, String message) throws UserSaveException {
        synchronized (USERS_FILE_LOCK) {
            try {
                ensureStorage();
                List<UserDto> existingUserDtos;

                // 1. Lire le fichier
                try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                    UsersDto existingDto = gson.fromJson(reader, UsersDto.class);
                    if (existingDto == null || existingDto.users() == null) {
                        existingUserDtos = new ArrayList<>();
                    } else {
                        existingUserDtos = new ArrayList<>(existingDto.users());
                    }
                }

                boolean found = false;

                // 2. Trouver l'utilisateur
                for (int i = 0; i < existingUserDtos.size(); i++) {
                    UserDto user = existingUserDtos.get(i);

                    if (user.login().equals(name)) {
                        List<String> messages = user.messages() != null ? new ArrayList<>(user.messages()) : new ArrayList<>();

                        // 3. Ajouter le message
                        messages.add(message);

                        // 4. Recréer le UserDto (record immuable)
                        UserDto updatedUser = new UserDto(
                                user.login(),
                                user.bcryptHash(),
                                user.bcryptRound(),
                                user.bcryptSalt(),
                                user.subscribers(),
                                user.userTags(),
                                messages,
                                user.subscriptionUsers()
                        );

                        existingUserDtos.set(i, updatedUser);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    LOGGER.error("Utilisateur non trouvé pour ajout message : {}", name);
                    return;
                }

                // 5. Réécrire le fichier
                UsersDto updatedDto = new UsersDto(existingUserDtos);
                String json = gson.toJson(updatedDto);
                Files.writeString(configPath, json);

            } catch (IOException | JsonParseException ex) {
                LOGGER.error("Erreur lors de l'ajout d'un message", ex);
                throw new UserSaveException("Erreur lors de l'ajout d'un message");
            }
        }
    }


    /**
     * Sauvegarde ou met à jour un utilisateur dans le fichier JSON.
     *
     * @param client L'utilisateur à sauvegarder.
     * @throws UserSaveException Si une erreur survient lors de l'écriture.
     */
    @Override
    public void saveUsers(Client client) throws UserSaveException {
        synchronized (USERS_FILE_LOCK) {
            try {
                ensureStorage();
                List<UserDto> existingUserDtos;
                try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                    UsersDto existingDto = gson.fromJson(reader, UsersDto.class);
                    if (existingDto == null || existingDto.users() == null) {
                        existingUserDtos = new ArrayList<>();
                    } else {
                        existingUserDtos = new ArrayList<>(existingDto.users());
                    }
                }
                UserDto newUserDto = JsonUserMapper.toUserDto(client);
                boolean updated = false;
                for (int i = 0; i < existingUserDtos.size(); i++) {
                    UserDto existingUserDto = existingUserDtos.get(i);
                    if (existingUserDto.login().equals(newUserDto.login())) {
                        existingUserDtos.set(i, newUserDto);
                        updated = true;
                        break;
                    }
                }
                if (!updated) {
                    existingUserDtos.add(newUserDto);
                }
                UsersDto dto = new UsersDto(existingUserDtos);
                String json = gson.toJson(dto);
                Files.writeString(configPath, json);
            } catch (IOException ex) {
                LOGGER.error("Impossible de sauvegarder le fichier users", ex);
                throw new UserSaveException("Impossible de sauvegarder le fichier users");
            } catch (JsonParseException ex) {
                LOGGER.error("Format JSON invalide pour le fichier users", ex);
                throw new UserSaveException("Format JSON invalide pour le fichier users");
            }
        }
    }

    /**
     * Ajoute un abonné à la liste des abonnés d'un utilisateur spécifique.
     *
     * @param client L'utilisateur qui reçoit un nouvel abonné.
     * @param sub    L'identifiant de l'abonné à ajouter.
     * @throws UserSaveException Si l'utilisateur n'est pas trouvé ou en cas d'erreur E/S.
     */
    @Override
    public void addSubscribersFor(Client client, String sub) throws UserSaveException {
        synchronized (USERS_FILE_LOCK) {
            try {
                ensureStorage();

                List<UserDto> existingUserDtos;

                // 1. Lire le fichier
                try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                    UsersDto existingDto = gson.fromJson(reader, UsersDto.class);
                    existingUserDtos = (existingDto != null && existingDto.users() != null) ? new ArrayList<>(existingDto.users()) : new ArrayList<>();
                }

                UserDto subscriberUser = null;
                UserDto subscribedToUser = null;

                // 2. Trouver les deux utilisateurs
                for (UserDto user : existingUserDtos) {
                    LOGGER.info("Checking {} for: {}", user.login(), client.getName());
                    if (user.login().equals(client.getName())) {
                        subscriberUser = user;
                    }
                    if (user.login().equals(sub)) {
                        subscribedToUser = user;
                    }
                }

                if (subscriberUser == null) {
                    LOGGER.error("Utilisateur abonné non trouvé : {}", client.getName());
                    return;
                }


                // 3. Mettre à jour la liste des subscriptions de Thomas
                List<String> subscriptions = subscriberUser.subscriptionUsers() != null ? new ArrayList<>(subscriberUser.subscriptionUsers()) : new ArrayList<>();
                if (!subscriptions.contains(sub)) {
                    subscriptions.add(sub);
                }

                UserDto updatedSubscriber = new UserDto(
                        subscriberUser.login(),
                        subscriberUser.bcryptHash(),
                        subscriberUser.bcryptRound(),
                        subscriberUser.bcryptSalt(),
                        subscriberUser.subscribers(),
                        subscriberUser.userTags(),
                        subscriberUser.messages(),
                        subscriptions
                );

                existingUserDtos.set(existingUserDtos.indexOf(subscriberUser), updatedSubscriber);
                if (subscribedToUser != null) {
                    // 4. Mettre à jour la liste des subscribers de Tom
                    List<String> subscribers = subscribedToUser.subscribers() != null ? new ArrayList<>(subscribedToUser.subscribers()) : new ArrayList<>();
                    if (!subscribers.contains(client.getName())) {
                        subscribers.add(client.getName());
                    }

                    UserDto updatedSubscribedTo = new UserDto(
                            subscribedToUser.login(),
                            subscribedToUser.bcryptHash(),
                            subscribedToUser.bcryptRound(),
                            subscribedToUser.bcryptSalt(),
                            subscribers,
                            subscribedToUser.userTags(),
                            subscribedToUser.messages(),
                            subscribedToUser.subscriptionUsers()
                    );

                    existingUserDtos.set(existingUserDtos.indexOf(subscribedToUser), updatedSubscribedTo);
                }
                // 5. Réécrire le fichier
                UsersDto updatedDto = new UsersDto(existingUserDtos);
                Files.writeString(configPath, gson.toJson(updatedDto));

            } catch (IOException | JsonParseException ex) {
                LOGGER.error("Erreur lors de l'ajout d'un subscriber", ex);
                throw new UserSaveException("Erreur lors de l'ajout d'un subscriber");
            }
        }
    }

    @Override
    public List<String> getUserMessagesAndEmpty(String name) throws UserReaderException {
        synchronized (USERS_FILE_LOCK) {
            try {
                ensureStorage();
                List<UserDto> existingUserDtos;

                // 1. Lire le fichier
                try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                    UsersDto existingDto = gson.fromJson(reader, UsersDto.class);
                    if (existingDto == null || existingDto.users() == null) {
                        return List.of();
                    }
                    existingUserDtos = new ArrayList<>(existingDto.users());
                }

                List<String> messagesToReturn = List.of();
                boolean found = false;

                // 2. Trouver l'utilisateur
                for (int i = 0; i < existingUserDtos.size(); i++) {
                    UserDto user = existingUserDtos.get(i);
                    if (user.login().equals(name)) {
                        // récupérer messages
                        List<String> messages = user.messages() != null ? new ArrayList<>(user.messages()) : new ArrayList<>();

                        messagesToReturn = new ArrayList<>(messages);

                        // 3. vider les messages
                        UserDto updatedUser = new UserDto(
                                user.login(),
                                user.bcryptHash(),
                                user.bcryptRound(),
                                user.bcryptSalt(),
                                user.subscribers(),
                                user.userTags(),
                                new ArrayList<>(),
                                user.subscriptionUsers()
                        );
                        existingUserDtos.set(i, updatedUser);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    LOGGER.error("Utilisateur non trouvé pour récupération messages : {}", name);
                    return List.of();
                }

                // 4. Réécrire le fichier (avec messages vidés)
                UsersDto updatedDto = new UsersDto(existingUserDtos);
                String json = gson.toJson(updatedDto);
                Files.writeString(configPath, json);

                // 5. Retourner les anciens messages
                return messagesToReturn;

            } catch (IOException | JsonParseException ex) {
                LOGGER.error("Erreur lors de la récupération des messages", ex);
                throw new UserReaderException("Erreur lors de la récupération des messages");
            }
        }
    }

    /**
     * Ajoute un tag à la liste des tags d'un utilisateur spécifique.
     *
     * @param tag    Le tag à ajouter.
     * @throws UserSaveException Si l'utilisateur n'est pas trouvé ou en cas d'erreur E/S.
     */
    @Override
    public void addTagFor(String username, String tag) throws UserSaveException {
        synchronized (USERS_FILE_LOCK) {
            try {
                ensureStorage();

                List<UserDto> existingUserDtos;

                // 1. Lire le fichier
                try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                    UsersDto existingDto = gson.fromJson(reader, UsersDto.class);
                    if (existingDto == null || existingDto.users() == null) {
                        existingUserDtos = new ArrayList<>();
                    } else {
                        existingUserDtos = new ArrayList<>(existingDto.users());
                    }
                }

                boolean found = false;

                // 2. Trouver le bon utilisateur
                for (int i = 0; i < existingUserDtos.size(); i++) {
                    UserDto user = existingUserDtos.get(i);

                    if (user.login().equals(username)) {

                        List<String> tags = user.userTags() != null
                                ? new ArrayList<>(user.userTags())
                                : new ArrayList<>();

                        // 3. Ajouter le tag s'il n'existe pas déjà
                        if (!tags.contains(tag)) {
                            tags.add(tag);
                        }

                        // 4. Recréer le UserDto (record immuable)
                        UserDto updatedUser = new UserDto(
                                user.login(),
                                user.bcryptHash(),
                                user.bcryptRound(),
                                user.bcryptSalt(),
                                user.subscribers(),
                                tags,
                                user.messages(),
                                user.subscriptionUsers()
                        );

                        existingUserDtos.set(i, updatedUser);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    LOGGER.error("Utilisateur non trouvé pour ajout tag : {}", username);
                    return;
                }

                // 5. Réécrire le fichier
                UsersDto updatedDto = new UsersDto(existingUserDtos);
                String json = gson.toJson(updatedDto);
                Files.writeString(configPath, json);

            } catch (IOException | JsonParseException ex) {
                LOGGER.error("Erreur lors de l'ajout d'un tag", ex);
                throw new UserSaveException("Erreur lors de l'ajout d'un tag");
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
            Files.writeString(configPath, gson.toJson(new UsersDto(List.of())));
        }
    }
}
