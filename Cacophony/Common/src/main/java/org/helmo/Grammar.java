package org.helmo;


import java.util.regex.Pattern;

/**
 * Classe définissant les règles de grammaire et les expressions régulières utilisées dans l'application.
 * <p>
 * Cette classe fournit un ensemble de constantes représentant les patterns regex pour valider
 * différents formats de données tels que les noms d'utilisateur, les domaines, les messages,
 * et autres éléments de protocole. Elle inclut également des patterns précompilés pour
 * une utilisation optimisée lors de la validation.
 */
public class Grammar {

    // Caractères de base

    /**
     * Expression régulière représentant un espace (%d32).
     */
    public static final String SP = "\\x20"; // %d32 (Espace)

    /**
     * Expression régulière représentant une nouvelle ligne CRLF (%d13 %d10).
     */
    public static final String NEWLINE = "\\r\\n"; // %d13 %d10

    /**
     * Expression régulière représentant un chiffre (%d48-57).
     */
    public static final String DIGIT = "\\d"; // %d48-57

    /**
     * Expression régulière représentant une lettre majuscule ou minuscule (%d65-90 / %d97-122).
     */
    public static final String LETTER = "[A-Za-z]"; // %d65-90 / %d97-122

    /**
     * Expression régulière représentant un chiffre ou une lettre.
     */
    public static final String DIGIT_LETTER = "[A-Za-z0-9]";

    // Plage allant de 32 (espace) à 255

    /**
     * Expression régulière représentant tout caractère dans la plage 32 (espace) à 255.
     */
    public static final String CHARACTER = "[\\x20-\\xFF]";

    // Caractères spéciaux ciblés (%d33-47 / %d58-64 / %d91-96)
    // Utilisation des codes hexa pour éviter les conflits d'échappement en Java

    /**
     * Expression régulière représentant les caractères spéciaux ciblés (%d33-47 / %d58-64 / %d91-96).
     */
    public static final String SPECIALCHAR = "[\\x21-\\x2F\\x3A-\\x40\\x5B-\\x60]";

    // Règles de longueur et combinaisons simples

    /**
     * Expression régulière représentant un numéro de port (1 à 5 chiffres).
     */
    public static final String PORT = "\\d{1,5}";

    /**
     * Expression régulière représentant un compteur de 2 chiffres.
     */
    public static final String COUNTER = "\\d{2}";

    /**
     * Expression régulière représentant la longueur d'un salt (2 chiffres).
     */
    public static final String SALT_LENGTH = "\\d{2}";

    /**
     * Expression régulière représentant un message de 1 à 200 caractères.
     */
    public static final String MESSAGE = CHARACTER + "{1,200}";

    /**
     * Expression régulière représentant un message interne de 1 à 500 caractères.
     */
    public static final String INNER_MESSAGE = CHARACTER + "{1,500}";

    /**
     * Expression régulière représentant un nom d'utilisateur (5 à 20 caractères alphanumériques).
     */
    public static final String USER = DIGIT_LETTER + "{5,20}";

    /**
     * Expression régulière représentant un tag précédé de # (5 à 20 caractères alphanumériques).
     */
    public static final String TAG = "#" + USER; // "#" suivi de 5 à 20 digit_letter

    // Domaine : digit_letter ou "."

    /**
     * Expression régulière représentant un nom de domaine (5 à 200 caractères alphanumériques ou points).
     */
    public static final String DOMAIN = "[A-Za-z0-9.]{5,200}";

    // Adresses

    /**
     * Expression régulière représentant une adresse utilisateur au format user@domain.
     */
    public static final String USER_AT_DOMAIN = "(" + USER + ")@(" + DOMAIN + ")";

    /**
     * Expression régulière représentant une adresse tag au format #tag@domain.
     */
    public static final String TAG_AT_DOMAIN = "(" + TAG + ")@(" + DOMAIN + ")";

    /**
     * Expression régulière représentant un identifiant numérique au format id@domain.
     */
    public static final String ID_AT_DOMAIN = "\\d{1,5}@" + DOMAIN;

    // Règles complexes combinées
    // bcrypt: $2b$ + counter + $ + 1 à 70 caractères (digit_letter ou specialchar)
    // Note: Le $ doit être échappé en regex (\\$)

    /**
     * Expression régulière représentant un caractère alphanumérique ou spécial.
     */
    public static final String DIGIT_LETTER_OR_SPECIAL = "[A-Za-z0-9\\x21-\\x2F\\x3A-\\x40\\x5B-\\x60]";

    /**
     * Expression régulière représentant un hash bcrypt au format $2b$counter$hash.
     */
    public static final String BCRYPT = "\\$2b\\$(" + COUNTER + ")\\$(" + DIGIT_LETTER_OR_SPECIAL + "{1,70})";;

    /**
     * Expression régulière représentant une chaîne hexadécimale SHA3 (30 à 200 caractères).
     */
    public static final String SHA3_HEXSTRING = DIGIT_LETTER + "{30,200}";

    /**
     * Expression régulière représentant une chaîne aléatoire de 22 caractères.
     */
    public static final String RANDOM_STRING = DIGIT_LETTER_OR_SPECIAL + "{22}";

    /**
     * Expression régulière représentant un tag avec domaine optionnel.
     */
    public static final String TAG_WITH_OPTIONAL_DOMAIN = "(" + TAG + ")(?:@(" + DOMAIN + "))?";

    // --- COMPILATION DES PATTERNS POUR UTILISATION ---

    /**
     * Pattern compilé pour valider une adresse user@domain.
     */
    public static final Pattern P_USER_AT_DOMAIN = Pattern.compile("^" + USER_AT_DOMAIN + "$");

    /**
     * Pattern compilé pour valider un hash bcrypt.
     */
    public static final Pattern P_BCRYPT = Pattern.compile("^" + BCRYPT + "$");

    /**
     * Pattern compilé pour valider une adresse tag@domain.
     */
    public static final Pattern P_TAG_AT_DOMAIN = Pattern.compile("^" + TAG_AT_DOMAIN + "$");

    /**
     * Pattern compilé pour valider un message.
     */
    public static final Pattern P_MESSAGE = Pattern.compile("^" + MESSAGE + "$");

    /**
     * Pattern compilé pour valider un tag avec domaine optionnel.
     */
    public static final Pattern P_TAG_FULL = Pattern.compile(TAG_WITH_OPTIONAL_DOMAIN);
}