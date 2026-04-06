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

/**
 * Service gérant la logique métier liée aux domaines et à leurs messages.
 */
public class DomainService {
    private static final Logger LOGGER = LogManager.getLogger();

    private final IDomainRepository domainRepository;
    private final CryptoProvider crypto;
    private final SpeakerConfig speakerConfig;

    /**
     * Initialise une nouvelle instance du service de domaine.
     *
     * @param domainRepository Le dépôt pour l'accès aux données des domaines.
     * @param crypto           Le fournisseur de services cryptographiques.
     * @param speakerConfig    La configuration de l'application.
     */
    public DomainService(IDomainRepository domainRepository, CryptoProvider crypto, SpeakerConfig speakerConfig) {
        this.domainRepository = domainRepository;
        this.crypto = crypto;
        this.speakerConfig = speakerConfig;
    }

    /**
     * Récupère la liste de tous les domaines disponibles.
     *
     * @return Une liste d'objets {@link Domain}.
     * @throws DomainReaderException Si une erreur survient lors de la lecture des domaines.
     */
    public List<Domain> getDomains() throws DomainReaderException {
        return domainRepository.getDomains();
    }

    /**
     * Recherche un domaine spécifique par son nom.
     *
     * @param domain Le nom du domaine à rechercher.
     * @return L'objet {@link Domain} correspondant, ou null s'il n'est pas trouvé.
     * @throws DomainReaderException Si une erreur survient lors de la recherche.
     */
    public Domain findDomain(String domain) throws DomainReaderException {
        List<Domain> domains = getDomains();
        for (Domain d : domains) {
            if (d.getName().equals(domain)) {
                return d;
            }
        }
        return null;
    }

    /**
     * Chiffre et sauvegarde un message pour un domaine donné.
     *
     * @param domainName Le nom du domaine associé au message.
     * @param message    Le contenu du message en clair.
     */
    public void saveMessage(String domainName, String message) {
        try {
            String localEncrypted = crypto.encrypt(message, speakerConfig.aesKey());
            domainRepository.saveMessage(domainName, localEncrypted);
        } catch (EncryptionException | DomainSaveException e) {
            LOGGER.error("Impossible de chiffrer / sauvegarder le message pour le domaine {}: {}", domainName, e.getMessage());
        }
    };

    /**
     * Récupère les messages d'un domaine puis les supprime du dépôt.
     *
     * @param domainName Le nom du domaine à vider.
     * @return La liste des messages (chiffrés) qui étaient présents.
     * @throws DomainReaderException Si le domaine ne peut pas être lu.
     */
    public List<String> clearMessages(String domainName) throws DomainReaderException {
       try {
           List<String> messages = new ArrayList<>(findDomain(domainName).getMessages());
           domainRepository.clearDomainMessages(domainName);
           return messages;
       } catch (DomainSaveException e) {
           LOGGER.error("Impossible de vider tous les messages du domaine {}: {}", domainName, e.getMessage());
       }
       return  List.of();
    }
}
