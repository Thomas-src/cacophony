package org.helmo.handler;

import org.apache.logging.log4j.*;
import org.helmo.Exception.HandlerException;
import org.helmo.MessageBuilder;
import org.helmo.handler.utils.CommandHandler;
import org.helmo.task.ClientSession;
import org.helmo.task.TaskContext;

import java.util.List;

/**
 * Gère la réponse au défi (challenge) envoyé par le client pour l'authentification.
 */
public class ResponseChallengeHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Traite la réponse du client au défi de sécurité.
     * Vérifie si la réponse correspond au challenge attendu pour la session actuelle.
     *
     * @param args Liste d'arguments contenant la réponse du client.
     * @param task Le contexte de la tâche, doit être une instance de ClientSession.
     * @return Un message de succès (OK) ou d'erreur (ERROR) formaté.
     * @throws HandlerException Si le contexte n'est pas une session client valide.
     */
    @Override
    public String handle(List<String> args, TaskContext task) throws HandlerException {
        if (!(task instanceof ClientSession session)) {
            throw new HandlerException("Task response nécessite une session client");
        }

        if(session.isAuthenticated()) {
            LOGGER.error("Utilisateur déjà authentifié");
            LOGGER.error("Fin d'authentification (Echec)");
            return MessageBuilder.buildError();
        }
        LOGGER.info("Réponse challenge reçue : {}", args.getFirst());
        String clientResponse = args.getFirst();
        String challenge = session.getRandomString();

        if (session.verifyChallenge(clientResponse, challenge)) {
            LOGGER.info("Authentication réussie pour : {}", session.getUsername());
            if(session.setAuthenticate()) {
                session.send( MessageBuilder.buildOk());
                session.sendPastMessages();
                LOGGER.info("Fin authentification (Réussite)");
                return null;
            }
            LOGGER.error("Erreur lors de l'authentification");
            LOGGER.error("Fin d'authentification (Echec)");
            return  MessageBuilder.buildError();
        }

        LOGGER.warn("Erreur lors de l'authentification pour → {}", session.getUsername());
        LOGGER.error("Fin d'authentification (Echec)");
        return MessageBuilder.buildError();
    }
}