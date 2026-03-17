package org.helmo.domain;

/**
 * DTO pour représenter un message POST à router.
 */
public class PostMessage {
    private final String fromDomain;
    private final String toDomain;
    private final byte[] payload;

    public PostMessage(String fromDomain, String toDomain, byte[] payload) {
        this.fromDomain = fromDomain;
        this.toDomain = toDomain;
        this.payload = payload;
    }

    public String getFromDomain() {
        return fromDomain;
    }

    public String getToDomain() {
        return toDomain;
    }

    public byte[] getPayload() {
        return payload;
    }
}

