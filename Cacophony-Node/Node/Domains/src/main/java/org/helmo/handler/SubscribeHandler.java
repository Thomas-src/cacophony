package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.*;
import org.helmo.Grammar;
import org.helmo.MessageBuilder;
import org.helmo.client.Client;
import org.helmo.client.ClientSession;
import org.helmo.services.TagManagerService;
import org.helmo.services.UserManagerService;
import org.helmo.tag.InternalTag;

import java.util.Arrays;
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
        if (args.isEmpty()) {
            throw new HandlerException("Demande invalide");
        }

        String target = args.getFirst();
        String response = "";

        if (Grammar.P_TAG_AT_DOMAIN.matcher(target).matches()) {
            response = tagSubscribe(target, session);
        } else if (Grammar.P_USER_AT_DOMAIN.matcher(target).matches()) {
            response = userSubscribe(target, session);
        } else {
            throw new HandlerException("Demande invalide");
        }

        return response;
    }


    private String userSubscribe(String target, ClientSession session) {
        LOGGER.info("SUBSCRIBE USER");
        List<Client> clients;
        String targetName = target.split("@")[0];
        try {
            clients = userManagerService.getUsers();
            for (Client client : clients) {
                if (targetName.equals(client.getName())) {
                    LOGGER.info("\n========" + session.getUsername() + "========\n");
                    String sub = session.getUserFullName();
                    userManagerService.addSubscribersFor(client, sub);
                }
            }
        } catch (UserReaderException | UserSaveException e) {
            return MessageBuilder.buildError();
        }
        return MessageBuilder.buildOk();
    }

    private String tagSubscribe(String target, ClientSession session) throws HandlerException {
        List<InternalTag> tags;
        try {
            tags = tagManagerService.getTags();
        } catch (TagReaderException e) {
            throw new HandlerException("Impossible de lire les tags");
        }

        for (InternalTag tag : tags) {
            if (tag.getFullName().equals(target.split("@")[0])) { // Si le tag existe deja
                // Ajouter le tags pour le client dans la session
                try {
                    tagManagerService.addUserToTag(session.getClient(), target.split("@")[0]);
                    userManagerService.addTagFor(session.getClient(), target);
                } catch (UserSaveException | TagSaveException e) {
                    throw new HandlerException("Impossible d'ajouter le tags :'" + target + "'  pour l'utilisateur : " + session.getUsername());
                }
                // Retourner ok
                return MessageBuilder.buildOk();
            }
        }

        // Creer le tag
        try {
            tagManagerService.createTag(session.getClient(), Arrays.stream(target.split("@")).toList().getFirst());
            userManagerService.addTagFor(session.getClient(), target);
        } catch (TagSaveException | UserSaveException e) {
            throw new HandlerException("Impossible de creer le tag :" + target);
        }
        // Retourner ok
        return MessageBuilder.buildOk();
    }
}
