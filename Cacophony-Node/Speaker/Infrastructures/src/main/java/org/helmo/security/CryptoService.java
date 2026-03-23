package org.helmo.security;

/**
 * Service de chiffrement/déchiffrement AES-256 GCM pour les communications Speaker <-> Node.
 * À implémenter : méthodes pour chiffrer/déchiffrer, gestion des clés, etc.
 */
public class CryptoService implements CryptoProvider {
    @Override
    public byte[] encrypt(byte[] plaintext, byte[] key) {
        // TODO: Implémenter le chiffrement AES-256 GCM
        // TODO: 1. Générer un IV (Initialization Vector) aléatoire de 12 octets
        // TODO: 2. Initialiser le Cipher avec AES/GCM/NoPadding
        // TODO: 3. Effectuer le chiffrement
        // TODO: 4. Concaténer l'IV et le texte chiffré pour le retour
        return new byte[0];
    }

    @Override
    public byte[] decrypt(byte[] ciphertext, byte[] key) {
        // TODO: Implémenter le déchiffrement AES-256 GCM
        // TODO: 1. Extraire l'IV du texte chiffré
        // TODO: 2. Initialiser le Cipher avec AES/GCM/NoPadding
        // TODO: 3. Effectuer le déchiffrement
        // TODO: 4. Retourner le texte déchiffré
        return new byte[0];
    }

    @Override
    public byte[] generateRandomKey() {
        return new byte[0];
    }
}
