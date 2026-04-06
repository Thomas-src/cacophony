package org.helmo.Exception;

import java.io.IOException;

/**
 * Exception levée lors d'une erreur de lecture d'un message.
 */
public class MessageReaderException extends IOException {

    /**
     * Construit une nouvelle exception avec le message de détail spécifié.
     * @param message le message de détail.
     */
    public MessageReaderException(String message) {
        super(message);
    }
}
