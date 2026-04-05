package org.helmo;


import static org.helmo.Grammar.*;
import java.util.regex.Pattern;


/**
 * Classe définissant les commandes du protocole et leurs patterns de validation.
 * <p>
 * Cette classe contient les définitions de toutes les commandes utilisées dans le protocole
 * de communication, ainsi que leurs patterns compilés pour la validation des messages.
 * Chaque commande est définie selon la grammaire du protocole et peut être validée
 * à l'aide des patterns compilés correspondants.
 */
public class Message {

    // --- COMMANDES DU PROTOCOLE ---

    /**
     * Commande WELCOME du protocole.
     * Format : "WELCOME" sp domain sp random_string newline
     * Utilisée pour accueillir un client avec le domaine du serveur et une chaîne aléatoire.
     */
    public static final String CMD_WELCOME = "WELCOME" + SP + DOMAIN + SP + RANDOM_STRING;

    /**
     * Commande USER du protocole.
     * Format : "USER" sp user newline
     * Note : le deuxième 'user' fait référence à la constante USER (5 à 20 digit_letter).
     * Utilisée pour identifier l'utilisateur lors de la connexion.
     */
    public static final String CMD_USER = "USER" + SP + "("+USER+")";

    /**
     * Commande CHALLENGE du protocole.
     * Format : "CHALLENGE" sp counter sp random_string newline
     * Utilisée pour envoyer un défi d'authentification avec un compteur et une chaîne aléatoire.
     */
    public static final String CMD_CHALLENGE = "CHALLENGE" + SP + COUNTER + SP + RANDOM_STRING;

    /**
     * Commande RESPONSE du protocole.
     * Format : "RESPONSE" sp sha3_hexstring newline
     * Utilisée pour répondre au défi d'authentification avec un hash SHA3.
     */
    public static final String CMD_RESPONSE = "RESPONSE" + SP +"(" +SHA3_HEXSTRING+")";

    /**
     * Commande ONBOARDING du protocole.
     * Format : "ONBOARDING" sp user sp salt_length sp bcrypt newline
     * Utilisée pour enregistrer un nouvel utilisateur avec ses informations d'authentification.
     */
    public static final String CMD_ONBOARDING = "ONBOARDING" + SP + "(" + USER+ ")" + SP + "("+ SALT_LENGTH +")" + SP + "(" + BCRYPT+ ")";

    /**
     * Commande SUBSCRIBE du protocole.
     * Format : "SUBSCRIBE" sp (user_at_domain / tag_at_domain) newline
     * Utilisation d'un groupe non capturant (?: ... | ... ) pour le OU logique.
     * Utilisée pour s'abonner à un utilisateur ou un tag.
     */
    public static final String CMD_SUBSCRIBE = "SUBSCRIBE" + SP + "(?:" + USER_AT_DOMAIN + "|" + TAG_AT_DOMAIN + ")";

    /**
     * Commande SEND du protocole.
     * Format : "SEND" sp message newline
     * Utilisée pour envoyer un message.
     */
    public static final String CMD_SEND = "SEND" + SP + "(" + MESSAGE + ")";
    /**
     * Commande MSG du protocole.
     * Format : "MSG" sp (user_at_domain / tag_at_domain) sp message newline
     * Utilisée pour envoyer un message à un utilisateur ou un tag spécifique.
     */
    public static final String CMD_MSG = "MSG" + SP + "(" + USER_AT_DOMAIN + "|" + TAG_AT_DOMAIN + ")" + SP + "("+MESSAGE+")";

    /**
     * Commande OK du protocole.
     * Format : "OK" [sp message] newline
     * Utilisation de (?: ... )? pour rendre le bloc [sp message] optionnel.
     * Utilisée pour confirmer le succès d'une opération avec un message optionnel.
     */
    public static final String CMD_OK = "OK" + "(?:" + SP + MESSAGE + ")?";

