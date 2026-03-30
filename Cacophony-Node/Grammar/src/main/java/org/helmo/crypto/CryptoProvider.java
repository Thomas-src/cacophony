package org.helmo.crypto;

import org.helmo.crypto.exceptions.DecryptionException;
import org.helmo.crypto.exceptions.EncryptionException;

/**
 * Interface fournissant des opérations cryptographiques pour AES-256 GCM.
 *
 * Cette interface définit les méthodes requises pour le chiffrement,
 * le déchiffrement et la génération de clés. Elle est destinée à être
 * implémentée par des classes fournissant des services cryptographiques concrets.
 */
public interface CryptoProvider {
/**
 * Chiffre une chaîne de texte en clair à l'aide de la clé symétrique spécifiée.
 *
 * Cette méthode utilise l'algorithme AES-256 GCM (Galois/Counter Mode) pour garantir
 * la confidentialité et l'intégrité des données.
 * Un vecteur d'initialisation (IV) aléatoire est généré et inclus dans le texte chiffré.
 *
 * @param plaintext la chaîne de texte en clair à chiffrer ; ne doit pas être {@code null} ou vide
 * @param key la clé de chiffrement symétrique, généralement une clé de 256 bits (32 octets) adaptée à AES-256 GCM ;
 *            ne doit pas être {@code null} ou invalide
 * @return une chaîne de texte contenant les données chiffrées et incluant le vecteur d'initialisation (IV) sous un format codé
 * @throws IllegalArgumentException si {@code plaintext} ou {@code key} est {@code null}, vide ou invalide
 * @throws EncryptionException si une erreur se produit lors du chiffrement des données
 */
String encrypt(String plaintext, String key) throws IllegalArgumentException, EncryptionException;



    /**
 * Déchiffre une chaîne de texte chiffrée à l'aide de la clé symétrique spécifiée.
 *
 * @param encryptedData les données chiffrées sous forme de chaîne, incluant
 *                      potentiellement un vecteur d'initialisation (IV) encodé
 *                      dans le format attendu ; ne doit pas être {@code null}
 *                      ou vide
 * @param key la clé de chiffrement symétrique utilisée pour déchiffrer les données,
 *            généralement une clé de 256 bits (32 octets) adaptée à AES-256 GCM ;
 *            ne doit pas être {@code null} ou invalide
 * @return les données déchiffrées sous forme de chaîne de texte en clair
 * @throws IllegalArgumentException si {@code encryptedData} ou {@code key} est {@code null},
 *                                  vide ou invalide
 * @throws DecryptionException si une erreur se produit lors du déchiffrement des données
 */
    String decypt(String encryptedData, String key) throws IllegalArgumentException, DecryptionException;

    byte[] generateRandomKey();

    // byte[] sign(byte[] message, byte[] privateKey);


    // boolean verify(byte[] message, byte[] signature, byte[] publicKey);
}
