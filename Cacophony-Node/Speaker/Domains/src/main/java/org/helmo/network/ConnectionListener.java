package org.helmo.network;

/**
 * Interface définissant un écouteur pour les événements de connexion.
 */
public interface ConnectionListener {
    /**
     * Méthode appelée lorsqu'une connexion est fermée.
     *
     * @param domainName Le nom du domaine dont la connexion a été fermée.
     */
    void onConnectionClosed(String domainName);
}