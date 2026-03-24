package org.helmo.exceptions;

/**
 * Exception levée lorsqu'un message ADVERT est invalide.
 */
public class InvalidAdvertMessageException extends RuntimeException {
    public InvalidAdvertMessageException(String message) {
        super(message);
    }

    public InvalidAdvertMessageException(String message, Throwable cause) {
        super(message, cause);
    }

    // TODO: Envisager d'ajouter des codes d'erreur pour distinguer les différents types d'erreurs de format de message ADVERT.
}
