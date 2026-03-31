package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.MessageBuilder;
import org.helmo.SpeakerConfig;
import org.helmo.crypto.CryptoService;
import org.helmo.crypto.exceptions.EncryptionException;
import org.helmo.domain.Domain;
import org.helmo.domain.DomainService;
import org.helmo.exceptions.DomainReaderException;
import org.helmo.network.SpeakerTcpConnector;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class AdvertHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private final DomainService domainService;
    private final SpeakerTcpConnector connector;
    private final CryptoService cryptoService;
    private final SpeakerConfig speakerConfig;

    public AdvertHandler(SpeakerTcpConnector connector, DomainService domainService, CryptoService cryptoService, SpeakerConfig speakerConfig) {
        this.connector = connector;
        this.domainService = domainService;
        this.cryptoService = cryptoService;
        this.speakerConfig = speakerConfig;
    }

    @Override
    public void handle(List<String> args) {
        LOGGER.info("Execution de advert: {}", args);
        String port = args.get(0);
        String domain = args.get(1);

        try {
            Domain d = domainService.findDomain(domain);
            if (d == null) {
                LOGGER.info("Domaine inconnu, ignoré : {}", domain);
                return;
            } else if (connector.containsNode(d)) {
                LOGGER.info("domaine déjà connecté {}", domain);
                return;
            }
            // résoudre l'ip depuis le domaine
            String ip = InetAddress.getByName(domain).getHostAddress();
            connector.connectToNode(d, ip, Integer.parseInt(port));

            for (String cryptedMessage : d.getMessages()) {
                try {
                    // todo supprimer ligne hardcode et décommenter la variable decryptedMessage
                    String decryptedMessage = MessageBuilder.buildPost("1000@node1.swilabus.xyz", "thomas@node1.swilabus.xyz","tomsy@node1.swilabus.xyz", "MSG tomsy@node1.swilabus.xyz SALUT #monTag1" );
                    String ms = cryptoService.encrypt(decryptedMessage, speakerConfig.aesKey()); // todo delete
                    LOGGER.info("Msg (pas) enregistré : \n{}", ms); // todo delete


                    // 1. décrypter le msg avec la clé serveur
                    // LOGGER.info("Msg chiffré enregistré : {}", cryptedMessage);
                    // String decryptedMessage = cryptoService.decrypt(cryptedMessage, speakerConfig.aesKey());
                    LOGGER.info("Msg clair : {}", decryptedMessage);
                    //2. crypter avec la clé du node approprié
                    LOGGER.info("Longueur message envoyé: {}", decryptedMessage.length());
                    String cryptedNodeMsg = cryptoService.encrypt(decryptedMessage, d.getAesKey());
                    LOGGER.info("Msg crypté à envoyer avec la clé {}\n{}", d.getAesKey(), cryptedNodeMsg);
                    connector.sendToNode(d.getName(), cryptedNodeMsg);
                } catch (EncryptionException e) {
                    LOGGER.error("Erreur de decryption du message : {}. Résultat :{}", cryptedMessage, e);
                }

            }

        } catch (UnknownHostException e) {
            LOGGER.info("Erreur domaine inconnu : {}", e.getMessage());
        } catch (DomainReaderException e) {
            LOGGER.info("Erreur lors de l'obtention des domaines : {}", e.getMessage());
        }
    }
}
