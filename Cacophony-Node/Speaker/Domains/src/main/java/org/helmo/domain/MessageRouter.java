package org.helmo.domain;

/**
 * Interface pour le routage des messages entre domaines.
 */
public interface MessageRouter {
    // TODO: Envisager d'ajouter des méthodes pour router différents types de messages .
    void routePostMessage(String fromDomainName, String toDomainName, byte[] encryptedMessage);

    void validateDecryptedMessage(byte[] decryptedMessage);

    // TODO: Ajouter une méthode pour gérer la réception des messages ADVERT et mettre à jour les informations des domaines.
    // void handleAdvertMessage(AdvertMessage advert, String sourceIp);
}
