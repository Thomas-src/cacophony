package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Config;
import org.helmo.Exception.HandlerException;
import org.helmo.Exception.TagReaderException;
import org.helmo.Exception.UserReaderException;
import org.helmo.Grammar;
import org.helmo.MessageBuilder;
import org.helmo.crypto.CryptoService;
import org.helmo.crypto.exceptions.EncryptionException;
import org.helmo.server.threads.EastThread;
import org.helmo.task.ClientSession;
import org.helmo.server.ActiveSession;
import org.helmo.services.TagManagerService;
import org.helmo.services.UserManagerService;
import org.helmo.subscriber.IUserSubscriber;
import org.helmo.tag.ITag;
import org.helmo.tag.InternalTag;
import org.helmo.tag.TagReference;
import org.helmo.task.TaskContext;

import java.util.*;
import java.util.regex.Matcher;

public class SendHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private final UserManagerService userManagerService;
    private final TagManagerService tagManagerService;
    private final ActiveSession activeSession;
    private final EastThread eastThread;
    private final Config config;

    public SendHandler(UserManagerService userManagerService, TagManagerService tagManagerService, ActiveSession activeSession, EastThread eastThread, Config config) {
        Objects.requireNonNull(userManagerService, "userManagerService is null");
        Objects.requireNonNull(tagManagerService, "tagManagerService is null");
        this.userManagerService = userManagerService;
        this.tagManagerService = tagManagerService;
        this.activeSession = activeSession;
        this.eastThread = eastThread;
        this.config = config;
    }

    @Override
    public String handle(List<String> args, TaskContext task) throws HandlerException {
        if (!(task instanceof ClientSession session)) {
            throw new HandlerException("Task send nécessite une session client");
        }

        if (!session.isAuthenticated() || args.isEmpty()) {
            return MessageBuilder.buildError();
        }
        Set<String> localSubscribers = new LinkedHashSet<>();
        Set<String> remoteSubscribers = new LinkedHashSet<>();
        Set<String> remoteTags = new LinkedHashSet<>();
        String message = args.getFirst();
        String username = session.getUserFullName();

        // Ajout des abonnés internes et externes
        addUsers(username, localSubscribers, remoteSubscribers);
        addTags(username, message, localSubscribers, remoteSubscribers,session.getDomain(), remoteTags);

        // Pour ne pas envoyer msg à l'envoyeur
        localSubscribers.remove(session.getUserFullName());
        remoteSubscribers.remove(session.getUserFullName());

        // 2. Pour abonnés locaux appel via WestThread
        String localResponse = MessageBuilder.buildMsg(session.getUserFullName(), message);
        for (String localSubscriber : localSubscribers) {
            activeSession.send(localSubscriber, localResponse);
        }
        // 3. todo Pour abonnés externes envoie msg au EastThread
        if (!remoteSubscribers.isEmpty()) {
            sendPostSubscribers(remoteSubscribers, session, message);
        }

        // Message de retour (optionnel)
        return MessageBuilder.buildOk();
    }

    private void sendPostSubscribers(Set<String> remoteSubscribers, ClientSession session, String message) {
        System.out.println();
        LOGGER.info("Remote subscribers size : " + remoteSubscribers.size());

        for (String remoteSubscriber : remoteSubscribers) {
            LOGGER.info(remoteSubscriber);
            String contextstr = MessageBuilder.buildPost( "1234 " + session.getDomain(), session.getUserFullName(), remoteSubscriber, message);

            eastThread.sendUncryptedMessage(contextstr);
        }
        System.out.println();
    }

    private List<ITag> extractTags(String message, String actualDomain) throws TagReaderException {
        List<ITag> tags = new ArrayList<>();
        Matcher m = Grammar.P_TAG_FULL.matcher(message);

        while (m.find()) {
            String tagName = m.group(1);
            String domain = m.group(2);

            LOGGER.info("Tag détecté: {}", tagName);
            if (domain == null  || domain.equalsIgnoreCase(actualDomain)) {
                List<IUserSubscriber> subscribers = tagManagerService.getTagSubscriber(tagName);
                tags.add(new InternalTag(tagName,subscribers));
            } else  {
                tags.add(new TagReference(tagName+"@"+domain));
                // todo speaker
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

    private void addTags(String username, String message, Set<String> localSubscribers, Set<String> remoteSubscribers, String actualDomain, Set<String> remoteTags) throws HandlerException {
        try {
            List<ITag> tags = extractTags(message, actualDomain);

            for (ITag tag : tags) {
                if (tag.isInternal()) {
                    InternalTag internal = (InternalTag) tag;
                    LOGGER.info("Abonnés au tag {} : {}", internal.getFullName(),
                            internal.getSubscribers().stream().map(IUserSubscriber::getFullName).toList());
                    if (internal.hasSubscriber(username)) {
                        createUsers(internal.getSubscribers(), localSubscribers, remoteSubscribers);
                    } else {
                        LOGGER.info("L'exécutant {} n'est pas abonné au tag {}", username, tag.getFullName());
                    }
                    } else {
                    remoteTags.add(tag.getFullName());
                }
            }
        } catch (TagReaderException e) {
            throw new HandlerException("Impossible de lire le fichier des tags");
        }
    }


    private void createUsers(List<IUserSubscriber> subscribers, Set<String> localSubscribers, Set<String> remoteSubscribers) {
        for (IUserSubscriber subscriber : subscribers) {
           if (subscriber.isLocal()) {
                localSubscribers.add(subscriber.getFullName());
            } else {
                remoteSubscribers.add(subscriber.getFullName());
            }
        }
    }
}
