package org.helmo.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.exceptions.DomainReaderException;
import org.helmo.repository.IDomainRepository;

import java.util.List;

public class DomainService {
    private static final Logger LOGGER = LogManager.getLogger();

    private final IDomainRepository domainRepository;

    public DomainService(IDomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    public List<Domain> getDomains() throws DomainReaderException {
        return domainRepository.getDomains();
    }

    public Domain findDomain(String domain) throws DomainReaderException {
        List<Domain> users = getDomains();
        for (Domain d : users) {
            if (d.getName().equals(domain)) {
                return d;
            }
        }
        return null;
    }
}
