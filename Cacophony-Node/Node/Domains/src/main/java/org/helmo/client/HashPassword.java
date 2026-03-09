package org.helmo.client;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Cette classe encapsule la logique de vérification du challenge pour l'authentification.
 * Elle prend en entrée un hash bcrypt stocké (représentant le mot de passe de l'utilisateur) et fournit une méthode pour vérifier la réponse du client au challenge.
 */
public record HashPassword(String storedBcryptHash, int bcryptRound,  String bcryptSalt) {

    /**
     * Vérifie si la réponse du client au challenge est correcte en suivant les étapes suivantes :
     * 1. Concaténer le challenge (22 caractères) avec le hash bcrypt stocké.
     * 2. Calculer le hash SHA3-256 de cette concaténation.
     * 3. Convertir le résultat en une chaîne hexadécimale (hexdigest).
     * 4. Comparer cette chaîne hexadécimale avec la réponse du client de manière sécurisée (en utilisant une comparaison en temps constant pour éviter les attaques par timing).
     * 5. Retourner true si la comparaison est correcte, false sinon.
     *
     * @param clientResponse Le hexadécimal que le client a envoyé en réponse au challenge.
     * @param challenge      Le challenge de 22 caractères envoyé au client.
     * @return vrai si la réponse du client est correcte, faux sinon.
     */
    public boolean checkChallenge(String clientResponse, String challenge) {
        try {
            // 1. Concaténer le challenge avec le hash bcrypt stocké
            String secretCombination = challenge + this.storedBcryptHash;

            // 2. Calculer le hash SHA3-256 de la concaténation
            MessageDigest digest = MessageDigest.getInstance("SHA3-256");
            byte[] encodedHash = digest.digest(secretCombination.getBytes(StandardCharsets.UTF_8));

            // 3. Convertir le résultat en une chaîne hexadécimale
            String expectedResponse = bytesToHex(encodedHash);

            // 4. Comparer cette chaîne hexadécimale avec la réponse du client de manière sécurisée
            return MessageDigest.isEqual(
                    expectedResponse.toLowerCase().getBytes(),
                    clientResponse.toLowerCase().getBytes()
            );

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA3-256 algorithm not found", e);
        }
    }

    /**
     * Helper pour convertir un tableau de bytes en une chaîne hexadécimale.
     */
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

    /**
     * retourne la valeur du hash bcrypt stocké, utile pour les tests unitaires
     *
     * @return storedBcryptHash
     */
    @Override
    public String storedBcryptHash() {
        return storedBcryptHash;
    }
}