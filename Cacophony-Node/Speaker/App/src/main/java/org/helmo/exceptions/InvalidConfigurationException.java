package org.helmo.exceptions;

/**
 * Exception levée lorsque la configuration est invalide.
 */
public class InvalidConfigurationException extends RuntimeException {
    public InvalidConfigurationException(String message) {
        super(message);
    }

    public InvalidConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
