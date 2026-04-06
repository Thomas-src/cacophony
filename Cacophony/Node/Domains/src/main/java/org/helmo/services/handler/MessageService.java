package org.helmo.services.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.TagReaderException;
import org.helmo.Exception.UserReaderException;
import org.helmo.Grammar;
import org.helmo.services.TagManagerService;
import org.helmo.services.UserManagerService;
import org.helmo.subscriber.IUserSubscriber;
import org.helmo.tag.ITag;
import org.helmo.tag.InternalTag;
import org.helmo.tag.TagReference;

import java.util.*;
import java.util.regex.Matcher;

/**
 * Service responsable de la gestion et de la récupération des abonnés pour un utilisateur ou un message donné.
 */
public class MessageService {
    private static final Logger LOGGER = LogManager.getLogger();

    private final UserManagerService userManagerService;
    private final TagManagerService tagManagerService;

    /**
     * Initialise une nouvelle instance de MessageService.
     *
     * @param userManagerService Le service de gestion des utilisateurs.
     * @param tagManagerService  Le service de gestion des tags.
     */
    public MessageService(UserManagerService userManagerService, TagManagerService tagManagerService) {
        this.userManagerService = Objects.requireNonNull(userManagerService);
        this.tagManagerService = Objects.requireNonNull(tagManagerService);
    }

    /**
     * Récupère la liste des abonnés (locaux et distants) ainsi que les tags distants concernés par un message.
     *
     * @param username Le nom de l'utilisateur émetteur.
     * @param message  Le contenu du message contenant potentiellement des tags.
     * @param domain   Le domaine local actuel.
     * @return Un objet {@link SubscriberResultData} contenant les ensembles d'abonnés et de tags.
     */
    public SubscriberResultData getSubscribers(String username, String message, String domain) throws UserReaderException, TagReaderException {
        Set<String> localSubscribers = new LinkedHashSet<>();
        Set<String> remoteSubscribers = new LinkedHashSet<>();
        LinkedHashSet<String> remoteTags = new LinkedHashSet<>();

        // 1. abonnés user
        addUsers(username, localSubscribers, remoteSubscribers);

        // 2. abonnés via tags
        List<ITag> tags = extractTags(message, domain);
        addTags(username, tags, localSubscribers, remoteSubscribers, remoteTags);

        // 3. ne pas s'envoyer à soi-même
        localSubscribers.remove(username);
        remoteSubscribers.remove(username);

        return new SubscriberResultData(localSubscribers, remoteSubscribers, remoteTags);
    }

    /**
     * Ajoute les abonnés d'un utilisateur spécifique aux listes d'abonnés locaux et distants.
     *
     * @param username Le nom de l'utilisateur dont on récupère les abonnés.
     * @param local    L'ensemble des abonnés locaux à compléter.
     * @param remote   L'ensemble des abonnés distants à compléter.
     * @throws UserReaderException Si une erreur survient lors de la lecture des utilisateurs.
     */
    private void addUsers(String username, Set<String> local, Set<String> remote) throws UserReaderException {
        List<IUserSubscriber> subscribers = userManagerService.getUserSubscribers(username);
        if (subscribers != null) {
            createUsers(subscribers, local, remote);
        }
    }

    /**
     * Traite une liste de tags pour identifier les abonnés locaux/distants et les tags distants.
     *
     * @param username   Le nom de l'utilisateur émetteur.
     * @param tags       La liste des tags extraits du message.
     * @param local      L'ensemble des abonnés locaux à compléter.
     * @param remote     L'ensemble des abonnés distants à compléter.
     * @param remoteTags L'ensemble des noms de tags distants à compléter.
     */
    private void addTags(String username, List<ITag> tags, Set<String> local, Set<String> remote, Set<String> remoteTags)  {
        for (ITag tag : tags) {
            if (tag.isInternal()) {
                InternalTag internal = (InternalTag) tag;
                if (internal.hasSubscriber(username)) {
                    createUsers(internal.getSubscribers(), local, remote);
                } else {
                    LOGGER.info("{} non abonné au tag {}", username, tag.getFullName());
                }
            } else {
                remoteTags.add(tag.getFullName());
            }
        }
    }

    /**
     * Extrait les tags d'un message et détermine s'ils sont internes ou distants.
     *
     * @param message Le contenu du message à analyser.
     * @param domain  Le domaine local pour la comparaison.
     * @return Une liste d'objets {@link ITag} représentant les tags trouvés.
     * @throws TagReaderException Si une erreur survient lors de la lecture des tags.
     */
    public List<ITag> extractTags(String message, String domain) throws TagReaderException {
        List<ITag> tags = new ArrayList<>();
        Matcher m = Grammar.P_TAG_FULL.matcher(message);

        while (m.find()) {
            String tagName = m.group(1);
            String tagDomain = m.group(2);
            LOGGER.debug("tagName={}, tagDomain={}", tagName, tagDomain);

            if (tagDomain == null || tagDomain.equalsIgnoreCase(domain)) {
                List<IUserSubscriber> subscribers = tagManagerService.getTagSubscriber(tagName);
                tags.add(new InternalTag(tagName, subscribers));
            } else {
                tags.add(new TagReference(tagName + "@" + tagDomain));
            }
        }
        return tags;
    }

    /**
     * Répartit une liste d'abonnés dans les ensembles locaux ou distants selon leur type.
     *
     * @param subscribers La liste des abonnés à traiter.
     * @param local       L'ensemble de destination pour les abonnés locaux.
     * @param remote      L'ensemble de destination pour les abonnés distants.
     */
    private void createUsers(List<IUserSubscriber> subscribers, Set<String> local, Set<String> remote) {
        for (IUserSubscriber sub : subscribers) {
            if (sub.isLocal()) {
                local.add(sub.getFullName());
            } else {
                remote.add(sub.getFullName());
            }
        }
    }
}