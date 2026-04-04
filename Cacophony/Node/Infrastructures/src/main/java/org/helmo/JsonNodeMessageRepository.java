package org.helmo;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.MessageReaderException;
import org.helmo.Exception.MessageSaveException;
import org.helmo.dto.MessagesDto;
import org.helmo.dto.TagsDto;
import org.helmo.repository.IMessageRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class JsonNodeMessageRepository implements IMessageRepository {
    private static final Path messagePath = findMessageFile();
    private static final Object TAGS_FILE_LOCK = new Object();
    private static final Gson gson = new Gson();
    private static final Logger LOGGER = LogManager.getLogger();
    /**
     * Recherche le fichier de messages dans les répertoires parents probables.
     * @return Le chemin vers le fichier de messages trouvé.
     */
    private static Path findMessageFile() {
        String fileName = "messages.json";

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
        return Paths.get("../Node/messages.json");
    }


    @Override
    public void saveMessage(String message) throws MessageSaveException {
        synchronized (TAGS_FILE_LOCK) {
            try {
                ensureStorage();
                MessagesDto dto;
                try (BufferedReader reader = Files.newBufferedReader(messagePath)) {
                    dto = gson.fromJson(reader, MessagesDto.class);
                }

                List<String> messages = (dto == null || dto.messages() == null) ? new ArrayList<>() : new ArrayList<>(dto.messages());
                messages.add(message);

                MessagesDto updatedDto = new MessagesDto(messages);
                Files.writeString(messagePath, gson.toJson(updatedDto));

            } catch (IOException | JsonParseException ex) {
                LOGGER.error("Impossible de sauvegarder le message", ex);
                throw new MessageSaveException("Erreur lors de la sauvegarde du message");
            }
        }
    }

    @Override
    public List<String> getAllAndDeleteMessages() throws MessageReaderException {
        synchronized (TAGS_FILE_LOCK) {
            try {
                ensureStorage();
                try (BufferedReader reader = Files.newBufferedReader(messagePath)) {
                    MessagesDto dto = gson.fromJson(reader, MessagesDto.class);
                    if (dto == null) {
                        return List.of();
                    }
                    // delete messages
                    MessagesDto emptyDto = new MessagesDto(List.of());
                    Files.writeString(messagePath, gson.toJson(emptyDto));
                    return dto.messages();
                }
            } catch (IOException ex) {
                LOGGER.error("Impossible de charger les messages", ex);
                throw new MessageReaderException("Impossible de charger les messages");
            } catch (JsonParseException ex) {
                LOGGER.error("Format JSON invalide pour le fichier messages", ex);
                throw new MessageReaderException("Format JSON invalide pour le fichier messages");
            }
        }
    }

    private void ensureStorage() throws IOException {
        if (Files.notExists(messagePath)) {
            Path parent = messagePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(messagePath, gson.toJson(new TagsDto(List.of())));
        }
    }
}
