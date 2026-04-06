package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Config;
import org.helmo.Exception.HandlerException;
import org.helmo.Exception.OnboardingException;
import org.helmo.Grammar;
import org.helmo.MessageBuilder;
import org.helmo.client.Client;
import org.helmo.handler.utils.CommandHandler;
import org.helmo.task.ClientSession;
import org.helmo.client.HashPassword;
import org.helmo.Exception.UserReaderException;
import org.helmo.Exception.UserSaveException;
import org.helmo.services.UserManagerService;
import org.helmo.task.TaskContext;

import java.util.List;

/**
 * Gère le processus d'inscription (onboarding) des nouveaux utilisateurs.
 */
public class OnboardingHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private final UserManagerService userManagerService;
    private final Config config;
    /**
     * Initialise le gestionnaire d'inscription avec le service de gestion des utilisateurs.
     *
     * @param userManagerService
     */
    public OnboardingHandler(UserManagerService userManagerService, Config config) {
        this.userManagerService = userManagerService;
        this.config = config;
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
        LOGGER.info("Nouvelle tentative d'inscription");
        if (!(task instanceof ClientSession session)) {
            throw new HandlerException("Task onboarding nécessite une session client");
        }

        if (session.isAuthenticated()) {
            LOGGER.error("Utilisateur déjà authentifié");
            LOGGER.error("Inscription terminée (Echec)");
            return MessageBuilder.buildError();
        }
        String name = args.getFirst();

        String bcrypt = args.get(2);

        var matcher = Grammar.P_BCRYPT.matcher(bcrypt);

        if (!matcher.matches()) {
            throw new HandlerException("Format bcrypt invalide");
        }

        int rotation = Integer.parseInt(matcher.group(1));

        String saltAndHash = matcher.group(2);
        String salt = saltAndHash.substring(0, config.saltSizeInBytes());
        String hashpassword = saltAndHash.substring(config.saltSizeInBytes());

        String fullUsername = name + "@" + task.getDomain();
        try {
            if (userManagerService.findUser(fullUsername) != null) {
                return MessageBuilder.buildError();
            }
            Client client = new Client(name, new HashPassword(hashpassword, rotation, salt), session.getDomain());
            userManagerService.saveUser(client);
            session.setClient(client);

            if (session.setAuthenticate()) {
                session.send(MessageBuilder.buildOk());
                session.sendPastMessages();
                LOGGER.info("L'utilisateur {} a bien été inscrit", session.getUserFullName());
                LOGGER.info("Inscirption terminée (Réussite)");
                return null;
            } else {
                LOGGER.error("Inscription terminée (Echec)");
                return MessageBuilder.buildError();
            }
        } catch (UserReaderException | UserSaveException e) {
            LOGGER.error("Erreur lors de la lecture ou ecriture des/de l'utilisateur(s), ", e);
            LOGGER.error("Inscription terminée (Echec)");
            throw new HandlerException("Impossible d'inscrire l'utilisateur " + name + "\nException enfant => " + e);
        }

    }
}
