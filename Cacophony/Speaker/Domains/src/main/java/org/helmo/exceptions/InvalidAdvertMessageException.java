package org.helmo.exceptions;

/**
 * Exception levée lorsqu'un message publicitaire est invalide.
 */
public class InvalidAdvertMessageException extends RuntimeException {
    /**
     * Construit une nouvelle exception avec le message détaillé spécifié.
     *
     * @param message le message détaillé.
     */
    public InvalidAdvertMessageException(String message) {
        super(message);
    }

    /**
     * Construit une nouvelle exception avec le message détaillé et la cause spécifiés.
     *
     * @param message le message détaillé.
     * @param cause la cause de l'exception.
     */
    public InvalidAdvertMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
