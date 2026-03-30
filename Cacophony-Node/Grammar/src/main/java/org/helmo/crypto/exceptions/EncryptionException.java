package org.helmo.crypto.exceptions;

/**
 * Exception levée lorsqu'une erreur se produit lors du chiffrement des données.
 */
public class EncryptionException extends Exception {
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
    public EncryptionException(String message) {
        super(message);
    }

    // TODO: Envisager d'ajouter des codes d'erreur pour distinguer les différents types d'erreurs de chiffrement (ex: clé invalide, algorithme non supporté, etc.).
}
