package org.helmo.repository;

import org.helmo.Exception.UserReaderException;
import org.helmo.Exception.UserSaveException;
import org.helmo.client.Client;

import java.util.List;

/**
 * Interface définissant les opérations de persistance pour les utilisateurs (clients).
 */
public interface IUserRepository {
    /**
     * Récupère la liste de tous les utilisateurs enregistrés.
     *
     * @return Une liste d'objets Client.
     * @throws UserReaderException Si une erreur survient lors de la lecture des données.
     */
    List<Client> getUsers() throws UserReaderException;
    void saveMessageForUser(String name, String message) throws UserSaveException;
    /**
     * Enregistre ou met à jour un utilisateur dans le dépôt.
     *
     * @param client L'utilisateur à sauvegarder.
     * @throws UserSaveException Si une erreur survient lors de la sauvegarde.
     */
    void saveUsers(Client client) throws UserSaveException;

    /**
     * Ajoute un abonné à un utilisateur spécifique.
     *
     * @param name L'utilisateur qui reçoit un nouvel abonné.
     * @param sub  Le nom de l'abonné à ajouter.
     * @throws UserSaveException Si l'opération de mise à jour échoue.
     */
    void addSubscribersFor(Client name, String sub) throws UserSaveException;

    List<String> getUserMessagesAndEmpty(String name) throws UserReaderException, UserSaveException;

    /**
     * Ajoute un tag (centre d'intérêt) à un utilisateur.
     *
     * @param client L'utilisateur concerné.
     * @param tag    Le tag à ajouter.
     * @throws UserSaveException Si l'opération de mise à jour échoue.
     */
    void addTagFor(Client client, String tag)throws UserSaveException;
}
