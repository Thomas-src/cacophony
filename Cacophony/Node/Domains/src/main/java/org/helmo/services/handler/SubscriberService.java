package org.helmo.services.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.*;
import org.helmo.Grammar;
import org.helmo.MessageBuilder;
import org.helmo.client.Client;
import org.helmo.crypto.exceptions.EncryptionException;
import org.helmo.services.IdGeneratorService;
import org.helmo.services.SpeakerService;
import org.helmo.services.TagManagerService;
import org.helmo.services.UserManagerService;
import org.helmo.tag.InternalTag;
import org.helmo.task.ClientSession;

import java.util.*;

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
     * Gère l'abonnement d'un utilisateur à un autre utilisateur.
     *
     * @param targetfullName Nom complet de la cible de l'abonnement.
     * @param domain         Domaine courant.
     * @param fullusername   Nom complet de l'utilisateur qui s'abonne.
     * @return Message de réponse (OK ou ERROR).
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
                    userManagerService.addSubscribersFor(client.getFullName(), fullusername);
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
     * @param fullTagName Nom du tag.
     * @param domain      Domaine courant.
     * @param fullUsername Nom complet de l'utilisateur.
     * @return Message de réponse (OK).
     * @throws HandlerException Si une erreur survient lors de la lecture ou sauvegarde.
     */
    public String tagSubscribe(String fullTagName, String domain, String fullUsername) throws HandlerException {
        List<InternalTag> tags;
        try {
            tags = tagManagerService.getTags();
        } catch (TagReaderException e) {
            throw new HandlerException("Impossible de lire les tags");
        }
        String username = extractUsername(fullUsername);
        for (InternalTag tag : tags) {
            if (tag.getFullName().equals(fullTagName)) {
                // Si le tag existe deja
                // Ajouter le tags pour le client dans la session
                try {
                    tagManagerService.addUserToTag(fullUsername, fullTagName);
                    userManagerService.addTagFor(fullUsername, fullTagName);
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
            tagManagerService.createTag(fullUsername, fullTagName);
            userManagerService.addTagFor(fullUsername, fullTagName);
            tagManagerService.addUserToTag(fullUsername, fullTagName);
            LOGGER.info(fullUsername + " a créé le tag : " + fullTagName);
        } catch (TagSaveException | UserSaveException e) {
            throw new HandlerException("Impossible de creer le tag :" + fullTagName);
        }
        // Retourner ok
        return MessageBuilder.buildOk();
    }

    /**
     * Envoie une demande d'abonnement au service Speaker.
     *
     * @param session Session du client actuel.
     * @param sub     Cible de l'abonnement (utilisateur ou tag).
     * @param isUser  Vrai s'il s'agit d'un utilisateur, faux s'il s'agit d'un tag.
     * @throws HandlerException Si une erreur survient lors du cryptage ou de la sauvegarde.
     */
    public void sendToSpeaker(ClientSession session, String sub, boolean isUser) throws HandlerException {
        if (!isUser) {
            try {
                LOGGER.info("subscribe vers le speaker enregistré :" + sub);
                userManagerService.addTagFor(session.getUserFullName(), sub);
            } catch (UserSaveException e) {
                LOGGER.error("Erreur lors de la lecture du tag " + e.getMessage());
            }
        } else {
            try {
                LOGGER.info("subscribe vers le speaker enregistré :" + sub);
                userManagerService.addSubscribtionFor(session.getUserFullName(), sub);
            } catch (UserSaveException e) {
                LOGGER.error("Erreur lors de l'enregistrement du tag " + e.getMessage());
            }
        }

        String subMessage = MessageBuilder.buildInnerSubscribe(sub);
        String id = idGeneratorService.getNextCountString();
        String messageToCrypt = MessageBuilder.buildPost(id + "@" + session.getDomain(), session.getUserFullName(), sub, subMessage);
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

    /**
     * Extrait le nom d'utilisateur d'un nom complet (user@domain).
     *
     * @param fullusername Nom complet.
     * @return Le nom d'utilisateur extrait.
     */
    private static String extractUsername(String fullusername) {
        var matcher = Grammar.P_USER_AT_DOMAIN.matcher(fullusername);
        if (matcher.matches()) {
            return matcher.group(1); // "#monTag2"
        }
        return fullusername;
    }
}