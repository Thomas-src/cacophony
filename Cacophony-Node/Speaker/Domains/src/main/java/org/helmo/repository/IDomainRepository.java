package org.helmo.repository;

import org.helmo.domain.Domain;
import org.helmo.exceptions.DomainReaderException;
import org.helmo.exceptions.DomainSaveException;

import java.util.List;

public interface IDomainRepository {
    List<Domain> getDomains() throws DomainReaderException;

    void saveMessages(String domain, List<String> messages) throws DomainSaveException;
}
