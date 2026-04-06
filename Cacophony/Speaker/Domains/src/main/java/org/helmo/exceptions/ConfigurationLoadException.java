package org.helmo.exceptions;

/**
 * Exception levée lorsqu'une erreur se produit lors du chargement de la configuration.
 */
public class ConfigurationLoadException extends RuntimeException {
    /**
     * Construit une nouvelle exception avec le message détaillé spécifié.
     *
     * @param message le message détaillé.
     */
    public ConfigurationLoadException(String message) {
        super(message);
    }

    /**
     * Construit une nouvelle exception avec le message détaillé et la cause spécifiés.
     *
     * @param message le message détaillé.
     * @param cause la cause de l'exception.
     */
    public ConfigurationLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
