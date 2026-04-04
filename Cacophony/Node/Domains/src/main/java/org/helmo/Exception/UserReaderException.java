package org.helmo.Exception;

/**
 * Exception levée lors d'une erreur de lecture des données utilisateur.
 */
public class UserReaderException extends Exception {
    /**
     * Construit une nouvelle exception avec un message détaillé.
     *
     * @param message le message détaillé.
     */
    public UserReaderException(String message) {
        super(message);
    }
}
