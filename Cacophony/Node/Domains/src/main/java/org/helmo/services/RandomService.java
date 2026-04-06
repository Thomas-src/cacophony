package org.helmo.services;

import java.security.SecureRandom;


/**
 * Service fournissant des fonctionnalités de génération de données aléatoires sécurisées.
 */
public class RandomService {
    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789" +
                    "!\"#$%&'()*+,-./" +  // %d33-47
                    ":;<=>?@" +           // %d58-64
                    "[]^_`";              // %d91-96
    /**
     * Génère une chaîne de caractères aléatoire de manière sécurisée.
     *
     * @param size La taille de la chaîne à générer.
     * @return Une chaîne de caractères composée de lettres et de chiffres.
     */
    public static String generateRandomString(int size) {
        SecureRandom random = new SecureRandom();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < size; i++) {
            int index = random.nextInt(CHARACTERS.length());
            result.append(CHARACTERS.charAt(index));
        }

        return result.toString();
    }
}
