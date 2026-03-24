package org.helmo.network;

/**
 * Interface pour la gestion d'une connexion TCP avec un Node.
 */
public interface NodeConnection {
    void sendMessage(String line);
    void close();
    String getDomain();
}

