package org.helmo.handler.speaker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.Exception.MessageSaveException;
import org.helmo.Exception.TagReaderException;
import org.helmo.Exception.UserReaderException;
import org.helmo.Message;
import org.helmo.MessageBuilder;
import org.helmo.crypto.exceptions.EncryptionException;
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

    private final SubscriberService subscriberService;
    private final MessageService messageService;
    private SendHandler sendHandler;
    private final UserManagerService userManagerService;
    private final TagManagerService tagManagerService;
    private final SpeakerService speakerService;

    /**
     * Initialise le gestionnaire de POST.
     *
     * @param activeSession     La session active actuelle.
     * @param subscriberService Le service d'envoi de messages.
     * @param messageService    Le service d'abonnements.
     */
    public PostHandler(ActiveSession activeSession, SubscriberService subscriberService, MessageService messageService, UserManagerService userManagerService, TagManagerService tagManagerService, SpeakerService speakerService) {
        this.activeSession = Objects.requireNonNull(activeSession, "activeSession is null");
        this.subscriberService = subscriberService;
        this.messageService = messageService;
        this.userManagerService = userManagerService;
        this.tagManagerService = tagManagerService;
        this.speakerService = speakerService;

    }

    @Override
    public String handle(List<String> args, TaskContext task) throws HandlerException {
        if (!(task instanceof SpeakerTask speakerTask)) {
            throw new HandlerException("POST requires a speaker context");
        }
        for (int i = 0; i < args.size(); i++) {
            LOGGER.info("Args {}: {}", i, args.get(i));
        }
        String idAtDomain = args.get(0);
        String fromUserAtDomain = args.get(1);
        String toFullAddress = args.get(4);  // user_at_domain OU tag_at_domain
        String toUser = args.get(5);  // null si tag
        String toTag = args.get(7);  // null si user
        String innerMessage = args.get(9);  // "MSG ..."

        LOGGER.info("POST reçu: {} → {}", fromUserAtDomain, toFullAddress);

        var matcherMSG = Message.P_CMD_MSG.matcher(innerMessage);
        if (matcherMSG.matches()) {
            String msgFrom = matcherMSG.group(1);
            String msgToSend = matcherMSG.group(0);
            if (toUser != null && !toUser.isBlank()) {
                doMsg(msgFrom, toFullAddress, msgToSend);
            } else if (toTag != null && !toTag.isBlank()) {
                doTag(msgFrom, toFullAddress, msgToSend, idAtDomain);
            }
        }
        // todo matcher SUB ici si

        return null;
    }

    private void doMsg(String fromUser, String toUser, String msgToSend) throws HandlerException {
        LOGGER.info("Envoi vers utilisateur: {} → {}", fromUser, toUser);
        activeSession.send(toUser, msgToSend);
    }

    private void doTag(String fromUser, String toTag, String msgToSend, String id) throws HandlerException {
        LOGGER.info("doTag: {} → {}", fromUser, toTag);
        try {
            List<IUserSubscriber> subs = tagManagerService.getTagSubscriber(toTag);
            for (IUserSubscriber subscriber : subs) {
                LOGGER.info(subscriber.getFullName());
            }
            if (subs.stream().anyMatch(sub -> sub.getFullName().equals(fromUser))) { // 1. vérifier si l'envoyeur est abonné au tag
                for (IUserSubscriber sub : subs) {
                    if (!sub.getFullName().equals(fromUser)) { // todo supprimer pour ne pas envoyer à l'expéditeur

                        if (sub.isLocal()) {
                            LOGGER.info("Response from remote tag (internal user) : {}", msgToSend);
                            activeSession.send(sub.getFullName(), msgToSend);
                        } else {
                            LOGGER.info("Resend msg for {} to speaker {}", sub.getFullName(), msgToSend);
                            String messageToCrypt = MessageBuilder.buildPost(id, fromUser, sub.getFullName(), msgToSend);
                            LOGGER.info("Resend post to speaker {}", messageToCrypt);
                            try {
                                speakerService.sendCryptedMessage(messageToCrypt); // todo envoyer vers le speaker
                            } catch (MessageSaveException e) {
                                LOGGER.error("Error sending Crypted Message", e);
                                throw new HandlerException("Impossible de sauvegarder le message");
                            } catch (EncryptionException e) {
                                LOGGER.error("Error crypting Message", e);
                                throw new HandlerException("Impossible de crypter le message");
                            }
                        }
                    }
                }
            } else {
                LOGGER.info("{} n'est pas abonné au tag {}", fromUser, toTag);
            }
        } catch (TagReaderException e) {
            throw new HandlerException("Erreur lecture tag", e);
        }
    }
}
