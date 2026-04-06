package org.helmo.repository;

import org.helmo.Exception.MessageReaderException;
import org.helmo.Exception.MessageSaveException;

import java.util.List;

/**
 * Interface définissant les opérations de stockage pour les messages.
 */
public interface IMessageRepository {
    /**
     * Enregistre un nouveau message dans le dépôt.
     *
     * @param message Le contenu du message à sauvegarder.
     * @throws MessageSaveException Si une erreur survient lors de l'enregistrement.
     */
    void saveMessage(String message) throws MessageSaveException;

    /**
     * Récupère tous les messages stockés et les supprime du dépôt.
     *
     * @return Une liste contenant tous les messages récupérés.
     * @throws MessageReaderException Si une erreur survient lors de la lecture ou de la suppression.
     */
    List<String> getAllAndDeleteMessages() throws MessageReaderException;
}
