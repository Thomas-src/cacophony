package org.helmo.exceptions;

/**
 * Exception levée lors d'une erreur de lecture des domaines.
 * Cette exception est utilisée pour signaler des problèmes lors de la lecture
 * de la configuration des domaines depuis une source de données.
 */
public class DomainReaderException extends Exception {
    /**
     * Constructeur de l'exception avec un message d'erreur.
     *
     * @param s le message d'erreur décrivant la cause de l'exception
     */
    public DomainReaderException(String s) {
        super(s);
    }
}
