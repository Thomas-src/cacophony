package org.helmo.domain;

/**
 * Interface pour le routage des messages entre domaines.
 */
public interface MessageRouter {
    void routePostMessage(String fromDomain, String toDomain, byte[] encryptedMessage);
}

