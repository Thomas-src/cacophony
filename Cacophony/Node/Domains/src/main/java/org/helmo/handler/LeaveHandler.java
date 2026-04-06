package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.MessageBuilder;
import org.helmo.handler.utils.CommandHandler;
import org.helmo.task.ClientSession;
import org.helmo.task.TaskContext;

import java.util.List;

/**
 * Gère la commande de déconnexion d'un client.
 */
public class LeaveHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Traite la demande de départ du client en fermant sa session.
     *
     * @param args Les arguments de la commande (non utilisés ici).
     * @param task Le contexte de la tâche, doit être une instance de ClientSession.
     * @return Un message de confirmation "OK".
     * @throws HandlerException Si le contexte n'est pas une session client valide.
     */
    public String handle(List<String> args, TaskContext task) throws HandlerException {
        if (!(task instanceof ClientSession session)) {
            throw new HandlerException("Task leave nécessite une session client");
        }
        LOGGER.info("LEAVE reçu, {}", session.getUsername());
        session.closeSession();

        return MessageBuilder.buildOk();
    }
}
