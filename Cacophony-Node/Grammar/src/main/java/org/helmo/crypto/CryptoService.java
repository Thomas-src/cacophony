package org.helmo.crypto;

import org.helmo.crypto.exceptions.DecryptionException;
import org.helmo.crypto.exceptions.EncryptionException;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Service qui implémente l'interface CryptoProvider pour fournir des fonctionnalités
 * de chiffrement et déchiffrement utilisant AES-256 GCM (Galois/Counter Mode).
 * Cette classe permet également de générer des clés aléatoires adaptées à l'algorithme utilisé.
 * Le chiffrement inclut un vecteur d'initialisation (IV) aléatoire pour garantir l'unicité
 * des sorties même avec des données en entrée identiques.
 */
public class CryptoService implements CryptoProvider {
    private static final String SHA_CRYPT = "SHA-256";
    private static final String AES_ALGORITHM = "AES";
    private static final String AES_ALGORITHM_GCM = "AES/GCM/NoPadding";

    private static final Integer IV_LENGTH_ENCRYPT = 12;
    private static final Integer TAG_LENGTH_ENCRYPT = 16;

    /**
     * Code de <a href="https://medium.com/@johnvazna/implementing-local-aes-gcm-encryption-and-decryption-in-java-ac1dacaaa409">johnvazna (Medium.com)</a>
     *
     * @param plainText les données en clair à chiffrer ; ne doit pas être {@code null}
     * @return
     * @throws EncryptionException
     */
    @Override
    public String encrypt(String plainText, String base64Key) throws EncryptionException {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(base64Key);
            if (decodedKey.length != 32) {
                throw new IllegalArgumentException("Clé AES invalide (doit faire 256 bits)");
            }
            SecretKeySpec key = new SecretKeySpec(decodedKey, "AES");

            byte[] iv = new byte[IV_LENGTH_ENCRYPT];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM_GCM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_ENCRYPT * 8, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            byte[] combinedIvAndCipherText = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, combinedIvAndCipherText, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, combinedIvAndCipherText, iv.length, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(combinedIvAndCipherText);
        } catch (GeneralSecurityException e) {
            throw new EncryptionException("Encryption Exception", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] encryptedData, byte[] key) {
        return new byte[0];
    }
}
