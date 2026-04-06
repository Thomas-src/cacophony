package org.helmo.exceptions;

/**
 * Exception levée lorsqu'un domaine configuré est introuvable.
 */
public class DomainNotFoundException extends RuntimeException {
    /**
     * Construit une nouvelle exception avec le message détaillé spécifié.
     *
     * @param message le message détaillé
     */
    public DomainNotFoundException(String message) {
        super(message);
    }

    /**
     * Construit une nouvelle exception avec le message détaillé et la cause spécifiés.
     *
     * @param message le message détaillé
     * @param cause la cause de l'exception
     */
    public DomainNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
