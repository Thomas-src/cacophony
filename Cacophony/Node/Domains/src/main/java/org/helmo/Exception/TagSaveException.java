package org.helmo.Exception;

/**
 * Exception levée lors d'une erreur durant la sauvegarde d'un tag.
 */
public class TagSaveException extends Exception {
    /**
     * Construit une nouvelle exception avec un message détaillé.
     *
     * @param message le message détaillé.
     */
    public TagSaveException(String message) {
        super(message);
    }
}
