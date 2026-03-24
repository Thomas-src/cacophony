package org.helmo.repository;

import org.helmo.domain.Domain;
import org.helmo.exceptions.DomainReaderException;

import java.util.List;

public interface IDomainRepository {
    List<Domain> getDomains() throws DomainReaderException;



}
