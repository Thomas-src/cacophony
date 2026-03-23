package org.helmo.security;

import org.helmo.exceptions.DecryptionException;
import org.helmo.exceptions.EncryptionException;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

/**
 * Service qui implémente l'interface CryptoProvider pour fournir des fonctionnalités
 * de chiffrement et déchiffrement utilisant AES-256 GCM (Galois/Counter Mode).
 * Cette classe permet également de générer des clés aléatoires adaptées à l'algorithme utilisé.
 * Le chiffrement inclut un vecteur d'initialisation (IV) aléatoire pour garantir l'unicité
 * des sorties même avec des données en entrée identiques.
 */
public class CryptoService implements CryptoProvider {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_LENGTH_BYTES = 12; // 96 bits, recommandé pour GCM
    private static final int TAG_LENGTH_BITS = 128;


    @Override
    public byte[] encrypt(byte[] plaintext, byte[] key) {
        try {
            // 1. Générer un IV (Initialization Vector) aléatoire de 12 octets
            final byte[] iv = new byte[IV_LENGTH_BYTES];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            // 2. Initialiser le Cipher avec AES/GCM/NoPadding
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            final SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmParameterSpec);

            // 3. Effectuer le chiffrement
            final byte[] cipherText = cipher.doFinal(plaintext);

            // 4. Concaténer l'IV et le texte chiffré pour le retour
            final byte[] encryptedData = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, encryptedData, 0, iv.length);
            System.arraycopy(cipherText, 0, encryptedData, iv.length, cipherText.length);

            return encryptedData;
        } catch (GeneralSecurityException e) {
            throw new EncryptionException("Erreur de sécurité lors du chiffrement des données", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] encryptedData, byte[] key) {
        try {
            if (encryptedData == null || encryptedData.length < IV_LENGTH_BYTES) {
                throw new DecryptionException("Données chiffrées invalides : taille insuffisante pour contenir l'IV.", null);
            }
            // 1. Extraire l'IV du début du tableau de bytes
            final byte[] iv = new byte[IV_LENGTH_BYTES];
            System.arraycopy(encryptedData, 0, iv, 0, iv.length);

            // Le reste est le texte chiffré
            final byte[] cipherText = new byte[encryptedData.length - iv.length];
            System.arraycopy(encryptedData, iv.length, cipherText, 0, cipherText.length);

            // 2. Initialiser le Cipher pour le déchiffrement
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            final SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmParameterSpec);

            // 3. Effectuer le déchiffrement
            return cipher.doFinal(cipherText);
        } catch (GeneralSecurityException e) {
            throw new DecryptionException("Erreur de sécurité lors du déchiffrement. La clé est peut-être incorrecte ou les données sont corrompues.", e);
        }
    }

    @Override
    public byte[] generateRandomKey() {
        // Une clé AES-256 a une longueur de 32 octets (256 bits)
        byte[] key = new byte[32];
        new SecureRandom().nextBytes(key);
        return key;
    }
}
