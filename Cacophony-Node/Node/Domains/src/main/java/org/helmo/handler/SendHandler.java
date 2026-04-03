package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.Exception.MessageSaveException;
import org.helmo.MessageBuilder;
import org.helmo.crypto.CryptoProvider;
import org.helmo.crypto.exceptions.EncryptionException;
import org.helmo.repository.IMessageRepository;
import org.helmo.server.threads.EastThread;
import org.helmo.services.*;
import org.helmo.task.ClientSession;
import org.helmo.server.ActiveSession;
import org.helmo.task.TaskContext;

import java.util.*;

/**
 * Gère l'envoi de messages d'un utilisateur vers ses abonnés locaux et distants.
 */
public class SendHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private final MessageService messageService;
    private final ActiveSession activeSession;
    private final EastThread eastThread;
    private final CryptoProvider cryptoService;
    private final IMessageRepository messageRepository;
    private final IdGeneratorService idGeneratorService;

    /**
     * Initialise le gestionnaire d'envoi.
     *
     * @param messageService Service pour récupérer les abonnés d'un message.
     * @param activeSession  Gestionnaire des sessions actives locales.
     * @param eastThread     Thread gérant les communications sortantes vers d'autres domaines.
     */
    public SendHandler(MessageService messageService, ActiveSession activeSession, EastThread eastThread, CryptoProvider cryptoService, IMessageRepository messageRepository, IdGeneratorService idGeneratorService) {
        Objects.requireNonNull(messageService, "userManagerService is null");
        this.messageService = messageService;
        this.activeSession = activeSession;
        this.eastThread = eastThread;
        this.cryptoService = cryptoService;
        this.messageRepository = messageRepository;
        this.idGeneratorService = idGeneratorService;
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
            sendToLocal(result.localSubscribers(), session, message);

            String id = String.format("%05d", idGeneratorService.getNextCount());
            // 3. todo Pour abonnés externes envoie msg au EastThread
            sendPostSubscribers(result.remoteSubscribers(), session, message, id);
            // 4. // todo pour les tags externes envoie msg au EastThread
            sendToRemoteTags(result.remoteTags(), session, message, id);

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
        LOGGER.info("NB d'abonnés locaux : {} - {}", localSubscribers.size(), localSubscribers);
        String localResponse = MessageBuilder.buildMsg(session.getUserFullName(), message);
        for (String localSubscriber : localSubscribers) {
            LOGGER.info("sendToLocal : {}", localSubscriber);
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
        LOGGER.info("NB d'abonnés ditants : {} - {}", remoteSubscribers.size(), remoteSubscribers);

        String domain = "@" + session.getDomain();
        for (String remoteSubscriber : remoteSubscribers) {
            String subMessage = MessageBuilder.buildMsg(session.getUserFullName(), message);
            String messageToCrypt = MessageBuilder.buildPost(id + domain, session.getUserFullName(), remoteSubscriber, subMessage);

            try {
                String encryptedMsg = cryptoService.encrypt(messageToCrypt);
                if (eastThread.isConnected()) {
                    eastThread.sendMessage(encryptedMsg);
                } else {
                    messageRepository.saveMessage(encryptedMsg);   // utilisation de la même clé
                }
            } catch (EncryptionException e) {
                LOGGER.error(e.getMessage(), e);
                throw new HandlerException("Impossible de crypter le message", e);
            } catch (MessageSaveException e) {
                LOGGER.error(e.getMessage(), e);
                throw new HandlerException("Impossible de sauvegarder le message", e);
            }
        }

    }

    private void sendToRemoteTags(Set<String> remoteTags, ClientSession session, String message, String id) {
        LOGGER.info("NB de tags ditants : {} - {}", remoteTags.size(), remoteTags);

        // todo méthode sendPostExternalTag
    }
}

