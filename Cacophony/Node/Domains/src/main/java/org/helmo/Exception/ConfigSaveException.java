package org.helmo.Exception;

/**
 * Exception lancée lors d'une erreur de lecture de la configuration.
 */
public class ConfigSaveException extends Exception {
    /**
     * Construit une nouvelle exception avec un message détaillé.
     * @param message le message détaillé.
     */
    public ConfigSaveException(String message) {
        super(message);
    }
}
