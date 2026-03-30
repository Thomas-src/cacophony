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
        List<Domain> users = getDomains();
        for (Domain d : users) {
            if (d.getName().equals(domain)) {
                return d;
            }
        }
        return null;
    }

    public List<String> getWaitingMessages(String domainName) throws DomainReaderException {
        Domain domain = findDomain(domainName);
        if (domain == null) return List.of();

        List<String> decrypted = new ArrayList<>();
        for (String encrypted : domain.getMessages()) {
            try {
                decrypted.add(crypto.decrypt(encrypted, speakerConfig.aesKey()));
            } catch (DecryptionException e) {
                LOGGER.warn("Impossible de déchiffrer un message du domaine {}: {}", domainName, e.getMessage());
            }
        }
        return decrypted;
    }

    public void saveWaitingMessages(String domainName, List<String> messages) throws DomainReaderException, DomainSaveException {
        Domain domain = findDomain(domainName);
        if (domain == null) {
            throw new DomainReaderException("Domaine introuvable : " + domainName);
        }

        List<String> encryptedMessages = new ArrayList<>();
        for (String msg : messages) {
            try {
                encryptedMessages.add(crypto.encrypt(msg, speakerConfig.aesKey()));
            } catch (EncryptionException e) {
                LOGGER.warn("Impossible de chiffrer un message pour le domaine {}: {}", domainName, e.getMessage());
            }
        }
        domainRepository.saveMessages(domainName, encryptedMessages);
    }
}
