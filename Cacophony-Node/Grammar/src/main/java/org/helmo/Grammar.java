package org.helmo;


import java.util.regex.Pattern;

public class Grammar {

    // Caractères de base
    public static final String SP = "\\x20"; // %d32 (Espace)
    public static final String NEWLINE = "\\r\\n"; // %d13 %d10
    public static final String DIGIT = "\\d"; // %d48-57
    public static final String LETTER = "[A-Za-z]"; // %d65-90 / %d97-122
    public static final String DIGIT_LETTER = "[A-Za-z0-9]";

    // Plage allant de 32 (espace) à 255
    public static final String CHARACTER = "[\\x20-\\xFF]";

    // Caractères spéciaux ciblés (%d33-47 / %d58-64 / %d91-96)
    // Utilisation des codes hexa pour éviter les conflits d'échappement en Java
    public static final String SPECIALCHAR = "[\\x21-\\x2F\\x3A-\\x40\\x5B-\\x60]";

    // Règles de longueur et combinaisons simples
    public static final String PORT = "\\d{1,5}";
    public static final String COUNTER = "\\d{2}";
    public static final String SALT_LENGTH = "\\d{2}";

    public static final String MESSAGE = CHARACTER + "{1,200}";
    public static final String INNER_MESSAGE = CHARACTER + "{1,500}";

    public static final String USER = DIGIT_LETTER + "{5,20}";
    public static final String TAG = "#" + '(' + USER + ')'; // "#" suivi de 5 à 20 digit_letter

    // Domaine : digit_letter ou "."
    public static final String DOMAIN = "[A-Za-z0-9.]{5,200}";

    // Adresses
    public static final String USER_AT_DOMAIN = "(" + USER + ")@(" + DOMAIN + ")";
    public static final String TAG_AT_DOMAIN =   TAG + "@(" + DOMAIN + ")";
    public static final String ID_AT_DOMAIN = "\\d{1,5}@" + DOMAIN;

    // Règles complexes combinées
    // bcrypt: $2b$ + counter + $ + 1 à 70 caractères (digit_letter ou specialchar)
    // Note: Le $ doit être échappé en regex (\\$)
    public static final String DIGIT_LETTER_OR_SPECIAL = "[A-Za-z0-9\\x21-\\x2F\\x3A-\\x40\\x5B-\\x60]";
    public static final String BCRYPT = "\\$2b\\$" + COUNTER + "\\$" + DIGIT_LETTER_OR_SPECIAL + "{1,70}";

    public static final String SHA3_HEXSTRING = DIGIT_LETTER + "{30,200}";
    public static final String RANDOM_STRING = DIGIT_LETTER_OR_SPECIAL + "{22}";

     public static final String TAG_WITH_OPTIONAL_DOMAIN = "#(" + USER + ")(?:@(" + DOMAIN + "))?";
    // --- COMPILATION DES PATTERNS POUR UTILISATION ---

    public static final Pattern P_USER_AT_DOMAIN = Pattern.compile("^" + USER_AT_DOMAIN + "$");
    public static final Pattern P_BCRYPT = Pattern.compile("^" + BCRYPT + "$");
    public static final Pattern P_TAG_AT_DOMAIN = Pattern.compile("^" + TAG_AT_DOMAIN + "$");
    public static final Pattern P_MESSAGE = Pattern.compile("^" + MESSAGE + "$");
    public static final Pattern P_TAG_FULL = Pattern.compile(TAG_WITH_OPTIONAL_DOMAIN);
}