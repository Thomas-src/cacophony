package org.helmo.dto;

/**
 * Représente le modèle de données pour un message déchiffré.
 */
public class DecryptedMessageDto {
    private String type;
    private String payload;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
