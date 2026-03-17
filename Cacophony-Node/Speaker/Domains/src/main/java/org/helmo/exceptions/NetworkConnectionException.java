package org.helmo.exceptions;

/**
 * Exception levée lorsqu'une erreur de connexion réseau se produit.
 */
public class NetworkConnectionException extends RuntimeException {
    public NetworkConnectionException(String message) {
        super(message);
    }

    public NetworkConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
