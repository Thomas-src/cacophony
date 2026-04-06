package org.helmo.Exception;

import java.io.IOException;

/**
 * Exception levée lorsqu'une erreur survient lors de la sauvegarde d'un message.
 */
public class MessageSaveException extends IOException {
    /**
     * Construit une nouvelle exception avec le message de détail spécifié.
     *
     * @param message le message de détail.
     */
    public MessageSaveException(String message) {
        super(message);
    }
}
