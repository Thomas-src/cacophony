package org.helmo.exceptions;

/**
 * Exception levée lorsqu'un domaine configuré est introuvable.
 */
public class DomainNotFoundException extends RuntimeException {
    public DomainNotFoundException(String message) {
        super(message);
    }

    public DomainNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
