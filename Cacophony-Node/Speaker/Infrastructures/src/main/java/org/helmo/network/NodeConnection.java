package org.helmo.network;

/**
 * Interface pour la gestion d'une connexion TCP avec un Node.
 */
public interface NodeConnection {
    void sendMessage(byte[] message);
    void close();
    String getDomain();
}

