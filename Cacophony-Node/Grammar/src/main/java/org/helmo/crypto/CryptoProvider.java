package org.helmo.crypto;

import org.helmo.crypto.exceptions.DecryptionException;
import org.helmo.crypto.exceptions.EncryptionException;

/**
 * Interface fournissant des opérations cryptographiques pour AES-256 GCM.
 *
 * Cette interface définit les méthodes requises pour le chiffrement,
 * le déchiffrement et la génération de clés. Elle est destinée à être
 * implémentée par des classes fournissant des services cryptographiques concrets.
 */
public interface CryptoProvider {
    /**
     * Chiffre un texte en clair en utilisant AES-256 GCM.
     *
     * @param plainText le texte en clair à chiffrer
     * @param base64Key la clé de chiffrement encodée en Base64
     * @return les données chiffrées sous forme de chaîne de caractères
     * @throws IllegalArgumentException si les paramètres sont invalides ou null
     * @throws EncryptionException      si une erreur se produit lors du chiffrement
     */
    String encrypt(String plainText, String base64Key) throws IllegalArgumentException, EncryptionException;

    /**
     * Déchiffre des données chiffrées en utilisant AES-256 GCM.
     *
     * @param encryptedData les données chiffrées à déchiffrer
     * @param base64Key     la clé de déchiffrement encodée en Base64
     * @return le texte en clair déchiffré
     * @throws IllegalArgumentException si les paramètres sont invalides ou null
     * @throws DecryptionException      si une erreur se produit lors du déchiffrement
     */
    String decrypt(String encryptedData, String base64Key) throws IllegalArgumentException, DecryptionException;


}
