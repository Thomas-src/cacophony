package org.helmo.exceptions;

/**
 * Exception levée lorsqu'une erreur se produit lors du déchiffrement des données.
 */
public class DecryptionException extends RuntimeException {
    public DecryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    // TODO: Envisager d'ajouter des codes d'erreur pour distinguer les différents types d'erreurs de déchiffrement (ex: clé incorrecte, données corrompues, etc.).
}
