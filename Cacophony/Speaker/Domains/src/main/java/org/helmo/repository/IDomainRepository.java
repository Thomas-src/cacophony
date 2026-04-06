package org.helmo.repository;

import org.helmo.domain.Domain;
import org.helmo.exceptions.DomainReaderException;
import org.helmo.exceptions.DomainSaveException;

import java.util.List;

/**
 * Interface pour la gestion de la persistance des données de domaine.
 */
public interface IDomainRepository {
    /**
     * Récupère la liste de tous les domaines disponibles.
     *
     * @return Une liste d'objets {@link Domain}.
     * @throws DomainReaderException si une erreur survient lors de la lecture des domaines.
     */
    List<Domain> getDomains() throws DomainReaderException;

    /**
     * Enregistre un message dans un domaine spécifique.
     *
     * @param domain  Le nom du domaine.
     * @param message Le contenu du message à enregistrer.
     * @throws DomainSaveException si une erreur survient lors de l'enregistrement.
     */
    void saveMessage(String domain, String message) throws DomainSaveException;

    /**
     * Supprime tous les messages d'un domaine spécifique.
     *
     * @param domain Le nom du domaine à vider.
     * @throws DomainSaveException si une erreur survient lors de la suppression des messages.
     */
    void clearDomainMessages(String domain) throws DomainSaveException;
}
