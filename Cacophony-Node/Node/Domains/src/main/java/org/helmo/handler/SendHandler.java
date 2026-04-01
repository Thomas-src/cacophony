package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Config;
import org.helmo.Exception.HandlerException;
import org.helmo.Exception.TagReaderException;
import org.helmo.Exception.UserReaderException;
import org.helmo.Grammar;
import org.helmo.Message;
import org.helmo.MessageBuilder;
import org.helmo.crypto.CryptoService;
import org.helmo.crypto.exceptions.EncryptionException;
import org.helmo.server.threads.EastThread;
import org.helmo.services.*;
import org.helmo.task.ClientSession;
import org.helmo.server.ActiveSession;
import org.helmo.subscriber.IUserSubscriber;
import org.helmo.tag.ITag;
import org.helmo.tag.InternalTag;
import org.helmo.tag.TagReference;
import org.helmo.task.TaskContext;

import java.util.*;
import java.util.regex.Matcher;

/**
 * Gère l'envoi de messages d'un utilisateur vers ses abonnés locaux et distants.
 */
public class SendHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private final MessageService messageService;
    private final ActiveSession activeSession;
    private final EastThread eastThread;
    private final Config config;
    private final CryptoService cryptoService;

    private int count = 0; // todo ok comme ceci ? pas de risque multithread ?
    /**
     * Initialise le gestionnaire d'envoi.
     *
     * @param messageService Service pour récupérer les abonnés d'un message.
     * @param activeSession  Gestionnaire des sessions actives locales.
     * @param eastThread     Thread gérant les communications sortantes vers d'autres domaines.
     * @param config         Configuration du noeud.
     */
    public SendHandler(MessageService messageService, ActiveSession activeSession, EastThread eastThread, Config config, CryptoService cryptoService) {
        Objects.requireNonNull(messageService, "userManagerService is null");
        this.messageService = messageService;
        this.activeSession = activeSession;
        this.eastThread = eastThread;
        this.config = config;
        this.cryptoService = cryptoService;
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
            SubscriberResultData result = messageService.getSubscribers(username, message, session.getDomain());
            // 2. Pour abonnés locaux appel via WestThread

            String id = String.format("%04d", count);
            sendToLocal(result.localSubscribers(), session, message);
            // 3. todo Pour abonnés externes envoie msg au EastThread
            sendPostSubscribers(result.remoteSubscribers(), session, message,id);
            // 4. // todo pour les tags externes envoie msg au EastThread

            // Message de retour (optionnel)
            return MessageBuilder.buildOk();

        } catch (Exception e) {
            throw new HandlerException("Erreur lors de l'envoi", e);
        }
    }

    /**
     * Envoie le message aux abonnés présents sur le même domaine.
     *
     * @param localSubscribers Liste des noms d'utilisateurs locaux.
     * @param session          Session de l'expéditeur.
     * @param message          Contenu du message.
     */
    private void sendToLocal(Set<String> localSubscribers, ClientSession session, String message) {
        String localResponse = MessageBuilder.buildMsg(session.getUserFullName(), message);
        for (String localSubscriber : localSubscribers) {
            activeSession.send(localSubscriber, localResponse);
        }
    }

    /**
     * Envoie le message aux abonnés situés sur des domaines distants via le EastThread.
     *
     * @param remoteSubscribers Liste des identifiants complets des abonnés distants.
     * @param session           Session de l'expéditeur.
     * @param message           Contenu du message.
     */
    private void sendPostSubscribers(Set<String> remoteSubscribers, ClientSession session, String message, String id) throws HandlerException {
        LOGGER.info("Remote subscribers size : " + remoteSubscribers.size());

        String aesKey = config.base64AES();
        String domain = "@" + session.getDomain();
        for (String remoteSubscriber : remoteSubscribers) {
            LOGGER.info(remoteSubscriber);
            String subMessage = MessageBuilder.buildMsg(session.getUserFullName(), message);
            String contextstr = MessageBuilder.buildPost(id + domain, session.getUserFullName(), remoteSubscriber, subMessage);
            try {
               String encryptedMsg =  cryptoService.encrypt(contextstr, aesKey);
                eastThread.sendMessage(encryptedMsg);
            } catch (EncryptionException e) {
                LOGGER.error(e.getMessage(), e);
                throw new HandlerException("Impossible de crypter le message", e);
            }


        }
    }

    // todo méthode sendPostExternalTag


}
