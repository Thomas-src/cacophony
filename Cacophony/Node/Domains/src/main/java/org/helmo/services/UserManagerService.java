package org.helmo.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Config;
import org.helmo.Exception.UserReaderException;
import org.helmo.Exception.UserSaveException;
import org.helmo.Grammar;
import org.helmo.client.Client;
import org.helmo.crypto.CryptoProvider;
import org.helmo.crypto.exceptions.DecryptionException;
import org.helmo.crypto.exceptions.EncryptionException;
import org.helmo.repository.IUserRepository;
import org.helmo.subscriber.IUserSubscriber;

import java.util.List;
import java.util.Objects;

/**
 * Service gérant les opérations liées aux utilisateurs.
 */
public class UserManagerService {
    private static final Logger LOGGER = LogManager.getLogger();

    private final IUserRepository userRepository;
    private final CryptoProvider cryptoService;
    private final Config config;
    /**
     * Initialise le service avec le dépôt d'utilisateurs spécifié.
     *
     * @param userRepository Le dépôt d'utilisateurs à utiliser.
     */
    public UserManagerService(IUserRepository userRepository, CryptoProvider cryptoService, Config config) {
        this.userRepository = userRepository;
        this.cryptoService = cryptoService;
        this.config = config;
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
     * @param fullusername Le client qui s'abonne.
     * @param username     Le nom de l'utilisateur auquel s'abonner.
     * @throws UserSaveException Si une erreur survient lors de l'enregistrement.
     */
    public void addSubscribersFor(String fullusername, String username) throws UserSaveException {
        userRepository.addSubscribersFor(fullusername, username);
    }

    /**
     * Ajoute un tag pour un client spécifique.
     *
     * @param fulltagname Le tag à ajouter.
     * @throws UserSaveException Si une erreur survient lors de l'enregistrement.
     */
    public void addTagFor(String fullUsername, String fulltagname) throws UserSaveException {
        userRepository.addTagFor(fullUsername, fulltagname);
    }

    /**
     * Recherche un utilisateur par son nom.
     *
     * @param username Le nom de l'utilisateur à rechercher.
     * @return Le client correspondant ou null s'il n'existe pas.
     * @throws UserReaderException Si une erreur survient lors de la lecture.
     */
    public Client findUser(String username) throws UserReaderException {
        if(transformToDomain(username).equals(config.currentDomain()))  {
            List<Client> users = getUsers();
            for (Client user : users) {
                if (user.getFullName().equals(username)) {
                    return user;
                }
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

    public void saveMessageForUser(String username, String message) throws UserSaveException {
        try {
            String cryptedMessage = cryptoService.encrypt(message);
            String name = transformToName(username);
            userRepository.saveMessageForUser(name, cryptedMessage);
        } catch (EncryptionException e) {
            throw new UserSaveException("Impossible de crypter le message");
        }
    }

    public void addSubscribtionFor(String fullusername, String username) throws UserSaveException {
        userRepository.addUserSubscriptionFor(fullusername, username);
    }

    public List<String> getUserMessagesAndEmpty(String username) throws UserReaderException, UserSaveException {
        String name = transformToName(username);
        List<Client> users = getUsers();

        for (Client user : users) {
            if (user.getName().equals(name)) {

                return userRepository.getUserMessagesAndEmpty(name)
                        .stream()
                        .map(encryptedData -> {
                            try {
                                return cryptoService.decrypt(encryptedData);
                            } catch (DecryptionException ignored) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .toList();
            }
        }
        return List.of();
    }

    private String transformToName(String fullUsername) {
        String name = fullUsername;

        var matcher = Grammar.P_USER_AT_DOMAIN.matcher(fullUsername);
        if (matcher.matches()) {
            name = matcher.group(1);
        }
        return name;
    }

    private String transformToDomain(String fullUsername) {
        String name = fullUsername;

        var matcher = Grammar.P_USER_AT_DOMAIN.matcher(fullUsername);
        if (matcher.matches()) {
            name = matcher.group(2);
        }
        return name;
    }
}
