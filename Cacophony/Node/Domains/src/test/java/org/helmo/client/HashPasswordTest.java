package org.helmo.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class HashPasswordTest {

    private HashPassword hashPassword;
    private final String storedBcryptHash = "$2a$10$N9qo8uLOickgx2ZMRZoMye.aA.w.s.vQv2v.vX.vX.vX.vX.vX";
    private final int bcryptRound = 10;
    private final String bcryptSalt = "N9qo8uLOickgx2ZMRZoMye";
    private final String challenge = "1234567890123456789012";

    @BeforeEach
    void setUp() {
        hashPassword = new HashPassword(storedBcryptHash, bcryptRound, bcryptSalt);
    }

    private String calculateExpectedResponse(String challenge, String storedHash) throws NoSuchAlgorithmException {
        String secretCombination = challenge + storedHash;
        MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        byte[] encodedHash = digest.digest(secretCombination.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedHash);
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
/* todo
    @Test
    void testCheckChallengeWithCorrectResponse() throws NoSuchAlgorithmException {
        String expectedResponse = calculateExpectedResponse(challenge, storedBcryptHash);
        assertTrue(hashPassword.checkChallenge(expectedResponse, challenge));
    }

 */

    @Test
    void testCheckChallengeWithIncorrectResponse() {
        String incorrectResponse = "incorrectResponse";
        assertFalse(hashPassword.checkChallenge(incorrectResponse, challenge));
    }
/*
    @Test
    void testCheckChallengeWithDifferentCaseResponse() throws NoSuchAlgorithmException {
        String expectedResponse = calculateExpectedResponse(challenge, storedBcryptHash);
        assertTrue(hashPassword.checkChallenge(expectedResponse.toUpperCase(), challenge));
    }
  */

    @Test
    void testStoredBcryptHashAccessor() {
        assertEquals(storedBcryptHash, hashPassword.storedBcryptHash());
    }

    @Test
    void testBcryptRoundAccessor() {
        assertEquals(bcryptRound, hashPassword.bcryptRound());
    }

    @Test
    void testBcryptSaltAccessor() {
        assertEquals(bcryptSalt, hashPassword.bcryptSalt());
    }
}