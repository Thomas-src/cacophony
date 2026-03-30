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
    String encrypt(String plainText, String base64Key) throws IllegalArgumentException, EncryptionException;

    String decrypt(String encryptedData, String base64Key) throws IllegalArgumentException, DecryptionException;


}
