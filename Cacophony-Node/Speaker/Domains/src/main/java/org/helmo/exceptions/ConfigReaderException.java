package org.helmo.exceptions;

/**
 * Exception levée lors d'erreurs de lecture de la configuration.
 * Cette exception est utilisée pour signaler des problèmes rencontrés
 * pendant le chargement ou l'analyse des fichiers de configuration.
 */
public class ConfigReaderException extends Exception {
    /**
     * Construit une nouvelle exception avec le message spécifié.
     *
     * @param message le message détaillant l'erreur de lecture de configuration
     */
    public ConfigReaderException(String message) {
        super(message);
    }

    /**
     * Construit une nouvelle exception avec le message spécifié et la cause.
     *
     * @param message le message détaillant l'erreur de lecture de configuration
     * @param cause   la cause de l'exception (peut être null)
     */
    public ConfigReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construit une nouvelle exception avec la cause spécifiée.
     *
     * @param cause la cause de l'exception
     */
    public ConfigReaderException(Throwable cause) {
        super(cause);
    }

    /**
     * Construit une nouvelle exception avec un message par défaut.
     * Le message par défaut est "Error reading configuration".
     */
    public ConfigReaderException() {
        	super("Error reading configuration");
    }
}
