package org.helmo.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.*;
import org.helmo.Grammar;
import org.helmo.MessageBuilder;
import org.helmo.client.Client;
import org.helmo.crypto.exceptions.EncryptionException;
import org.helmo.subscriber.IUserSubscriber;
import org.helmo.tag.ITag;
import org.helmo.tag.InternalTag;
import org.helmo.tag.TagReference;
import org.helmo.task.ClientSession;

import java.util.*;
import java.util.regex.Matcher;

/**
 * Service responsable de la gestion et de la récupération des abonnés pour un utilisateur ou un message donné.
 */
public class SubscriberService {
    private static final Logger LOGGER = LogManager.getLogger();

    private final UserManagerService userManagerService;
    private final TagManagerService tagManagerService;
    private final SpeakerService speakerService;
    private final IdGeneratorService idGeneratorService;

    /**
     * Initialise le gestionnaire d'abonnement.
     *
     * @param userManagerService Service de gestion des utilisateurs.
     * @param tagManagerService  Service de gestion des tags.
     */
    public SubscriberService(UserManagerService userManagerService, TagManagerService tagManagerService, SpeakerService speakerService, IdGeneratorService idGeneratorService) {
        this.userManagerService = userManagerService;
        this.tagManagerService = tagManagerService;
        this.speakerService = speakerService;
        this.idGeneratorService = idGeneratorService;
    }

    /**
     *
     * @param targetfullName Nom d'utilisateur de l'abonnement
     * @param domain Domaine courant
     * @param fullusername Nom d'utilisateur de l'abonné
     * @return
     */
    public String userSubscribe(String targetfullName, String domain, String fullusername) {
        LOGGER.info("SUBSCRIBE USER target : {}, to {}", targetfullName, fullusername);
        List<Client> clients;
        String targetName = extractUsername(targetfullName);
        if (targetName.equals(fullusername)) {
            return MessageBuilder.buildError();
        }
        try {
            clients = userManagerService.getUsers();
            for (Client client : clients) {
                if (targetName.equals(client.getName())) {
                    LOGGER.info("========{} est désormais abonné========", fullusername);
                    userManagerService.addSubscribersFor(client, fullusername, domain);
                }
            }
        } catch (UserReaderException | UserSaveException e) {
            return MessageBuilder.buildError();
        }
        return MessageBuilder.buildOk();
    }

    /**
     * Gère l'abonnement d'un utilisateur à un tag.
     *
     * @param tagName Nom du tag.
     * @return Message de réponse (OK).
     * @throws HandlerException Si une erreur survient lors de la lecture ou sauvegarde.
     */
    public String tagSubscribe(String tagName, String domain, String fullUsername) throws HandlerException {
        List<InternalTag> tags;
        String fullTagName = tagName + "@" + domain;
        try {
            tags = tagManagerService.getTags();
        } catch (TagReaderException e) {
            throw new HandlerException("Impossible de lire les tags");
        }
        String username = extractUsername(fullUsername);
        for (InternalTag tag : tags) {
            if (tag.getFullName().equals(tagName)) {
                // Si le tag existe deja
                // Ajouter le tags pour le client dans la session
                try {
                    tagManagerService.addUserToTag(fullUsername, tagName);
                    userManagerService.addTagFor(username, fullTagName);
                    LOGGER.info(username + " s'est abonné à : " + fullTagName);
                } catch (UserSaveException | TagSaveException e) {
                    throw new HandlerException("Impossible d'ajouter le tags :'" + fullTagName + "'  pour l'utilisateur : " + username);
                }
                // Retourner ok
                return MessageBuilder.buildOk();
            }
        }


        // Creer le tag
        try {
            tagManagerService.createTag(fullUsername, tagName);
            userManagerService.addTagFor(username, fullTagName);
            LOGGER.info(fullUsername + " a créé le tag : " + fullTagName);
        } catch (TagSaveException | UserSaveException e) {
            throw new HandlerException("Impossible de creer le tag :" + fullTagName);
        }
        // Retourner ok
        return MessageBuilder.buildOk();
    }

    public void sendToSpeaker(ClientSession session, List<String> args) throws HandlerException {
        String toSubscribe = args.get(0) != null ? args.getFirst() + "@" + args.get(1) : args.get(2) + "@" + args.getLast();

        String subMessage = MessageBuilder.buildInnerSubscribe(toSubscribe);
        String  id= idGeneratorService.getNextCountString();
        String messageToCrypt = MessageBuilder.buildPost(id + "@"+ session.getDomain(), session.getUserFullName(), toSubscribe, subMessage);
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
    private static String extractUsername(String fullusername) {
        var matcher = Grammar.P_USER_AT_DOMAIN.matcher(fullusername);
        if (matcher.matches()) {
            return matcher.group(1); // "#monTag2"
        }
        return fullusername;
    }
}