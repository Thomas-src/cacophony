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
        if (!session.isAuthenticated() || args.isEmpty()) {
            return MessageBuilder.buildError();
        }
        Set<String> localSubscribers = new LinkedHashSet<>();
        Set<String> remoteSubscribers = new LinkedHashSet<>();
        String message = args.getFirst();
        String username = session.getUserFullName();

        // Ajout des abonnés internes et externes
        addUsers(username, localSubscribers, remoteSubscribers);
        addTags(username, message, localSubscribers, remoteSubscribers,session.getDomain());

        // Pour ne pas envoyer msg à l'envoyeur
        localSubscribers.remove(session.getUserFullName());
        remoteSubscribers.remove(session.getUserFullName());

        // 2. Pour abonnés locaux appel via WestThread
        String localResponse = MessageBuilder.buildMsg(session.getUserFullName(), message);
        for (String localSubscriber : localSubscribers) {
            ActiveSession.send(localSubscriber, localResponse);
        }
        // 3. todo Pour abonnés externes envoie msg au EastThread

        // Message de retour (optionnel)
        return MessageBuilder.buildOk();
    }

    private List<String> extractTags(String message, String actualDomain) {
        List<String> tags = new ArrayList<>();
        Matcher m = Grammar.P_TAG_FULL.matcher(message);

        while (m.find()) {
            String tagName = m.group(1);
            String domain = m.group(2);
            LOGGER.info(actualDomain);
            LOGGER.info(domain);

            if (domain != null && !domain.equalsIgnoreCase(actualDomain)) {
                tags.add("#" + tagName + "@" + domain);
            } else {
                tags.add("#" + tagName);
            }
        }
        return tags;
    }

    private void addUsers(String username, Set<String> localSubscribers, Set<String> remoteSubscribers) throws HandlerException {
        try {
            List<IUserSubscriber> subscribers = userManagerService.getUserSubscribers(username);
            if (subscribers != null) {
                LOGGER.info("L'utilisateur {} possède {} abonnés", username, subscribers.size());
                createUsers(subscribers, localSubscribers, remoteSubscribers);
            }
        } catch (UserReaderException e) {
            throw new HandlerException("Impossible de lire le fichier des utilisateurs");
        }
    }

    private void addTags(String username, String message, Set<String> localSubscribers, Set<String> remoteSubscribers, String actualDomain) throws HandlerException {
        List<String> tags = extractTags(message, actualDomain);
        LOGGER.info("Tags du message: {}", tags);
        try {
            for (String tag : tags) {
                List<IUserSubscriber> subscribers = tagManagerService.getTagSubscriber(tag);
                if (subscribers != null) {
                    if (subscribers.stream().anyMatch(sub -> sub.getFullName().equals(username))) {  // vérifier si l'utilisateur est abonné au tag
                        LOGGER.info("Le tag {} possède {} abonnés", tag, subscribers.size());
                        createUsers(subscribers, localSubscribers, remoteSubscribers);
                    }
                    LOGGER.info("L'exécutant n'est pas abonné à la liste");

                }
            }
        } catch (TagReaderException e) {
            throw new HandlerException("Impossible de lire le fichier des tags");
        }
    }


    private void createUsers(List<IUserSubscriber> subscribers, Set<String> localSubscribers, Set<String> remoteSubscribers) {
        for (IUserSubscriber subscriber : subscribers) {
            LOGGER.info("Vérification Subscriber {}", subscriber.getFullName());
            if (subscriber.isLocal()) {
                localSubscribers.add(subscriber.getFullName());
            } else {
                remoteSubscribers.add(subscriber.getFullName());
            }
        }
    }
}
