package org.helmo.handler.speaker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.Exception.TagReaderException;
import org.helmo.Exception.UserReaderException;
import org.helmo.Message;
import org.helmo.MessageBuilder;
import org.helmo.handler.CommandHandler;
import org.helmo.handler.SendHandler;
import org.helmo.handler.SubscribeHandler;
import org.helmo.server.ActiveSession;
import org.helmo.services.*;
import org.helmo.subscriber.IUserSubscriber;
import org.helmo.task.SpeakerTask;
import org.helmo.task.TaskContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gère la commande POST reçue d'un Speaker.
 */
public class PostHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private final ActiveSession activeSession;
    private final Set<String> processedIds = ConcurrentHashMap.newKeySet();

    private final SubscriberService subscriberService;
    private final MessageService messageService;
    private SendHandler sendHandler;
    private final UserManagerService userManagerService;
    private final TagManagerService tagManagerService;
    /**
     * Initialise le gestionnaire de POST.
     *
     * @param activeSession     La session active actuelle.
     * @param subscriberService Le service d'envoi de messages.
     * @param messageService    Le service d'abonnements.
     */
    public PostHandler(ActiveSession activeSession, SubscriberService subscriberService, MessageService messageService, UserManagerService userManagerService, TagManagerService tagManagerService) {
        this.activeSession = Objects.requireNonNull(activeSession, "activeSession is null");
        this.subscriberService = subscriberService;
        this.messageService = messageService;
        this.userManagerService = userManagerService;
        this.tagManagerService = tagManagerService;

    }

    @Override
    public String handle(List<String> args, TaskContext task) throws HandlerException {
        if (!(task instanceof SpeakerTask speakerTask)) {
            throw new HandlerException("POST requires a speaker context");
        }

        for (int i = 0; i < args.size(); i++) {
            LOGGER.info("[{}]: {}",i, args.get(i));
        }
        String idAtDomain = args.getFirst();
        if (!processedIds.add(idAtDomain)) {
            LOGGER.info("POST id dupliqué: {}", idAtDomain);
            return null;
        }

        LOGGER.info("Exécution du post: {}", idAtDomain);

        String message = args.getLast();
        var matcherMSG = Message.P_CMD_MSG.matcher(message);
        var matcherSUB = Message.P_CMD_MSG.matcher(message);
        if (matcherMSG.matches()) {
            for (int i = 1; i <= matcherMSG.groupCount(); i++) {
                args.add(matcherMSG.group(i));
            }
            doMsg(args );
        } else if (matcherSUB.matches()) {
            for (int i = 1; i <= matcherSUB.groupCount(); i++) {
                args.add(matcherSUB.group(i));
            }
            doSub(args);
        }
        return null;
    }


    /**
     * Traite la logique d'un message (MSG) extrait d'une commande POST.
     *
     * @param args La liste des arguments extraits du message.
     * @throws HandlerException Si une erreur survient lors du traitement ou de l'envoi.
     */
    private void doMsg(List<String> args) throws HandlerException {
        LOGGER.info("message");
        for (int i = 0; i < args.size(); i++) {
            LOGGER.info("{} - {}", i, args.get(i));
        }
        String fromUser = args.get(1);
        String toUser = args.get(4);
        String message = args.getLast();

        String toTag = args.get(6);

        // todo si user entrée === user sortie on arrête

        if (toUser != null && !toUser.isBlank()) {
            String localResponse = MessageBuilder.buildMsg(fromUser, message);
            activeSession.send(toUser, localResponse);
        } else if (toTag != null && !toTag.isBlank()) {

            // 1. vérifier tag interne

            // todo résoudre les abonnés du tag
            try {
                LOGGER.info("{} Envoie vers tags - {}", fromUser, toTag);
                List<IUserSubscriber> subs =  tagManagerService.getTagSubscriber(toTag);
            } catch (TagReaderException e) {
                throw new RuntimeException(e); // todo
            }


            // 1. vérifier tag exite
            // 2. vérifier tag possède l'utilisateur en tant qu'abonné
            // 3. essayer d'envoyer vers users abonnés locaux
            // 4. essayer d'envoyer vers users distants
            // si tag local, vérifier que l'utilisateur est abonné


          //  activeSession.send(fromUser, message);
        }

    }

    /**
     * Traite la logique d'un abonnement (SUB) extrait d'une commande POST.
     *
     * @param args La liste des arguments extraits du message.
     */
    private void doSub(List<String> args) {
        LOGGER.info("do sub");
        // todo réception du speaker ! au double newline du create msg de subscribe
    }
}
