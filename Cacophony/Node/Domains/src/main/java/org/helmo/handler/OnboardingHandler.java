package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.Exception.OnboardingException;
import org.helmo.MessageBuilder;
import org.helmo.client.Client;
import org.helmo.task.ClientSession;
import org.helmo.client.HashPassword;
import org.helmo.Exception.UserReaderException;
import org.helmo.Exception.UserSaveException;
import org.helmo.services.UserManagerService;
import org.helmo.task.TaskContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Gère le processus d'inscription (onboarding) des nouveaux utilisateurs.
 */
public class OnboardingHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private final UserManagerService userManagerService;

    /**
     * Initialise le gestionnaire d'inscription avec le service de gestion des utilisateurs.
     *
     * @param userManagerService
     */
    public OnboardingHandler(UserManagerService userManagerService) {
        this.userManagerService = userManagerService;
    }

    /**
     * Permet l'inscription ou non d'un nouvel utilisateur. Prend en paramètre la demande d'inscription, compare ce potentiel nouvel utilisateur avec ceux déjà enregistré, l'inscrit si c'est possbile
     *
     * @param args Les arguments de la commande (nom, longueur du sel, hash complet)
     * @return le message "OK" si l'inscription peut se faire, "ERR" sinon (ex: utilisateur déjà existant)
     * @throws OnboardingException
     */
    @Override
    public String handle(List<String> args, TaskContext task) throws HandlerException {
        LOGGER.info("New Onboarding request received with args: {}", args);
        if (!(task instanceof ClientSession session)) {
            throw new HandlerException("Task onboarding nécessite une session client");
        }

        if (session.isAuthenticated()) {
            return MessageBuilder.buildError();
        }
        String name = args.getFirst();

        int rotation = Integer.parseInt(args.get(2).split("\\$")[2]); // TODO supprimer les split, extraire via les pattern REGEX
        var saltLength = Integer.parseInt(args.get(1));
        var salt = args.get(2).split("\\$")[3].substring(0, saltLength);
        var hashpassword = args.get(2).split("\\$")[3].substring(saltLength);

        try {
            if(userManagerService.findUser(name) != null) {
                return MessageBuilder.buildError();
            }
            Client client = new Client(name, new HashPassword(hashpassword, rotation, salt), new ArrayList<>(), session.getDomain(), new ArrayList<>(), new ArrayList<>());
            userManagerService.saveUser(client);
            session.setClient(client);

            if(session.setAuthenticate()) {
                session.send(MessageBuilder.buildOk());
                session.sendPastMessages();
                return null;
            } else {
                return MessageBuilder.buildError();
            }
        } catch (UserReaderException | UserSaveException e) {
            throw new HandlerException("Impossible d'inscrire l'utilisateur" + name + "\nException enfant => " + e);
        }

    }
}
