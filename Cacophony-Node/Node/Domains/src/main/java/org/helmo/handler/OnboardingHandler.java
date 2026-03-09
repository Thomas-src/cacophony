package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.Exception.OnboardingException;
import org.helmo.Grammar;
import org.helmo.client.Client;
import org.helmo.repository.IUserRepository;
import org.helmo.Exception.UserReaderException;
import org.helmo.Exception.UserSaveException;

import java.util.regex.Pattern;


public class OnboardingHandler implements CommandHandler{
    private static final Logger LOGGER = LogManager.getLogger();

    private final static String ERROR_MESSAGE = "ERR";
    private final static String OK_MESSAGE = "OK";
    private final IUserRepository userRepository;

    public OnboardingHandler(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Permet l'inscription ou non d'un nouvel utilisateur. Prend en paramètre la demande d'inscription, compare ce potentiel nouvel utilisateur avec ceux déjà enregistré, l'inscrit si c'est possbile
     * @param message
     * @return le message "OK" si l'inscription peut se faire, "ERR" sinon
     * @throws OnboardingException
     */
    @Override
    public String handle(String message) throws HandlerException {
        LOGGER.info("New Onboarding request received");

        Client client = Client.newClientFromOnboarding(message);

        try {
            for (Client user : userRepository.getUsers()) {
                if (user.getName().equals(client.getName())) {
                    LOGGER.info("User with name {} already exists", user.getName());
                    return ERROR_MESSAGE;
                }
            }

            userRepository.saveUsers(client);
        } catch (UserReaderException | UserSaveException e) {
            throw new HandlerException("Impossible d'inscrire l'utilisateur" + client.getName() + "\nException enfant => " + e);
        }

        LOGGER.info("Onboarding OK");
        return OK_MESSAGE;
    }

    @Override
    public Pattern getPattern() {
        return Grammar.P_USER_AT_DOMAIN;
    }
}
