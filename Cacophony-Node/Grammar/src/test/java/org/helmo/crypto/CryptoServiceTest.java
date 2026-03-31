package org.helmo.crypto;

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
        System.out.println("base64Key: " + base64Key);
    }

    @Test
    void testEncryptAndDecryptCycle() throws EncryptionException, DecryptionException {
        String encrypted = cryptoService.encrypt(message, base64Key);
        assertNotNull(encrypted, "Le message chiffré ne doit pas être nul.");
        assertNotEquals(message, encrypted, "Le message chiffré doit être différent du texte original.");

        String decrypted = cryptoService.decrypt(encrypted, base64Key);
        assertEquals(message, decrypted, "Le message déchiffré doit correspondre au texte original.");
    }

    @Test
    void testEncryptRandomness() throws EncryptionException {
        String encrypted1 = cryptoService.encrypt(message, base64Key);
        System.out.println(encrypted1);
        String encrypted2 = cryptoService.encrypt(message, base64Key);
        System.out.println(encrypted2);
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

    @Test
    void testEncryptKey() throws EncryptionException, DecryptionException {
        String mybase64 = "8N+2qBU5K0IJ/65xOldmicvA99zZwQdEk4kr0ifvZVk=";
        String encrypted = cryptoService.encrypt(message, mybase64);
        assertNotNull(encrypted, "Le message chiffré ne doit pas être nul.");
        assertNotEquals(message, encrypted, "Le message chiffré doit être différent du texte original.");

        String decrypted = cryptoService.decrypt(encrypted, mybase64);
        System.out.println(decrypted);
        assertEquals(message, decrypted, "Le message déchiffré doit correspondre au texte original.");
    }

}