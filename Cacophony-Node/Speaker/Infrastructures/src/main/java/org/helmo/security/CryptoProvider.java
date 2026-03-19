package org.helmo.security;

/**
 * Interface pour le chiffrement/déchiffrement AES-256 GCM.
 */
public interface CryptoProvider {
    byte[] encrypt(byte[] plaintext, byte[] key);
    byte[] decrypt(byte[] ciphertext, byte[] key);
}

