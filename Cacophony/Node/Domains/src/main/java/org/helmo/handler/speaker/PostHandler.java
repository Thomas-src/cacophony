package org.helmo.handler.speaker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.Exception.MessageSaveException;
import org.helmo.Exception.TagReaderException;
import org.helmo.Exception.UserReaderException;
import org.helmo.Message;
import org.helmo.MessageBuilder;
import org.helmo.client.Client;
import org.helmo.crypto.exceptions.EncryptionException;
import org.helmo.handler.utils.CommandHandler;
import org.helmo.server.ActiveSession;
import org.helmo.services.*;
import org.helmo.services.handler.MessageService;
import org.helmo.services.handler.SubscriberService;
import org.helmo.subscriber.IUserSubscriber;
import org.helmo.tag.ITag;
import org.helmo.task.SpeakerTask;
import org.helmo.task.TaskContext;

import java.util.*;

/**
 * Gère la commande POST reçue d'un Speaker.
 */
public class PostHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private final ActiveSession activeSession;

    private final LinkedList<String> ids = new LinkedList<>();
    private final SubscriberService subscriberService;
    private final TagManagerService tagManagerService;
    private final SpeakerService speakerService;
    private final UserManagerService userManagerService;
    private final MessageService messageService;

    /**
     * Initialise le gestionnaire de POST.
     *
     * @param activeSession     La session active actuelle.
     * @param subscriberService Le service d'envoi de messages.
     */
    public PostHandler(ActiveSession activeSession, SubscriberService subscriberService, TagManagerService tagManagerService, SpeakerService speakerService, UserManagerService userManagerService, MessageService messageService) {
        this.activeSession = Objects.requireNonNull(activeSession, "activeSession is null");
        this.subscriberService = subscriberService;
        this.tagManagerService = tagManagerService;
        this.speakerService = speakerService;
        this.userManagerService = userManagerService;
        this.messageService = messageService;
    }

    @Override
    public String handle(List<String> args, TaskContext task) throws HandlerException {
        if (!(task instanceof SpeakerTask speakerTask)) {
            throw new HandlerException("POST requires a speaker context");
        }
        String idAtDomain = args.get(0);
        String fromUserAtDomain = args.get(1);

        String userAddress = args.get(4) + "@" + args.get(5);

        String tagAddress = args.get(6) + "@" + args.get(7);
        String toFullAddress = args.get(4) != null ? userAddress : tagAddress;

        String toUser = args.get(5);  // null si tag
        String toTag = args.get(7);  // null si user
        String innerMessage = args.get(8);  // "MSG ..."

        LOGGER.info("POST reçu: {} → {}", fromUserAtDomain, toFullAddress);

        var matcherMSG = Message.P_INNER_CMD_MSG.matcher(innerMessage);
        if (matcherMSG.matches()) {
            String msgFrom = matcherMSG.group(1);
            String msgToSend = matcherMSG.group(0);

            String msgContent = matcherMSG.group(matcherMSG.groupCount());
            for (int i = 0; i < matcherMSG.groupCount(); i++) {
                LOGGER.info("{} - {} ", i, msgContent);
            }
            try {
                List<ITag> msgTags = messageService.extractTags(msgContent, task.getDomain());
                for (ITag tag : msgTags) {
                    LOGGER.info(tag.toString());
                }
                if (toUser != null && !toUser.isBlank()) {
                    doMsg(msgFrom, toFullAddress, msgToSend);
                } else if (toTag != null && !toTag.isBlank()) {
                    doTag(msgFrom, toFullAddress, msgToSend, idAtDomain, msgTags, task.getDomain());
                }

            } catch (TagReaderException e) {
                LOGGER.error("Impossible de lire les tags du message", e);
                throw new HandlerException(e.getMessage());
            }
        } else {
            LOGGER.info("ne matche pas msg");
        }
        var matcherSUB = Message.P_CMD_INNER_SUBSCRIBE.matcher(innerMessage);
        if (matcherSUB.matches()) {
            for (int i = 0; i < matcherSUB.groupCount(); i++) {
                LOGGER.info("Subcribe {}: {}", i, matcherSUB.group(i));
            }
            if (ids.contains(idAtDomain)) {
                LOGGER.info("Msg subscribe déjà exécuté {}", idAtDomain);
                return null;
            }

            ids.push(idAtDomain);
            String tagName = matcherSUB.group(3) + "@" + task.getDomain();
            String userName = matcherSUB.group(1) + "@" + task.getDomain();
            boolean isUser = matcherSUB.group(1) != null;
            if (isUser) {
                subscriberService.userSubscribe(userName, task.getDomain(), fromUserAtDomain);
            } else {
                subscriberService.tagSubscribe(tagName, task.getDomain(), fromUserAtDomain);
            }

        } else {
            LOGGER.info("ne matche pas subscribe");
        }

        return null;
    }

    private void doMsg(String fromUser, String toUser, String msgToSend) {
        LOGGER.info("Envoi vers utilisateur: {} → {}", fromUser, toUser);
        activeSession.send(toUser, msgToSend);
    }

    private void doTag(String fromUser, String toTag, String msgToSend, String id, List<ITag> msgTags, String currentDomain) throws HandlerException {
        LOGGER.info("doTag: {} → {}", fromUser, toTag);
        try {
            List<IUserSubscriber> subs = tagManagerService.getTagSubscriber(toTag);
            LOGGER.info("Subscribers du tag {}: {}", toTag, subs.stream().map(s -> s.getFullName() + "(local=" + s.isLocal() + ")").toList());

            if (subs.stream().anyMatch(sub -> sub.getFullName().equals(fromUser))) {
                for (IUserSubscriber sub : subs) {
                    if (!sub.getFullName().equals(fromUser)) {
                        List<String> relevant = getRelevantSubscriptions(sub.getFullName(), fromUser, msgTags, currentDomain, toTag);

                        LOGGER.info("relevant du tag {} pour {}",relevant, sub.getFullName());
                        if (relevant.isEmpty()) {
                            LOGGER.info("Livraison ignorée pour {} : aucune subscription pertinente", sub.getFullName());
                            continue;
                        }
                        if (!relevant.getFirst().equals(toTag)) {
                            LOGGER.info("Livraison ignorée pour {} : priorité supérieure à {} existe: {}", sub.getFullName(), toTag, relevant.getFirst());
                            continue;
                        }

                        if (sub.isLocal()) {
                            LOGGER.info("Response from remote tag (internal user) : {}", msgToSend);
                            activeSession.send(sub.getFullName(), msgToSend);
                        } else {
                            LOGGER.info("Resend msg for {} to speaker {}", sub.getFullName(), msgToSend);
                            String messageToCrypt = MessageBuilder.buildPost(id, fromUser, sub.getFullName(), msgToSend);
                            LOGGER.info("Resend post to speaker {}", messageToCrypt);
                            try {
                                speakerService.sendCryptedMessage(messageToCrypt);
                            } catch (MessageSaveException e) {
                                LOGGER.error("Error sending Crypted Message", e);
                                throw new HandlerException("Impossible de sauvegarder le message");
                            } catch (EncryptionException e) {
                                LOGGER.error("Error crypting Message", e);
                                throw new HandlerException("Impossible de crypter le message");
                            }
                        }
                    } else {
                        LOGGER.info("ne pas envoyer le msg à l'expéditeur");
                    }
                }
            } else {
                LOGGER.info("{} n'est pas abonné au tag {}", fromUser, toTag);
            }
        } catch (TagReaderException e) {
            throw new HandlerException("Erreur lecture tag", e);
        }
    }

    /**
     * Résout les destinataires uniques selon l'ordre de priorité.
     */
    private List<String> getRelevantSubscriptions(String recipient, String fromUser, List<ITag> msgTags, String currentDomain, String toTag) {
        try {
            LOGGER.info("Looking up recipient: '{}'", recipient);
            Client user = userManagerService.findUser(recipient);
            if (user == null) {
                LOGGER.warn("User not found: {}", recipient);
                return List.of();
            }

            List<String> subscriptions = new ArrayList<>(user.getSubscribtions());
            user.getTags().stream().map(ITag::getFullName).forEach(subscriptions::add);

            LOGGER.info("Subscriptions for {}: {}", recipient, subscriptions);
            LOGGER.info("Looking for sender '{}' and tags {}", fromUser, msgTags.stream().map(ITag::getFullName).toList());

            // Règle 1 : abonné à l'expéditeur → jamais livrer car réalisé dans une autre méthode
            if (subscriptions.contains(fromUser)) {
                LOGGER.info("Livraison autorisée pour {} : abonné à l'expéditeur {}", recipient, fromUser);
                return List.of();
            }

            // Règle 2 : tags du message auxquels le destinataire est abonné (ordre d'apparition)
            List<String> matchedTags = new ArrayList<>();
            for (ITag tag : msgTags) {
                String tagFullAddress = tag.getFullName();
                if (subscriptions.contains(tagFullAddress)) {
                    matchedTags.add(tagFullAddress);
                }
            }

            if (!matchedTags.isEmpty()) {
                // Livrer seulement si toTag est le premier tag matché
                LOGGER.info("Tags matchés pour {}: {}", recipient, matchedTags);
                return List.of(matchedTags.getFirst());
            }

            // Fallback : abonné au tag destination directement
            if (subscriptions.contains(toTag)) {
                LOGGER.info("Relevant (destination tag) for {}: {}", recipient, toTag);
                return List.of(toTag);
            }

            LOGGER.info("Aucune subscription pertinente pour {}", recipient);
            return List.of();

        } catch (UserReaderException e) {
            LOGGER.error("Error finding user {}: {}", recipient, e.getMessage());
        }
        return List.of();
    }
}