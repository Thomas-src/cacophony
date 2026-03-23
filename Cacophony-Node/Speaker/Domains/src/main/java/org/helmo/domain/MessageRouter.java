package org.helmo.domain;

/**
 * Interface pour le routage des messages entre domaines.
 */
public interface MessageRouter {
    void routePostMessage(String fromDomainName, String toDomainName, byte[] encryptedMessage);

    void validateDecryptedMessage(byte[] decryptedMessage);
}
