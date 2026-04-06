package org.helmo.exceptions;

import java.io.IOException;

/**
 * Exception lancée lors d'une erreur au sein d'un gestionnaire.
 */
public class HandlerException extends IOException {
    /**
     * Construit une nouvelle exception avec le message détaillé spécifié.
     *
     * @param message le message détaillé.
     */
    public HandlerException(String message) {
        super(message);
    }
}
