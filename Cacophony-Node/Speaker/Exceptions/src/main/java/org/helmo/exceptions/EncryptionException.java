package org.helmo.exceptions;

/**
 * Exception levée lorsqu'une erreur se produit lors du chiffrement des données.
 */
public class EncryptionException extends RuntimeException {
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    // TODO: Envisager d'ajouter des codes d'erreur pour distinguer les différents types d'erreurs de chiffrement (ex: clé invalide, algorithme non supporté, etc.).
}
