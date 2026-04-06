package org.helmo.exceptions;

/**
 * Exception levée lorsqu'une erreur survient lors de la sauvegarde d'un domaine.
 */
public class DomainSaveException extends Exception {
    /**
     * Construit une nouvelle exception avec le message de détail spécifié.
     *
     * @param message le message de détail.
     */
    public DomainSaveException(String message) {
        super(message);
    }
}
