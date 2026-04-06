package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.Exception.MessageSaveException;
import org.helmo.MessageBuilder;
import org.helmo.crypto.exceptions.EncryptionException;
import org.helmo.handler.utils.CommandHandler;
import org.helmo.services.*;
import org.helmo.services.handler.MessageService;
import org.helmo.services.handler.SubscriberResultData;
import org.helmo.task.ClientSession;
import org.helmo.server.ActiveSession;
import org.helmo.task.TaskContext;

import java.util.*;

/**
 * Gère l'envoi de messages d'un utilisateur vers ses abonnés locaux, distants et les tags distants.
 */
public class SendHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private final MessageService messageService;
    private final ActiveSession activeSession;
    private final SpeakerService speakerService;
    private final IdGeneratorService idGeneratorService;

    /**
     * Initialise le gestionnaire d'envoi.
     *
     * @param messageService Service pour récupérer les abonnés d'un message.
     * @param speakerService Service pour l'envoi de messages cryptés vers l'extérieur.
     * @param idGeneratorService Service de génération d'identifiants uniques pour les messages.
     * @param activeSession  Gestionnaire des sessions actives locales.
     */
    public SendHandler(MessageService messageService, ActiveSession activeSession, SpeakerService speakerService,IdGeneratorService idGeneratorService) {
        Objects.requireNonNull(messageService, "userManagerService is null");
        this.messageService = messageService;
        this.activeSession = activeSession;
        this.idGeneratorService = idGeneratorService;
        this.speakerService = speakerService;
    }

    /**
     * Traite la commande d'envoi de message.
     *
     * @param args Liste d'arguments contenant le message à envoyer.
     * @param task Contexte de la tâche (doit être une ClientSession).
     * @return Réponse formatée indiquant le succès de l'opération.
     * @throws HandlerException Si la session est invalide ou si une erreur survient lors de l'envoi.
     */
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

            String id =  idGeneratorService.getNextCountString();
            // 3. Pour abonnés externes envoie msg au EastThread
            sendPostSubscribers(result.remoteSubscribers(), session, message, id);
            // 4. Pour les tags externes envoie msg au EastThread
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
        LOGGER.info("NB d'abonnés locaux : {}", localSubscribers.size());
        String localResponse = MessageBuilder.buildMsgInner(session.getUserFullName(), message);
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
     * @param id                Identifiant unique du message.
     * @throws HandlerException Si le cryptage ou la sauvegarde du message échoue.
     */
    private void sendPostSubscribers(Set<String> remoteSubscribers, ClientSession session, String message, String id) throws HandlerException {
        LOGGER.info("NB d'abonnés distants : {} - {}", remoteSubscribers.size(), remoteSubscribers);

        String domain = "@" + session.getDomain();
        String subMessage = MessageBuilder.buildMsgInner(session.getUserFullName(), message);
        for (String remoteSubscriber : remoteSubscribers) {
            String messageToCrypt = MessageBuilder.buildPost(id + domain, session.getUserFullName(), remoteSubscriber, subMessage);

            try {
               speakerService.sendCryptedMessage(messageToCrypt);
            } catch (EncryptionException e) {
                LOGGER.error(e.getMessage(), e);
                throw new HandlerException("Impossible de crypter le message", e);
            } catch (MessageSaveException e) {
                LOGGER.error(e.getMessage(), e);
                throw new HandlerException("Impossible de sauvegarder le message", e);
            }
        }

    }

    /**
     * Envoie le message vers les tags distants via le EastThread.
     *
     * @param remoteTags Liste ordonnée des tags distants.
     * @param session    Session de l'expéditeur.
     * @param message    Contenu du message.
     * @param id         Identifiant unique du message.
     * @throws HandlerException Si le cryptage ou la sauvegarde du message échoue.
     */
    private void sendToRemoteTags(LinkedHashSet<String> remoteTags, ClientSession session, String message, String id) throws HandlerException {
        LOGGER.info("NB de tags distants : {}", remoteTags.size());

        if (remoteTags.isEmpty()) return;

        String domain = "@" + session.getDomain();
        String subMessage = MessageBuilder.buildMsgInner(session.getUserFullName(), message);

        // Seul le premier tag compte
        String firstTag = remoteTags.getFirst();
        LOGGER.info("Envoi uniquement vers le premier tag distant : {}", firstTag);

        String messageToCrypt = MessageBuilder.buildPost(id + domain, session.getUserFullName(), firstTag, subMessage);
        try {
            speakerService.sendCryptedMessage(messageToCrypt);
        } catch (EncryptionException e) {
            LOGGER.error(e.getMessage(), e);
            throw new HandlerException("Impossible de crypter le message", e);
        } catch (MessageSaveException e) {
            LOGGER.error(e.getMessage(), e);
            throw new HandlerException("Impossible de sauvegarder le message", e);
        }
    }
}
