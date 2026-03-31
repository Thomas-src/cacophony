package org.helmo.repository;

import org.helmo.Exception.TagReaderException;
import org.helmo.Exception.TagSaveException;
import org.helmo.client.Client;
import org.helmo.tag.InternalTag;

import java.util.List;

/**
 * Interface définissant les opérations de persistance pour les tags.
 */
public interface ITagRepository {
    /**
     * Récupère la liste de tous les tags existants.
     *
     * @return Une liste d'InternalTag.
     * @throws TagReaderException Si une erreur survient lors de la lecture.
     */
    List<InternalTag> getTags() throws TagReaderException;

    /**
     * Sauvegarde un objet tag complet.
     *
     * @param internalTag Le tag à sauvegarder.
     * @throws TagSaveException Si une erreur survient lors de la sauvegarde.
     */
    void saveTag(InternalTag internalTag) throws TagSaveException;

    /**
     * Crée un nouveau tag associé à un client.
     *
     * @param client Le client créateur du tag.
     * @param tag Le nom du tag à créer.
     * @throws TagSaveException Si la création échoue.
     */
    void createTag(Client client, String tag)throws TagSaveException;

    /**
     * Ajoute un utilisateur à un tag existant.
     *
     * @param client Le client à ajouter.
     * @param tag Le nom du tag.
     * @throws TagSaveException Si l'ajout échoue.
     */
    void addUserToTag(Client client, String tag) throws TagSaveException;
}
