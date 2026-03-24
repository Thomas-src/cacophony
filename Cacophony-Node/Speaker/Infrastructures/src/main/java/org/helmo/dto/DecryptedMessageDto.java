package org.helmo.dto;

import com.google.gson.JsonObject;

/**
 * DTO (Data Transfer Object) représentant un message après son déchiffrement.
 * Cette classe est utilisée pour valider la structure du message JSON.
 */
public class DecryptedMessageDto {
    // TODO: Définir les types de messages possibles sous forme d'enum pour plus de robustesse.
    // public enum MessageType { TEXT, IMAGE, ... }
    private String type;
    private JsonObject payload;

    public String getType() {
        return type;
    }

    public JsonObject getPayload() {
        return payload;
    }

    // TODO: Ajouter des constructeurs, des setters et des méthodes de validation si nécessaire.
}
