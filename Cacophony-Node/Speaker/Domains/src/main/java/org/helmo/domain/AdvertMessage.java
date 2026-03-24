package org.helmo.domain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Message;
import org.helmo.domain.exceptions.InvalidAdvertMessageException;

import java.util.regex.Matcher;


/**
 * DTO pour représenter un message ADVERT reçu en multicast.
 */
public class AdvertMessage {
    private static final Logger LOGGER = LogManager.getLogger();

    private final String domain;
    private final int port;

    public AdvertMessage(String rawMessage) {
        LOGGER.info("New advert recieved : " + rawMessage);
        if (!Message.P_ADVERT.matcher(rawMessage).matches()) {
            throw new InvalidAdvertMessageException(rawMessage);
        }

        Matcher matcher = Message.P_ADVERT.matcher(rawMessage);
        matcher.matches();

        this.port = Integer.parseInt(matcher.group(1));
        this.domain = matcher.group(2);

        LOGGER.info("Advert message is: " + rawMessage);
        LOGGER.info("Domain is: " + domain);
        LOGGER.info("Port is: " + port);
    }

    /**
     * Construit un AdvertMessage à partir d'une chaîne brute.
     * Le format attendu est "domain:port".
     */
//    public AdvertMessage(String rawMessage) {
//        if (rawMessage == null || !rawMessage.contains(":")) {
//            throw new InvalidAdvertMessageException("The advert message must not be null and must contain a ':' separator.");
//        }
//
//        String[] parts = rawMessage.split(":", 2);
//        if (parts.length != 2) {
//            throw new InvalidAdvertMessageException("The advert message must be in the format 'domain:port'.");
//        }
//
//        this.domain = parts[0];
//        try {
//            this.port = Integer.parseInt(parts[1]);
//        } catch (NumberFormatException e) {
//            throw new InvalidAdvertMessageException("The port in the advert message must be a valid integer.", e);
//        }
//    }

    public String getDomain() {
        return domain;
    }

    public int getPort() {
        return port;
    }

    // TODO: Ajouter une méthode toString() pour faciliter le logging et le débogage.
}
