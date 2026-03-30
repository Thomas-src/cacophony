package org.helmo.crypto;

import org.helmo.crypto.exceptions.DecryptionException;
import org.helmo.crypto.exceptions.EncryptionException;

/**
 * Interface fournissant des opérations cryptographiques pour AES-256 GCM.
 * <p>
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
     */
    String encrypt(String plaintext, String base64Key) throws EncryptionException;

    /**
     * Déchiffre les données fournies à l'aide de la clé spécifiée
     * en utilisant l'algorithme AES-256 en mode GCM.
     **/
    String decrypt(String encryptedData, String base64Key) throws DecryptionException;

}
