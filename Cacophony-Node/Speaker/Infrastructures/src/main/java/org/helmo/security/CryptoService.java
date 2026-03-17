package org.helmo.security;

/**
 * Service de chiffrement/déchiffrement AES-256 GCM pour les communications Speaker <-> Node.
 * À implémenter : méthodes pour chiffrer/déchiffrer, gestion des clés, etc.
 */
public class CryptoService implements CryptoProvider {
    @Override
    public byte[] encrypt(byte[] plaintext, byte[] key) {
        // TODO: Implémenter le chiffrement AES-256 GCM
        return new byte[0];
    }

    @Override
    public byte[] decrypt(byte[] ciphertext, byte[] key) {
        // TODO: Implémenter le déchiffrement AES-256 GCM
        return new byte[0];
    }
}
