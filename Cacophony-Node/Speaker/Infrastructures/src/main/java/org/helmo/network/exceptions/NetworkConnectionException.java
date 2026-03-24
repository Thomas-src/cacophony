package org.helmo.network.exceptions;

/**
 * Exception levée lorsqu'une erreur de connexion réseau se produit.
 */
public class NetworkConnectionException extends RuntimeException {
    public NetworkConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    // TODO: Envisager d'ajouter des codes d'erreur pour distinguer les différents types d'erreurs de connexion (ex: timeout, connexion refusée, etc.).
}
