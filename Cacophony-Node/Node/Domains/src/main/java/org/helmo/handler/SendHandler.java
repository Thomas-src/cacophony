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
import org.helmo.services.SubscriberResultData;
import org.helmo.services.SubscriberService;
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

    private final SubscriberService subscriberService;
    private final ActiveSession activeSession;
    private final EastThread eastThread;
    private final Config config;

    public SendHandler(SubscriberService subscriberService, ActiveSession activeSession, EastThread eastThread, Config config) {
        Objects.requireNonNull(subscriberService, "userManagerService is null");
        this.subscriberService = subscriberService;
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

        String message = args.getFirst();
        String username = session.getUserFullName();

        try {
            SubscriberResultData result = subscriberService.getSubscribers(username, message, session.getDomain());
            // 2. Pour abonnés locaux appel via WestThread
            sendToLocal(result.localSubscribers(),session, message);
            // 3. todo Pour abonnés externes envoie msg au EastThread
            sendPostSubscribers(result.remoteSubscribers(),session, message);


            // Message de retour (optionnel)
            return MessageBuilder.buildOk();

        } catch (Exception e) {
            throw new HandlerException("Erreur lors de l'envoi", e);
        }
    }

    private void sendToLocal(Set<String> localSubscribers, ClientSession session, String message ) {
        String localResponse = MessageBuilder.buildMsg(session.getUserFullName(), message);
        for (String localSubscriber : localSubscribers) {
            activeSession.send(localSubscriber, localResponse);
        }
    }

    private void sendPostSubscribers(Set<String> remoteSubscribers, ClientSession session, String message) {
        System.out.println();
        LOGGER.info("Remote subscribers size : " + remoteSubscribers.size());

        for (String remoteSubscriber : remoteSubscribers) {
            LOGGER.info(remoteSubscriber);
            // todo supprimer le hardcode + crypter
            String contextstr = MessageBuilder.buildPost( "1234 " + session.getDomain(), session.getUserFullName(), remoteSubscriber, message);

            eastThread.sendUncryptedMessage(contextstr);
        }
        System.out.println();
    }

}
