package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.domain.Domain;
import org.helmo.exceptions.HandlerException;
import org.helmo.network.SpeakerTcpConnector;

import java.util.List;
import java.util.Map;

public class AdvertHandler implements CommandHandler{
    private static final Logger LOGGER = LogManager.getLogger();

    private final SpeakerTcpConnector connector;
    private final Map<String, Domain> speakerDomains;
    public AdvertHandler(SpeakerTcpConnector connector, Map<String, Domain> speakerDomains) {
        this.connector = connector;
        this.speakerDomains = speakerDomains;
    }

    @Override
    public String handle(List<String> args) throws HandlerException {
        LOGGER.info("Execution de advert: {}", args);
        String port = args.get(0);
        String domain = args.get(1);
        Domain d = speakerDomains.get(domain);
        if (d == null) {
            LOGGER.info("Domaine inconnu, ignoré : {}", domain);
            return ""; // spec : silence si inconnu
        }
        // connector.connectToNode(domain, ip, Integer.parseInt(port), d.getAesKey()); // ip nécessaire ?
        return "";
    }
}
