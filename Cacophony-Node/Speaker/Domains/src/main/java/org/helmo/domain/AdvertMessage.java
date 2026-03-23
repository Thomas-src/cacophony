package org.helmo.domain;

import org.helmo.exceptions.InvalidAdvertMessageException;

/**
 * DTO pour représenter un message ADVERT reçu en multicast.
 */
public class AdvertMessage {
    private final String domain;
    private final int port;

    /**
     *
     * @param rawMessage
     */
    public AdvertMessage(String rawMessage) {
        if (rawMessage == null || !rawMessage.contains(":")) {
            throw new InvalidAdvertMessageException("The advert message must not be null and must contain a ':' separator.");
        }

        String[] parts = rawMessage.split(":", 2);
        if (parts.length != 2) {
            throw new InvalidAdvertMessageException("The advert message must be in the format 'domain:port'.");
        }

        this.domain = parts[0];
        try {
            this.port = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new InvalidAdvertMessageException("The port in the advert message must be a valid integer.", e);
        }
    }

    public String getDomain() {
        return domain;
    }

    public int getPort() {
        return port;
    }
}
