package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.MessageBuilder;
import org.helmo.SpeakerConfig;
import org.helmo.crypto.CryptoProvider;
import org.helmo.crypto.exceptions.EncryptionException;
import org.helmo.domain.Domain;
import org.helmo.domain.DomainService;
import org.helmo.exceptions.DomainReaderException;
import org.helmo.exceptions.DomainSaveException;
import org.helmo.exceptions.HandlerException;
import org.helmo.network.SpeakerTcpConnector;
import org.helmo.repository.IDomainRepository;

import java.util.List;

/**
 * Gère la commande POST pour relayer des messages entre les domaines.
 */
 public class PostHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private final SpeakerTcpConnector connector;
    private final CryptoProvider crypto;
    private final DomainService domainService;
    private final IDomainRepository domainRepository;
    private final SpeakerConfig speakerConfig;

    /**
     * Initialise une nouvelle instance de PostHandler.
     *
     * @param connector      Le connecteur TCP pour envoyer des messages aux nœuds.
     * @param cryptoProvider Le fournisseur de services de cryptographie.
     * @param domainService  Le service de gestion des domaines.
     * @param jsonDomainRepository Le dépôt pour sauvegarder les messages en attente.
     * @param speakerConfig  La configuration locale du Speaker.
     */
    public PostHandler(SpeakerTcpConnector connector, CryptoProvider cryptoProvider, DomainService domainService, IDomainRepository jsonDomainRepository, SpeakerConfig speakerConfig) {
        this.connector = connector;
        this.crypto = cryptoProvider;
        this.domainService = domainService;
        this.domainRepository = jsonDomainRepository;
        this.speakerConfig = speakerConfig;
    }

    /**
     * Traite la commande POST en rechiffrant le message pour le domaine de destination.
     *
     * @param args La liste des arguments contenant les informations du message (id, source, destination, contenu).
     * @throws HandlerException Si une erreur survient lors du traitement ou de l'envoi du message.
     */
    @Override
    public void handle(List<String> args) throws HandlerException {
        String idAtDomain = args.get(0);
        String userSrc = args.get(1);
        String dest = args.get(4);
        String domainDest = args.get(6);
        String plainText = args.getLast();

        try {
            // Récupérer la clé du domaine destination
            Domain destDomain = domainService.findDomain(domainDest);
            if (destDomain == null) {
                throw new HandlerException("Domaine destination inconnu : " + domainDest);
            }

            // Reconstruire et envoyer le message POST
            String post = MessageBuilder.buildPost(idAtDomain, userSrc, dest, plainText);

            if (connector.containsNode(destDomain)) {
                // Rechiffrer avec la clé destination
                String destKey = destDomain.getAesKey();
                String reEncrypted = crypto.encrypt(post, destKey);
                LOGGER.info("Message rechiffré et envoyé à {}", domainDest);
                connector.sendToNode(domainDest, reEncrypted);
            } else {
                String localEncrypted = crypto.encrypt(post, speakerConfig.aesKey());
                LOGGER.info("Node absent, enregistrement du message {}", domainDest);
                domainRepository.saveMessage(destDomain.getName(), localEncrypted);
            }
        } catch (DomainReaderException e) {
            throw new HandlerException("Impossible de lire les domaines : " + e.getMessage());
        } catch (EncryptionException e) {
            throw new HandlerException("Impssible d'encrypter le message : " + e.getMessage());
        } catch (DomainSaveException e) {
            throw new HandlerException("Impossible de sauvegarder le message : " + e.getMessage());
        }
    }
}
