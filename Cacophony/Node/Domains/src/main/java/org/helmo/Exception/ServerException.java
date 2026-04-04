package org.helmo.Exception;

import java.io.IOException;

/**
 * Exception personnalisée pour gérer les erreurs liées au serveur.
 */
public class ServerException extends IOException {
    /**
     * Exception levée lors d'une erreur serveur.
     *
     * @param message Le message d'erreur.
     */
    public ServerException(String message) {
        super(message);
    }
}
