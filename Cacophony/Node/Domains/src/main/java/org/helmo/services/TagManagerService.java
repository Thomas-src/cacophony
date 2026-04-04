package org.helmo.services;

import org.helmo.Exception.TagReaderException;
import org.helmo.Exception.TagSaveException;
import org.helmo.Grammar;
import org.helmo.client.Client;
import org.helmo.tag.InternalTag;
import org.helmo.repository.ITagRepository;
import org.helmo.subscriber.IUserSubscriber;

import java.util.Collections;
import java.util.List;

/**
 * Service gérant les opérations liées aux tags.
 */
public class TagManagerService {
    private final ITagRepository tagRepository;

    /**
     * Initialise le service avec le dépôt de tags spécifié.
     *
     * @param tagRepository Le dépôt à utiliser pour l'accès aux données.
     */
    public TagManagerService(ITagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
     * Récupère la liste de tous les tags.
     *
     * @return Une liste d'objets InternalTag.
     * @throws TagReaderException Si une erreur survient lors de la lecture.
     */
    public List<InternalTag> getTags() throws TagReaderException {
        return tagRepository.getTags();
    }

    /**
     * Récupère les abonnés d'un tag spécifique par son nom.
     *
     * @param tagName Le nom du tag.
     * @return Une liste d'abonnés (IUserSubscriber).
     * @throws TagReaderException Si une erreur survient lors de la lecture.
     */
    public List<IUserSubscriber> getTagSubscriber(String tagName) throws TagReaderException {
        return getTags().stream()
                .filter(t -> t.getFullName().equals(extractTagName(tagName))) // todo equalsIgnoreCase ????
                .findFirst()
                .map(InternalTag::getSubscribers)
                .orElse(Collections.emptyList());
    }

    /**
     * Crée un nouveau tag pour un client donné.
     *
     * @param client Le client créateur.
     * @param tag    Le nom du tag à créer.
     * @throws TagSaveException Si une erreur survient lors de la sauvegarde.
     */
    public void createTag(Client client, String tag) throws TagSaveException {
        tagRepository.createTag(client, tag);
    }

    /**
     * Ajoute un utilisateur (client) à la liste des abonnés d'un tag.
     *
     * @param client Le client à ajouter.
     * @param tag    Le nom du tag.
     * @throws TagSaveException Si une erreur survient lors de la mise à jour.
     */
    public void addUserToTag(Client client, String tag) throws TagSaveException {
        tagRepository.addUserToTag(client, tag);
    }

    private static String extractTagName(String tagFullName) {
        var matcher = Grammar.P_TAG_FULL.matcher(tagFullName);
        if (matcher.matches()) {
            return matcher.group(1); // "#monTag2"
        }
        return tagFullName; // fallback si pas de domaine
    }
}
