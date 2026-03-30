package org.helmo.crypto;

import org.helmo.crypto.CryptoService;
import org.helmo.crypto.exceptions.DecryptionException;
import org.helmo.crypto.exceptions.EncryptionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe générée par ia
 */
class CryptoServiceTest {

    private CryptoService cryptoService;
    private String base64Key;
    private String message;

    // Génération clé AES-256 aléatoire en Base64
    private String generateRandomBase64Key() {
        byte[] key = new byte[32];
        new Random().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    @BeforeEach
    void setUp() {
        cryptoService = new CryptoService();
        base64Key = generateRandomBase64Key();
        message = "This is a secret message for String/Base64 test!";
    }

    @Test
    void testEncryptAndDecryptCycle() throws EncryptionException {
        String encrypted = cryptoService.encrypt(message, base64Key);
        assertNotNull(encrypted, "Le message chiffré ne doit pas être nul.");
        assertNotEquals(message, encrypted, "Le message chiffré doit être différent du texte original.");

        String decrypted = cryptoService.decrypt(encrypted, base64Key);
        assertEquals(message, decrypted, "Le message déchiffré doit correspondre au texte original.");
    }

    @Test
    void testEncryptRandomness() throws EncryptionException {
        String encrypted1 = cryptoService.encrypt(message, base64Key);
        String encrypted2 = cryptoService.encrypt(message, base64Key);
        assertNotEquals(encrypted1, encrypted2, "Deux chiffrements du même message doivent être différents grâce à l'IV.");
    }

    @Test
    void testDecryptWithWrongKey() throws EncryptionException {
        String encrypted = cryptoService.encrypt(message, base64Key);
        String wrongKey = generateRandomBase64Key();

        assertThrows(DecryptionException.class, () -> {
            cryptoService.decrypt(encrypted, wrongKey);
        }, "Déchiffrement avec une mauvaise clé doit lancer une DecryptionException.");
    }

    @Test
    void testEncryptWithInvalidKey() {
        String invalidKey = "abcd"; // clé trop courte
        assertThrows(EncryptionException.class, () -> {
            cryptoService.encrypt(message, invalidKey);
        }, "Chiffrer avec une clé invalide doit lancer une EncryptionException.");
    }
}