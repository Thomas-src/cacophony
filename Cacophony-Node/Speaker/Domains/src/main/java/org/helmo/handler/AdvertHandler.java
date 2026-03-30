package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.domain.Domain;
import org.helmo.domain.DomainService;
import org.helmo.exceptions.DomainReaderException;
import org.helmo.network.SpeakerTcpConnector;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

public class AdvertHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private final DomainService domainService;
    private final SpeakerTcpConnector connector;
    public AdvertHandler(SpeakerTcpConnector connector, DomainService domainService) {
        this.connector = connector;
        this.domainService = domainService;
    }

    @Override
    public void handle(List<String> args)  {
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

        } catch (UnknownHostException e) {
            LOGGER.info("Erreur domaine inconnu : {}", e.getMessage());
        } catch (DomainReaderException e) {
            LOGGER.info("Erreur lors de l'obtention des domaines : {}", e.getMessage());
        }
    }
}
