package org.helmo.exceptions;

/**
 * Exception levée lorsqu'une erreur de connexion réseau se produit.
 */
public class NetworkConnectionException extends RuntimeException {
    /**
     * Construit une nouvelle exception avec le message détaillé spécifié.
     *
     * @param message le message détaillé.
     */
    public NetworkConnectionException(String message) {
        super(message);
    }

    /**
     * Construit une nouvelle exception avec le message détaillé et la cause spécifiés.
     *
     * @param message le message détaillé.
     * @param cause la cause de l'exception.
     */
    public NetworkConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
