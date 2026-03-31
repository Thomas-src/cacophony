package org.helmo.Exception;

import java.io.IOException;

/**
 * Exception personnalisée pour la gestion des erreurs au sein des gestionnaires (handlers).
 */
public class HandlerException extends Exception {
    /**
     * Construit une nouvelle exception avec le message de détail spécifié.
     *
     * @param message le message de détail.
     */
    public HandlerException(String message) {
        super(message);
    }
    /**
     * Construit une nouvelle exception avec le message de détail et la cause spécifiés.
     *
     * @param message le message de détail.
     * @param cause la cause de l'exception.
     */
    public HandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}
