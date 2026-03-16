package org.helmo;


import static org.helmo.Grammar.*;
import java.util.regex.Pattern;


public class Message {

    // --- COMMANDES DU PROTOCOLE ---

    // welcome = "WELCOME" sp domain sp random_string newline
    public static final String CMD_WELCOME = "WELCOME" + SP + DOMAIN + SP + RANDOM_STRING;

    // user = "USER" sp user newline
    // Note : le deuxième 'user' fait référence à la constante USER (5 à 20 digit_letter)
    public static final String CMD_USER = "USER" + SP + USER;

    // challenge = "CHALLENGE" sp counter sp random_string newline
    public static final String CMD_CHALLENGE = "CHALLENGE" + SP + COUNTER + SP + RANDOM_STRING;

    // response = "RESPONSE" sp sha3_hexstring newline
    public static final String CMD_RESPONSE = "RESPONSE" + SP + SHA3_HEXSTRING;

    // onboarding = "ONBOARDING" sp user sp salt_length sp bcrypt newline
    public static final String CMD_ONBOARDING = "ONBOARDING" + SP + USER + SP + SALT_LENGTH + SP + BCRYPT;

    // subscribe = "SUBSCRIBE" sp (user_at_domain / tag_at_domain) newline
    // Utilisation d'un groupe non capturant (?: ... | ... ) pour le OU logique
    public static final String CMD_SUBSCRIBE = "SUBSCRIBE" + SP + "(?:" + USER_AT_DOMAIN + "|" + TAG_AT_DOMAIN + ")";

    // send = "SEND" sp message newline
    public static final String CMD_SEND = "SEND" + SP + MESSAGE;

    // msg = "MSG" sp (user_at_domain / tag_at_domain) sp message newline
    public static final String CMD_MSG = "MSG" + SP + "(?:" + USER_AT_DOMAIN + "|" + TAG_AT_DOMAIN + ")" + SP + MESSAGE;

    // ok = "OK" [sp message] newline
    // Utilisation de (?: ... )? pour rendre le bloc [sp message] optionnel
    public static final String CMD_OK = "OK" + "(?:" + SP + MESSAGE + ")?";

    // error = "ERROR" [sp message] newline
    public static final String CMD_ERROR = "ERROR" + "(?:" + SP + MESSAGE + ")?";

    // leave = "LEAVE" newline
    public static final String CMD_LEAVE = "LEAVE";


    // --- COMPILATION DES PATTERNS DES COMMANDES ---

    // On ajoute "^" et "$" pour s'assurer que la chaîne correspond exactement à la commande, du début à la fin.
    public static final Pattern P_CMD_WELCOME = Pattern.compile("^" + CMD_WELCOME + "$");
    public static final Pattern P_CMD_USER = Pattern.compile("^" + CMD_USER + "$");
    public static final Pattern P_CMD_CHALLENGE = Pattern.compile("^" + CMD_CHALLENGE + "$");
    public static final Pattern P_CMD_RESPONSE = Pattern.compile("^" + CMD_RESPONSE + "$");
    public static final Pattern P_CMD_ONBOARDING = Pattern.compile("^" + CMD_ONBOARDING + "$");
    public static final Pattern P_CMD_SUBSCRIBE = Pattern.compile("^" + CMD_SUBSCRIBE + "$");
    public static final Pattern P_CMD_SEND = Pattern.compile("^" + CMD_SEND + "$");
    public static final Pattern P_CMD_MSG = Pattern.compile("^" + CMD_MSG + "$");
    public static final Pattern P_CMD_OK = Pattern.compile("^" + CMD_OK + "$");
    public static final Pattern P_CMD_ERROR = Pattern.compile("^" + CMD_ERROR + "$");
    public static final Pattern P_CMD_LEAVE = Pattern.compile("^" + CMD_LEAVE + "$");
}
