package org.helmo.domain;

/**
 * DTO pour représenter un message ADVERT reçu en multicast.
 */
public class AdvertMessage {
    private final String domain;
    private final int port;

    public AdvertMessage(String domain, int port) {
        this.domain = domain;
        this.port = port;
    }

    public String getDomain() {
        return domain;
    }

    public int getPort() {
        return port;
    }
}

