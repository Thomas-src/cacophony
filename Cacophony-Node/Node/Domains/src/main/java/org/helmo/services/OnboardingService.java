package org.helmo.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.OnboardingException;
import org.helmo.HashPassword;
import org.helmo.User;
import org.helmo.client.Client;
import org.helmo.repository.IUserRepository;
import org.helmo.repository.UserReaderException;
import org.helmo.repository.UserSaveException;
import org.helmo.views.IViewServer;

import java.util.List;

public class OnboardingService implements IOnboarding{
    private static final Logger LOGGER = LogManager.getLogger();

    private final static String ERROR_MESSAGE = "ERR";
    private final static String OK_MESSAGE = "OK";

    private final IViewServer view;
    private final IUserRepository userRepository;

    public OnboardingService(IViewServer viewServer,  IUserRepository userRepository) {
        this.view = viewServer;
        this.userRepository = userRepository;
    }

    @Override
    public String onNewOnboarding(String message) throws OnboardingException {
        LOGGER.info("New Onboarding request received");

        Client client = Client.newClientFromOnboarding(message);

        try {
            for (User user : userRepository.getUsers()) {
                if (user.getName().equals(client.getName())) {
                    LOGGER.info("User with name " + user.getName() + " already exists");
                    return ERROR_MESSAGE;
                }
            }
            HashPassword hpsw = new HashPassword(client.getHashPsw(), client.getRotation(), client.getSalt());
            User user = new User(client.getName(), hpsw , List.of());

            userRepository.saveUsers(user);
        } catch (UserReaderException | UserSaveException e) {
            throw new OnboardingException("Impossible d'inscrire l'utilisateur" + client.getName() + "\nException enfant => " + e);
        }
        LOGGER.info("Onboarding OK");
        return OK_MESSAGE;
    }
}
