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

    void saveMessage(String domain, String message) throws DomainSaveException;
    void clearDomainMessages(String domain) throws DomainSaveException;
}
