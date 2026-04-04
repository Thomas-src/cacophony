package org.helmo.domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.SpeakerConfig;
import org.helmo.crypto.CryptoProvider;
import org.helmo.crypto.exceptions.DecryptionException;
import org.helmo.crypto.exceptions.EncryptionException;
import org.helmo.exceptions.DomainReaderException;
import org.helmo.exceptions.DomainSaveException;
import org.helmo.repository.IDomainRepository;

import java.util.ArrayList;
import java.util.List;

public class DomainService {
    private static final Logger LOGGER = LogManager.getLogger();

    private final IDomainRepository domainRepository;
    private final CryptoProvider crypto;
    private final SpeakerConfig speakerConfig;

    public DomainService(IDomainRepository domainRepository, CryptoProvider crypto, SpeakerConfig speakerConfig) {
        this.domainRepository = domainRepository;
        this.crypto = crypto;
        this.speakerConfig = speakerConfig;
    }

    public List<Domain> getDomains() throws DomainReaderException {
        return domainRepository.getDomains();
    }

    public Domain findDomain(String domain) throws DomainReaderException {
        List<Domain> domains = getDomains();
        for (Domain d : domains) {
            if (d.getName().equals(domain)) {
                return d;
            }
        }
        return null;
    }

    public void saveMessage(String domainName, String message) {
        try {
            String localEncrypted = crypto.encrypt(message, speakerConfig.aesKey());
            domainRepository.saveMessage(domainName, localEncrypted);
        } catch (EncryptionException | DomainSaveException e) {
            LOGGER.warn("Impossible de chiffrer / sauvegarder le message pour le domaine {}: {}", domainName, e.getMessage());
        }
    };

    public List<String> clearMessages(String domainName) throws DomainReaderException {
       try {
           List<String> messages = new ArrayList<>(findDomain(domainName).getMessages());
           domainRepository.clearDomainMessages(domainName);
           return messages;
       } catch (DomainSaveException e) {
           LOGGER.warn("Impossible de vider tous les messages du domaine {}: {}", domainName, e.getMessage());
       }
       return  List.of();
    }

}
