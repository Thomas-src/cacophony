package org.helmo.repository;

import org.helmo.domain.Domain;
import org.helmo.exceptions.DomainReaderException;
import org.helmo.exceptions.DomainSaveException;

import java.util.List;

/**
 * Interface for managing domain data persistence.
 */
public interface IDomainRepository {
    /**
     * Retrieves the list of all available domains.
     *
     * @return A list of {@link Domain} objects.
     * @throws DomainReaderException if an error occurs while reading the domains.
     */
    List<Domain> getDomains() throws DomainReaderException;

    /**
     * Saves a list of messages for a specific domain.
     *
     * @param domain   The name of the domain where messages should be saved.
     * @param messages The list of messages to persist.
     * @throws DomainSaveException if an error occurs while saving the messages.
     */
    void saveMessages(String domain, List<String> messages) throws DomainSaveException;
}
