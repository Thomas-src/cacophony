package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.MessageBuilder;
import org.helmo.crypto.CryptoProvider;
import org.helmo.domain.Domain;
import org.helmo.domain.DomainService;
import org.helmo.exceptions.HandlerException;
import org.helmo.network.SpeakerTcpConnector;

import java.util.List;

public class PostHandler implements CommandHandler{
    private static final Logger LOGGER = LogManager.getLogger();

    private final SpeakerTcpConnector connector;
    private final CryptoProvider crypto;
    private final DomainService domainService;
    public PostHandler(SpeakerTcpConnector connector, CryptoProvider cryptoProvider, DomainService domainService) {
        this.connector = connector;
        this.crypto = cryptoProvider;
        this.domainService = domainService;

    }

    @Override
    public void handle(List<String> args) throws HandlerException {
        LOGGER.info(args.toString());
    // todo
        //  envoyer vers le node2 crypté avec sa clé
        String idAtDomain      = args.get(0);
        String userSrc         = args.get(1);
        String domainSrc       = args.get(2);
        String dest            = args.get(3);
        String domainDest      = args.get(4);
        String plainText       = args.get(5);
        try {
            // Récupérer la clé du domaine destination
            Domain destDomain = domainService.findDomain(domainDest);
            if (destDomain == null) {
                throw new HandlerException("Domaine destination inconnu : " + domainDest);
            }

            // Rechiffrer avec la clé destination
            String destKey = destDomain.getAesKey();
            String reEncrypted = crypto.encrypt(plainText, destKey);

            // Reconstruire et envoyer le message POST
            String post = MessageBuilder.buildPost(idAtDomain, userSrc + "@" + domainSrc, dest + "@" + domainDest, reEncrypted);
            connector.sendToNode(domainDest, post);
            LOGGER.info("Message rechiffré et envoyé à {}", domainDest);

        } catch (Exception e) {
            throw new HandlerException("Erreur PostHandler : " + e.getMessage());
        }
    }
}
