package org.helmo;


import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.UserReaderException;
import org.helmo.Exception.UserSaveException;
import org.helmo.client.Client;
import org.helmo.repository.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JsonUserRepository implements IUserRepository {
    private final Config config;

    // Verrou JVM unique pour serialiser toutes les lectures/ecritures users.json.
    private static final Object USERS_FILE_LOCK = new Object();

    private static final String FILE_NAME = "Node/users.json";
    private static final Gson gson = new Gson();
    private static final Path configPath = Paths.get(FILE_NAME);
    private static final Logger LOGGER = LogManager.getLogger();


    public JsonUserRepository(Config config){
        Objects.requireNonNull(config);
        this.config = config;
    }

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
                    return JsonMapper.mapUsers(dto, config.getCurrentDomain());
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
                UserDto newUserDto = JsonMapper.toUserDto(client);
                String newUserJson = gson.toJson(newUserDto);
                boolean updated = false;
                for (int i = 0; i < existingUserDtos.size(); i++) {
                    UserDto existingUserDto = existingUserDtos.get(i);
                    String existingUserJson = gson.toJson(existingUserDto);
                    if (existingUserJson.equals(newUserJson)) {
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
