package org.helmo.infrastructure.exceptions;

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

    // TODO: Envisager d'ajouter des codes d'erreur pour distinguer les différents types d'erreurs de configuration (ex: fichier non trouvé, JSON invalide, etc.).
}
