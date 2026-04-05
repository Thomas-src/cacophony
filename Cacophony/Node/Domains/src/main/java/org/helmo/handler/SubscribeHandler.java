package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.*;
import org.helmo.MessageBuilder;
import org.helmo.handler.utils.CommandHandler;
import org.helmo.services.*;
import org.helmo.task.ClientSession;
import org.helmo.task.TaskContext;

import java.util.List;

/**
 * Gère les commandes d'abonnement (SUBSCRIBE) pour les utilisateurs et les tags.
 */
public class SubscribeHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private final SubscriberService subscriberService;

    /**
     * Initialise le gestionnaire d'abonnement.
     */
    public SubscribeHandler(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @Override
    public String handle(List<String> args, TaskContext task) throws HandlerException {
        if (!(task instanceof ClientSession session)) {
            throw new HandlerException("Task subscribe nécessite une session client");
        }
        if (args.size() < 2) {
            throw new HandlerException("Demande invalide");
        }
        String response = MessageBuilder.buildError();
        boolean isUser = args.get(0) != null;
        String sub = isUser ? args.get(0) : args.get(2);
        String domain = isUser ? args.get(1) : args.get(3);


        if (domain.equalsIgnoreCase(session.getDomain())) {
            if (isUser) {
                response = subscriberService.userSubscribe(sub, session.getDomain(), session.getUserFullName());
            } else {
                response = subscriberService.tagSubscribe(sub, session.getDomain(), session.getUserFullName());
            }
        } else {
            subscriberService.sendToSpeaker(session, args);
        }

        return response;
    }


}
