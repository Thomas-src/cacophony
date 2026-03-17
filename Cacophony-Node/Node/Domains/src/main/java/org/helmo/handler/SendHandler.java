package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.Exception.TagReaderException;
import org.helmo.Exception.UserReaderException;
import org.helmo.Grammar;
import org.helmo.MessageBuilder;
import org.helmo.client.ClientSession;
import org.helmo.server.ActiveSession;
import org.helmo.services.TagManagerService;
import org.helmo.services.UserManagerService;
import org.helmo.subscriber.IUserSubscriber;

import java.util.*;
import java.util.regex.Matcher;

public class SendHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private final UserManagerService userManagerService;
    private final TagManagerService tagManagerService;
    public SendHandler(UserManagerService userManagerService, TagManagerService tagManagerService) {
        Objects.requireNonNull(userManagerService, "userManagerService is null");
        Objects.requireNonNull(tagManagerService, "tagManagerService is null");
        this.userManagerService = userManagerService;
        this.tagManagerService = tagManagerService;
    }

    @Override
    public String handle(List<String> args, ClientSession session) throws HandlerException {
        if(!session.isAuthenticated()) {
            return MessageBuilder.buildError();
        }

        String message = args.getFirst();
        Set<String> localSubscribers  = new LinkedHashSet<>();
        Set<String> remoteSubscribers  = new LinkedHashSet<>();
        try {
            List<IUserSubscriber> subscribers = userManagerService.getUserSubscribers(session.getUserFullName());
            if(subscribers != null) {
                LOGGER.info("L'utilisateur {} possède {} abonnés",session.getUsername(),subscribers.size());
                addUsers(subscribers, localSubscribers, remoteSubscribers);
            }


        } catch (UserReaderException e) {
            throw new HandlerException("Impossible de lire le fichier des utilisateurs");
        }

        // todo attention pas envoyer msg à l'envoyeur
       // 1. obtenir tous les tags (internes / externes du message)
        // vérifier si utilisateur abonnés à ces tags sinon annuler

        List<String> tags = extractTags(message);
        LOGGER.info("Tags du message: {}", tags);
        try {
            for(String tag : tags) {
                // todo 1. vérifier si l'utilisateur est abonné au tag
                List<IUserSubscriber> subscribers = tagManagerService.getTagSubscriber(tag);
                if(subscribers != null) {
                    LOGGER.info("Le tag {} possède {} abonnés",tag,subscribers.size());
                    addUsers(subscribers, localSubscribers, remoteSubscribers);
                }
            }
        } catch (TagReaderException e) {
            throw new HandlerException("Impossible de lire le fichier des tags");
        }

        // Supprimé le client de la liste si jamais
        localSubscribers.remove(session.getUserFullName());
        remoteSubscribers.remove(session.getUserFullName());
        // 2. Pour abonnés locaux appel via WestThread
        String localSubscriberResponse = MessageBuilder.buildMsg(session.getUserFullName(), message);
        for(String localSubscriber : localSubscribers) {
               ActiveSession.send(localSubscriber,localSubscriberResponse);
        }
        // 3. Pour abonnés externes envoie msg au EastThread

        // message de retour
        return MessageBuilder.buildOk();
    }

    private List<String> extractTags(String message) {
        List<String> tags = new ArrayList<>();
        Matcher m = Grammar.P_TAG_FULL.matcher(message);

        while (m.find()) {
            String tagName = m.group(1);
            String domain = m.group(2);

            if (domain != null) {
                tags.add("#" + tagName + "@" + domain);
            } else {
                tags.add("#" + tagName);
            }
        }

        return tags;
    }

    private void addUsers(List<IUserSubscriber> subscribers, Set<String> localSubscribers, Set<String> remoteSubscribers) {
        for  (IUserSubscriber subscriber : subscribers) {
            LOGGER.info("Vérification Subscriber {}", subscriber.getFullName());
            if(subscriber.isLocal()) {
                localSubscribers.add(subscriber.getFullName());
            } else {
                remoteSubscribers.add(subscriber.getFullName());
            }
        }
    }
}
