package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.*;
import org.helmo.MessageBuilder;
import org.helmo.client.Client;
import org.helmo.client.ClientSession;
import org.helmo.services.TagManagerService;
import org.helmo.services.UserManagerService;
import org.helmo.tag.InternalTag;

import java.util.List;

public class SubscribeHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private final UserManagerService userManagerService;
    private final TagManagerService tagManagerService;

    public SubscribeHandler(UserManagerService userManagerService, TagManagerService tagManagerService) {
        this.userManagerService = userManagerService;
        this.tagManagerService = tagManagerService;
    }

    @Override
    public String handle(List<String> args, ClientSession session) throws HandlerException {
        if (args.size() < 2) {
            throw new HandlerException("Demande invalide");
        }
        String response = MessageBuilder.buildError();
        boolean isUser = args.get(0) != null;
        String sub = isUser ? args.get(0) : args.get(2);
        String domain = isUser ? args.get(1) : args.get(3);

        if (domain.equalsIgnoreCase(session.getDomain())) {
            if(isUser) {
                response = userSubscribe(sub, session);
            } else {
                response = tagSubscribe(sub, session);
            }

        } else {
            // todo speaker
        }

        return response;
    }


    private String userSubscribe(String targetName, ClientSession session) {
        LOGGER.info("SUBSCRIBE USER");
        List<Client> clients;
        if (targetName.equals(session.getUsername())) {
            return MessageBuilder.buildError();
        }
        try {
            clients = userManagerService.getUsers();
            for (Client client : clients) {
                LOGGER.info("Checking {}", client.getName());
                if (targetName.equals(client.getName())) {
                    LOGGER.info("========{}========", session.getUsername());
                    String sub = session.getUserFullName();
                    userManagerService.addSubscribersFor(client, sub);
                }
            }
        } catch (UserReaderException | UserSaveException e) {
            return MessageBuilder.buildError();
        }
        return MessageBuilder.buildOk();
    }

    private String tagSubscribe(String tagName, ClientSession session) throws HandlerException {
        List<InternalTag> tags;
        String fullTagName = tagName+"@"+session.getDomain();
        try {
            tags = tagManagerService.getTags();
        } catch (TagReaderException e) {
            throw new HandlerException("Impossible de lire les tags");
        }

        for (InternalTag tag : tags) {
            LOGGER.info("Comparing {} with {}", tag.getFullName(), tagName);
            if (tag.getFullName().equals(tagName)) {
                // Si le tag existe deja
                // Ajouter le tags pour le client dans la session
                try {
                    tagManagerService.addUserToTag(session.getClient(), tagName);
                    userManagerService.addTagFor(session.getClient(), fullTagName);
                    LOGGER.info(session.getUsername() + " s'est abonné à : " + fullTagName);
                } catch (UserSaveException | TagSaveException e) {
                    throw new HandlerException("Impossible d'ajouter le tags :'" + fullTagName + "'  pour l'utilisateur : " + session.getUsername());
                }
                // Retourner ok
                return MessageBuilder.buildOk();
            }
        }

        // Creer le tag
        try {
            tagManagerService.createTag(session.getClient(), tagName);
            userManagerService.addTagFor(session.getClient(), fullTagName);
            LOGGER.info(session.getUsername() + " a créé le tag : " + fullTagName);
        } catch (TagSaveException | UserSaveException e) {
            throw new HandlerException("Impossible de creer le tag :" + fullTagName);
        }
        // Retourner ok
        return MessageBuilder.buildOk();
    }
}
