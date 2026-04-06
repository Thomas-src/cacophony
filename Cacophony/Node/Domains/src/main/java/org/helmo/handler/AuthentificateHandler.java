package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.Exception.UserReaderException;
import org.helmo.MessageBuilder;
import org.helmo.client.Client;
import org.helmo.handler.utils.CommandHandler;
import org.helmo.task.ClientSession;
import org.helmo.services.UserManagerService;
import org.helmo.task.TaskContext;

import java.util.List;

/**
 * Gère la commande d'authentification d'un utilisateur.
 */
public class AuthentificateHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private final UserManagerService userManagerService;

    /**
     * Initialise le gestionnaire d'authentification.
     * @param userManagerService Le service de gestion des utilisateurs.
     */
    public AuthentificateHandler(UserManagerService userManagerService) {
        this.userManagerService = userManagerService;
    }

    @Override
    public String handle(List<String> args, TaskContext task) throws HandlerException {
        LOGGER.info("Nouvelle tentative d'authentification");
        if (!(task instanceof ClientSession session)) {
            throw new HandlerException("Task auth nécessite une session client");
        }
        if (session.isAuthenticated()) {
            LOGGER.error("Utilisateur déjà authentifié");
            LOGGER.error("Fin d'authentification (Echec)");
            return MessageBuilder.buildError();
        }
        String username = args.getFirst().trim();

        try {
            Client user = userManagerService.findUser(username);
            if(user == null) {
                return MessageBuilder.buildError();
            }

            session.setClient(user);
            String challange = MessageBuilder.buildChallenge(String.valueOf(user.getHashPassword().bcryptRound()), user.getHashPassword().bcryptSalt());
            LOGGER.error("Envoie du challange → {}", challange);
            return challange;
        } catch (UserReaderException e) {
            LOGGER.error("Erreur lors de la lecture des utilisateur : " + e.getMessage());
            LOGGER.error("Fin d'authentification (Echec)");
            throw new HandlerException(e.getMessage());
        }

    }
}
