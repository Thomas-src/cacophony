package org.helmo.Exception;

/**
 * Exception lancée lors d'une erreur de lecture de la configuration.
 */
public class ConfigReaderException extends Exception {
    /**
     * Construit une nouvelle exception avec un message détaillé.
     * @param message le message détaillé.
     */
    public ConfigReaderException(String message) {
        super(message);
    }
}
