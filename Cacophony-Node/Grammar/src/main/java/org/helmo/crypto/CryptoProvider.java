package org.helmo.crypto;

/**
 * Interface fournissant des opérations cryptographiques pour AES-256 GCM.
 *
 * Cette interface définit les méthodes requises pour le chiffrement,
 * le déchiffrement et la génération de clés. Elle est destinée à être
 * implémentée par des classes fournissant des services cryptographiques concrets.
 */
public interface CryptoProvider {
    /**
     * Chiffre les données fournies en entrée à l'aide de la clé spécifiée
     * en utilisant l'algorithme AES-256 en mode GCM.
     *
     * @param plaintext les données en clair à chiffrer ; ne doit pas être {@code null}
     *
     * @return un tableau d'octets contenant les données chiffrées, incluant
     *         éventuellement le vecteur d'initialisation (IV) et le tag d'authentification
     * @throws IllegalArgumentException si {@code plaintext} ou {@code key} est {@code null}
     *                                  ou si la taille de la clé n'est pas valide pour AES-256
     * @throws RuntimeException si une erreur se produit lors de l'opération de chiffrement
     */
    String encrypt(String plaintext,String base64Key) throws Exception;

    /**
     * Déchiffre les données fournies à l'aide de la clé spécifiée
     * en utilisant l'algorithme AES-256 en mode GCM.
     *
     * @param encryptedData les données chiffrées à déchiffrer ; ne doit pas être {@code null}
     *                      et doit correspondre au format produit par {@link #encrypt(byte[], byte[])}
     * @param key la clé de déchiffrement symétrique correspondant à celle utilisée pour le chiffrement ;
     *            ne doit pas être {@code null}
     * @return un tableau d'octets contenant les données déchiffrées en clair
     * @throws IllegalArgumentException si {@code encryptedData} ou {@code key} est {@code null}
     *                                  ou si la taille de la clé n'est pas valide pour AES-256
     * @throws RuntimeException si une erreur se produit lors de l'opération de déchiffrement
     *                          ou si les données chiffrées sont invalides ou altérées
     */
    byte[] decrypt(byte[] encryptedData, byte[] key);

}
