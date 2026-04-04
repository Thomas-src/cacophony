package org.helmo.exceptions;

/**
 * Exception levée lorsqu'une erreur se produit lors du chargement de la configuration.
 */
public class ConfigurationLoadException extends RuntimeException {
    public ConfigurationLoadException(String message) {
        super(message);
    }

    public ConfigurationLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
