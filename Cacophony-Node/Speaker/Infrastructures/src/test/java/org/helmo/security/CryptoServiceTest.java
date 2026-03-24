package org.helmo.security;

import org.helmo.domain.CryptoProvider;
import org.helmo.security.exceptions.DecryptionException;
import org.helmo.security.exceptions.EncryptionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class CryptoServiceTest {

    private CryptoProvider cryptoService;
    private byte[] key;
    private String message;
    private byte[] messageBytes;

    @BeforeEach
    void setUp() {
        cryptoService = new CryptoService();
        key = cryptoService.generateRandomKey();
        message = "This is a secret message for testing.";
        messageBytes = message.getBytes(StandardCharsets.UTF_8);
    }

    @Test
    void testEncryptAndDecryptCycle() {
        // Chiffrement
        byte[] encryptedData = cryptoService.encrypt(messageBytes, key);
        assertNotNull(encryptedData, "Le message chiffré ne doit pas être nul.");
        assertNotEquals(0, encryptedData.length, "Le message chiffré ne doit pas être vide.");
        assertFalse(Arrays.equals(messageBytes, encryptedData), "Le message chiffré doit être différent de l'original.");

        // Déchiffrement
        byte[] decryptedData = cryptoService.decrypt(encryptedData, key);
        assertNotNull(decryptedData, "Le message déchiffré ne doit pas être nul.");
        assertEquals(message, new String(decryptedData, StandardCharsets.UTF_8), "Le message déchiffré doit correspondre à l'original.");
    }

    @Test
    void testEncryptionIsRandom() {
        // Chiffrer le même message deux fois doit produire des résultats différents
        // en raison de l'IV aléatoire.
        byte[] encryptedData1 = cryptoService.encrypt(messageBytes, key);
        byte[] encryptedData2 = cryptoService.encrypt(messageBytes, key);

        assertFalse(Arrays.equals(encryptedData1, encryptedData2), "Deux chiffrements du même message avec la même clé doivent être différents.");
    }

    @Test
    void testDecryptWithWrongKey() {
        byte[] encryptedData = cryptoService.encrypt(messageBytes, key);
        byte[] wrongKey = cryptoService.generateRandomKey();

        // S'assurer que les clés sont bien différentes
        assertFalse(Arrays.equals(key, wrongKey));

        // Le déchiffrement avec une mauvaise clé doit échouer
        assertThrows(DecryptionException.class, () -> {
            cryptoService.decrypt(encryptedData, wrongKey);
        }, "Déchiffrer avec une mauvaise clé doit lancer une DecryptionException.");
    }

    @Test
    void testEncryptWithInvalidKey() {
        byte[] invalidKey = new byte[]{1, 2, 3}; // Clé non valide (longueur incorrecte)
        assertThrows(EncryptionException.class, () -> {
            cryptoService.encrypt(messageBytes, invalidKey);
        }, "Chiffrer avec une clé non valide doit lancer une EncryptionException.");
    }
}
