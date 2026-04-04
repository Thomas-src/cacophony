package org.helmo.crypto.exceptions;

/**
 * Exception levée lorsqu'une erreur se produit lors des processus de chiffrement.
 *
 * Cette exception est utilisée pour signaler les problèmes rencontrés pendant le 
 * processus de chiffrement, tels que des clés invalides, des données corrompues ou 
 * d'autres erreurs liées au chiffrement.
 *
 * Elle fournit des constructeurs pour définir un message spécifique, une cause, ou les deux, 
 * facilitant l'identification et le débogage des problèmes de chiffrement.
 */
public class EncryptionException extends Exception {
    /**
     * Construit une nouvelle EncryptionException avec le message détaillé et la cause spécifiés.
     *
     * Ce constructeur permet la création d'une EncryptionException avec à la fois un 
     * message personnalisé décrivant l'erreur de chiffrement et une cause (un autre throwable) 
     * qui a déclenché cette exception, fournissant un contexte supplémentaire pour le débogage.
     *
     * @param message le message détaillé décrivant l'erreur de chiffrement
     * @param cause   la cause de cette exception, qui peut être récupérée en utilisant la méthode {@code getCause()}
     */
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * Construit une nouvelle EncryptionException avec le message détaillé spécifié.
     *
     * Ce constructeur permet la création d'une EncryptionException avec un 
     * message personnalisé décrivant l'erreur de chiffrement rencontrée.
     *
     * @param message le message détaillé décrivant l'erreur de chiffrement
     */
    public EncryptionException(String message) {
        super(message);
    }
}