    /**
     * Commande ERROR du protocole.
     * Format : "ERROR" [sp message] newline
     * Utilisée pour signaler une erreur avec un message optionnel.
     */
    public static final String CMD_ERROR = "ERROR" + "(?:" + SP + MESSAGE + ")?";

    /**
     * Commande LEAVE du protocole.
     * Format : "LEAVE" newline
     * Utilisée pour quitter la session.
     */
    public static final String CMD_LEAVE = "LEAVE";

    /**
     * Commande ADVERT du protocole.
     * Format : "ADVERT" sp port sp domain newline
     * Utilisée pour annoncer un serveur avec son port et son domaine.
     */
    public static final String ADVERT = "ADVERT" + SP + "("+ PORT + ")" + SP + "("+ DOMAIN+ ")" + NEWLINE;

    /**
     * Commande POST du protocole.
     * Format : "POST" sp id_at_domain sp user_at_domain sp (user_at_domain / tag_at_domain) sp inner_message newline
     * Utilisée pour publier un message avec un identifiant, un auteur, un destinataire et le contenu.
     */
    public  static final String CMD_POST = "POST" + SP + "(" + ID_AT_DOMAIN  + ")" + SP + "(" + USER_AT_DOMAIN  + ")"+ SP +"(?:" + USER_AT_DOMAIN + "|" + TAG_AT_DOMAIN + ")" + SP + "("+ INNER_MESSAGE+")" + NEWLINE;
    // --- COMPILATION DES PATTERNS DES COMMANDES ---

    /**
     * Pattern compilé pour la validation de la commande WELCOME.
     * On ajoute "^" et "$" pour s'assurer que la chaîne correspond exactement à la commande, du début à la fin.
     */
    public static final Pattern P_CMD_WELCOME = Pattern.compile("^" + CMD_WELCOME + "$");
    /**
     * Pattern compilé pour la validation de la commande USER.
     */
    public static final Pattern P_CMD_USER = Pattern.compile("^" + CMD_USER + "$");
    /**
     * Pattern compilé pour la validation de la commande CHALLENGE.
     */
    public static final Pattern P_CMD_CHALLENGE = Pattern.compile("^" + CMD_CHALLENGE + "$");
    /**
     * Pattern compilé pour la validation de la commande RESPONSE.
     */
    public static final Pattern P_CMD_RESPONSE = Pattern.compile("^" + CMD_RESPONSE + "$");
    /**
     * Pattern compilé pour la validation de la commande ONBOARDING.
     */
    public static final Pattern P_CMD_ONBOARDING = Pattern.compile("^" + CMD_ONBOARDING + "$");
    /**
     * Pattern compilé pour la validation de la commande SUBSCRIBE.
     */
    public static final Pattern P_CMD_SUBSCRIBE = Pattern.compile("^" + CMD_SUBSCRIBE + "$");
    /**
     * Pattern compilé pour la validation de la commande SEND.
     */
    public static final Pattern P_CMD_SEND = Pattern.compile("^" + CMD_SEND + "$");
    /**
     * Pattern compilé pour la validation de la commande MSG.
     */
    public static final Pattern P_CMD_MSG = Pattern.compile("^" + CMD_MSG + "$");
    /**
     * Pattern compilé pour la validation de la commande OK.
     */
    public static final Pattern P_CMD_OK = Pattern.compile("^" + CMD_OK + "$");
    /**
     * Pattern compilé pour la validation de la commande ERROR.
     */
    public static final Pattern P_CMD_ERROR = Pattern.compile("^" + CMD_ERROR + "$");
    /**
     * Pattern compilé pour la validation de la commande LEAVE.
     */
    public static final Pattern P_CMD_LEAVE = Pattern.compile("^" + CMD_LEAVE + "$");
    /**
     * Pattern compilé pour la validation de la commande ADVERT.
     */
    public static final Pattern P_ADVERT = Pattern.compile("^" + ADVERT + "$");
    /**
     * Pattern compilé pour la validation de la commande POST.
     */
    public static final Pattern P_POST = Pattern.compile("^" + CMD_POST + "$");
}
