package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.Exception.UserReaderException;
import org.helmo.ISubscriber;
import org.helmo.MessageBuilder;
import org.helmo.client.ClientSession;
import org.helmo.server.ActiveSession;
import org.helmo.server.threads.WestThread;
import org.helmo.services.UserManagerService;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SendHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private final UserManagerService userManagerService;

    public SendHandler(UserManagerService userManagerService) {
        Objects.requireNonNull(userManagerService, "userManagerService is null");
        this.userManagerService = userManagerService;
    }

    @Override
    public String handle(String message, ClientSession session) throws HandlerException {
        if(!session.isAuthenticated()) {
            return MessageBuilder.buildError();
        }
        Set<String> localSubscribers  = new LinkedHashSet<>();
        Set<String> remoteSubscribers  = new LinkedHashSet<>();


        try {
            LOGGER.info(userManagerService.getUserSubscribers(session.getUsername()));
            for(ISubscriber subscriber : userManagerService.getUserSubscribers(session.getUsername())) {
               // todo continuer ici (trouve aucun abonné)
                LOGGER.info("Subscriber " + subscriber.getFullName());
               if(subscriber.isLocal()) {
                   localSubscribers.add(subscriber.getName());
               } else {
                   remoteSubscribers.add(subscriber.getFullName());
               }
            }
        } catch (UserReaderException e) {
            throw new HandlerException("Impossible de lire le fichier");
        }

        // todo attention pas envoyer msg à l'envoyeur
       // 1. obtenir tous les tags (internes / externes du message)
        // vérifier si utilisateur abonnés à ces tags sinon supprimer

        List<String> tags = extractTags(message);
        // ajouter tous les abonnés locaux dans un seul set (éviter collision) users
        // ajouter tous les abonnés externes dans un seul set (éviter collision) users
        // ajouter tous les abonnés locaux dans un seul set (éviter collision) tags
        // ajouter tous les abonnés externes dans un seul set (éviter collision) tags

        // 2. Pour abonnés locaux appel via WestThread




        String localSubscriberResponse = MessageBuilder.buildMsg(session.getUserFullName(), message);

        for(String localSubscriber : localSubscribers) {
               ActiveSession.send(localSubscriber,localSubscriberResponse);
        }
        // 3. Pour abonnés externes envoie msg au EastThread


//-------
        // 1. Gérer les messages
        //      - Lister les tags

        //      - Vérifier l'existence des tags locaux

        // 2. Obtenir les subscribers

        // 3. notification locales (WestThread)

        // 4. notification speaker (eastThread)

        // 5. message de retour
        return MessageBuilder.buildOk();
    }

    private static final Pattern HASHTAG_RE = Pattern.compile("#([A-Za-z0-9]{5,20})"); // todo déplacer dans la grammaire

    private List<String> extractTags(String message) {
        List<String> tags = new ArrayList<>();
        Matcher m = HASHTAG_RE.matcher(message);
        while (m.find()) {
            tags.add("#" + m.group(1));
        }
        return tags;
    }
}
