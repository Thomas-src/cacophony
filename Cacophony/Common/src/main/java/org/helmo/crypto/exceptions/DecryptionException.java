package org.helmo.crypto.exceptions;

/**
 * Exception levée lorsqu'une erreur se produit lors du déchiffrement de données.
 *
 * Cette exception est utilisée pour signaler les problèmes rencontrés lors de la tentative
 * de déchiffrement de données, tels que des clés invalides, des données corrompues ou
 * d'autres erreurs liées au déchiffrement.
 *
 * Elle fournit des constructeurs pour définir un message spécifique, une cause, ou les deux,
 * facilitant ainsi le diagnostic et le débogage des problèmes de déchiffrement.
 */
public class DecryptionException extends Exception {
    /**
     * Construit une nouvelle DecryptionException avec un message détaillé et une cause spécifiés.
     *
     * Ce constructeur permet la création d'une DecryptionException avec à la fois un message
     * personnalisé pour décrire l'erreur de déchiffrement et une cause (un autre throwable) qui
     * a déclenché cette exception, fournissant ainsi plus de contexte pour le débogage.
     *
     * @param message le message détaillé décrivant l'erreur de déchiffrement
     * @param cause   la cause de cette exception, qui peut être récupérée en utilisant la méthode {@code getCause()}
     */
    public DecryptionException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * Construit une nouvelle DecryptionException avec un message détaillé spécifié.
     *
     * @param message le message détaillé pour décrire l'erreur de déchiffrement
     */
    public DecryptionException(String message) {
        super(message);
    }
}
