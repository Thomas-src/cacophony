package org.helmo.Exception;

/**
 * Exception levée lors d'une erreur de lecture des tags.
 */
public class TagReaderException extends Exception {
    /**
     * Construit une nouvelle exception avec le message détaillé spécifié.
     *
     * @param message le message détaillé.
     */
    public TagReaderException(String message) {
        super(message);
    }
}
