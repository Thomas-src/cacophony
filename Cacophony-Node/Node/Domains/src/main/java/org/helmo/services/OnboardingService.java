package org.helmo.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.OnboardingException;
import org.helmo.client.Client;
import org.helmo.repository.IUserRepository;
import org.helmo.Exception.UserReaderException;
import org.helmo.Exception.UserSaveException;


public class OnboardingService implements IOnboarding{
    private static final Logger LOGGER = LogManager.getLogger();

    private final static String ERROR_MESSAGE = "ERR";
    private final static String OK_MESSAGE = "OK";
    private final IUserRepository userRepository;

    public OnboardingService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Permet l'inscription ou non d'un nouvel utilisateur. Prend en paramètre la demande d'inscription, compare ce potentiel nouvel utilisateur avec ceux déjà enregistré, l'inscrit si c'est possbile
     * @param message
     * @return le message "OK" si l'inscription peut se faire, "ERR" sinon
     * @throws OnboardingException
     */
    @Override
    public String onNewOnboarding(String message) throws OnboardingException {
        LOGGER.info("New Onboarding request received");

        Client client = Client.newClientFromOnboarding(message);

        // Ici j'ai découtert user apres avoir fait ma version de client donc j'ai utilisé un peu les deux, ce serait bien de merge les deux
        try {
            for (Client user : userRepository.getUsers()) {
                if (user.getName().equals(client.getName())) {
                    LOGGER.info("User with name {} already exists", user.getName());
                    return ERROR_MESSAGE;
                }
            }

            userRepository.saveUsers(client);
        } catch (UserReaderException | UserSaveException e) {
            throw new OnboardingException("Impossible d'inscrire l'utilisateur" + client.getName() + "\nException enfant => " + e);
        }

        LOGGER.info("Onboarding OK");
        return OK_MESSAGE;
    }
}
