package org.helmo.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.UserSaveException;
import org.helmo.client.Client;
import org.helmo.repository.IUserRepository;
import org.helmo.Exception.UserReaderException;
import org.helmo.subscriber.IUserSubscriber;

import java.util.List;

/**
 * Service gérant les opérations liées aux utilisateurs.
 */
public class UserManagerService {
    private static final Logger LOGGER = LogManager.getLogger();

    private final IUserRepository userRepository;

    /**
     * Initialise le service avec le dépôt d'utilisateurs spécifié.
     *
     * @param userRepository Le dépôt d'utilisateurs à utiliser.
     */
    public UserManagerService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Récupère la liste de tous les utilisateurs.
     *
     * @return Une liste de clients.
     * @throws UserReaderException Si une erreur survient lors de la lecture.
     */
    public List<Client> getUsers() throws UserReaderException {
        return userRepository.getUsers();
    }

    /**
     * Récupère la liste des abonnés pour un utilisateur spécifique.
     *
     * @param username Le nom complet de l'utilisateur.
     * @return La liste des abonnés ou null si l'utilisateur n'est pas trouvé.
     * @throws UserReaderException Si une erreur survient lors de la lecture.
     */
    public List<IUserSubscriber> getUserSubscribers(String username) throws UserReaderException {
        return getUsers().stream()
                .filter(u -> u.getFullName().equals(username))
                .findFirst()
                .map(Client::getSubscribers)
                .orElse(null);
    }

    /**
     * Ajoute un abonné pour un utilisateur donné.
     *
     * @param client   Le client qui s'abonne.
     * @param username Le nom de l'utilisateur auquel s'abonner.
     * @throws UserSaveException Si une erreur survient lors de l'enregistrement.
     */
    public void addSubscribersFor(Client client, String username) throws UserSaveException {
        userRepository.addSubscribersFor(client, username);
    }

    /**
     * Ajoute un tag pour un client spécifique.
     *
     * @param client Le client concerné.
     * @param tag    Le tag à ajouter.
     * @throws UserSaveException Si une erreur survient lors de l'enregistrement.
     */
    public void addTagFor(Client client, String tag) throws UserSaveException {
        userRepository.addTagFor(client, tag);
    }

    /**
     * Recherche un utilisateur par son nom.
     *
     * @param username Le nom de l'utilisateur à rechercher.
     * @return Le client correspondant ou null s'il n'existe pas.
     * @throws UserReaderException Si une erreur survient lors de la lecture.
     */
    public Client findUser(String username) throws UserReaderException {
        List<Client> users = getUsers();
        for (Client user : users) {
            if (user.getName().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Enregistre un nouvel utilisateur ou met à jour un utilisateur existant.
     *
     * @param user Le client à sauvegarder.
     * @throws UserSaveException Si une erreur survient lors de la sauvegarde.
     */
    public void saveUser(Client user) throws UserSaveException {
        userRepository.saveUsers(user);
    }
}
