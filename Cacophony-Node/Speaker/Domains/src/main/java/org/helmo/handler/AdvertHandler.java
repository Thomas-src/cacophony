package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.domain.Domain;
import org.helmo.network.SpeakerTcpConnector;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

public class AdvertHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private final SpeakerTcpConnector connector;
    private final Map<String, Domain> speakerDomains;
    public AdvertHandler(SpeakerTcpConnector connector, Map<String, Domain> speakerDomains) {
        this.connector = connector;
        this.speakerDomains = speakerDomains;
    }

    @Override
    public void handle(List<String> args)  {
        LOGGER.info("Execution de advert: {}", args);
        String port = args.get(0);
        String domain = args.get(1);
        Domain d = speakerDomains.get(domain);
        if (d == null) {
            LOGGER.info("Domaine inconnu, ignoré : {}", domain);
            return;
        } else if (connector.containsNode(domain)) {
            LOGGER.info("domaine déjà connecté {}", domain);
            return;
        }
        try {
            // résoudre l'ip depuis le domaine
            String ip = InetAddress.getByName(domain).getHostAddress();
            connector.connectToNode(domain, ip, Integer.parseInt(port), d.getAesKey());


        } catch (UnknownHostException e) {
            LOGGER.info("Erreur domaine inconnu : {}", e.getMessage());
        }
    }
}
