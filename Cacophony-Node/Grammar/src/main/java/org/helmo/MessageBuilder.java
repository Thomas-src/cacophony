package org.helmo;


import static org.helmo.Grammar.*;


/* * Cette classe fournit des méthodes statiques pour construire les messages du protocole de communication.
 * Chaque méthode correspond à une commande spécifique et prend les paramètres nécessaires pour construire le message.
 * Les messages sont formatés selon les règles définies dans la grammaire, avec les espaces et les nouvelles lignes appropriés.
 */
public class MessageBuilder {
    private static final String SP = " ";
    private static final String NEWLINE = "\r\n";

    // welcome = "WELCOME" sp domain sp random_string newline

    /**
     * Cette méthode construit un message de bienvenue en utilisant le format spécifié dans la grammaire.
     * @param domain
     * @param randomString
     * @return
     */
    public static String buildWelcome(String domain, String randomString) {
        return "WELCOME " + domain + SP + randomString + NEWLINE;
    }

    // user = "USER" sp user newlineµ

    /**
     * Cette méthode contruit un message d'identification de l'utilisateur en utilisant le format spécifié dans la grammaire.
     * @param user
     * @return
     */
    public static String buildUser(String user) {
        return "USER" + SP + user + NEWLINE;
    }

    // challenge = "CHALLENGE" sp counter sp random_string newline

    /**
     * Cette méthode contruit un message de défi en utilisant le format spécifié dans la grammaire.
     * @param counter
     * @param randomString
     * @return
     */
    public static String buildChallenge(String counter, String randomString) {
        return "CHALLENGE" + SP + counter + SP + randomString + NEWLINE;
    }

    // response = "RESPONSE" sp sha3_hexstring newline

    /**
     * Cette méthode construit un message de réponse en utilisant le format spécifié dans la grammaire.
     * @param sha3Hexstring
     * @return
     */
    public static String buildResponse(String sha3Hexstring) {
        return "RESPONSE" + SP + sha3Hexstring + NEWLINE;
    }

    // onboarding = "ONBOARDING" sp user sp salt_length sp bcrypt newline

    /**
     * Cette méthode contruit un message d'onboarding en utilisant le format spécifié dans la grammaire.
     * @param user
     * @param saltLength
     * @param bcrypt
     * @return
     */
    public static String buildOnboarding(String user, String saltLength, String bcrypt) {
        return "ONBOARDING" + SP + user + SP + saltLength + SP + bcrypt + NEWLINE;
    }

    // subscribe = "SUBSCRIBE" sp (user_at_domain / tag_at_domain) newline

    /**
     *  Cette méthode contruit un message d'abonnement en utilisant le format spécifié dans la grammaire. Le paramètre 'target' peut être soit une adresse utilisateur (user_at_domain) soit une adresse de tag (tag_at_domain).
     * @param target
     * @return
     */
    public static String buildSubscribe(String target) {
        return "SUBSCRIBE" + SP + target + NEWLINE;
    }

    // send = "SEND" sp message newline

    /**
     * Cette méthode retourne un message de type "SEND" formaté selon les règles de la grammaire, en incluant le message fourni en paramètre.
     * @param message
     * @return
     */
    public static String buildSend(String message) {
        return "SEND" + SP + message + NEWLINE;
    }

    // msg = "MSG" sp user_at_domain sp message newline

    /**
     * Cette méthode retourne un message de type "MSG" formaté selon les règles de la grammaire, en incluant l'adresse de l'utilisateur (userAtDomain) et le message fourni en paramètre.
     * @param userAtDomain
     * @param message
     * @return
     */
    public static String buildMsg(String userAtDomain, String message) {
        return "MSG" + SP + userAtDomain + SP + message + NEWLINE;
    }

    // ok = "OK" newline (Version sans message)

    /**
     * Cette méthode retourne un message de type "OK" formaté selon les règles de la grammaire, sans message optionnel.
     * @return
     */
    public static String buildOk() {
        return "OK" + NEWLINE;
    }

    // ok = "OK" sp message newline (Version avec message optionnel)

    /**
     * Cette méthode retourne un message de type "OK" formaté selon les règles de la grammaire, en incluant le message optionnel fourni en paramètre. Si le message est null ou vide, elle retourne la version sans message.
     * @param message
     * @return
     */
    public static String buildOk(String message) {
        if (message == null || message.isEmpty()) return buildOk();
        return "OK" + SP + message + NEWLINE;
    }

    // error = "ERROR" newline (Version sans message)

    /**
     * Cette méthode retourne un message de type "ERROR" formaté selon les règles de la grammaire, sans message optionnel.
     * @return
     */
    public static String buildError() {
        return "ERROR" + NEWLINE;
    }

    // error = "ERROR" sp message newline (Version avec message optionnel)

    /**
     * Cette méthode retourne un message de type "ERROR" formaté selon les règles de la grammaire, en incluant le message optionnel fourni en paramètre. Si le message est null ou vide, elle retourne la version sans message.
     * @param message
     * @return
     */
    public static String buildError(String message) {
        if (message == null || message.isEmpty()) return buildError();
        return "ERROR" + SP + message + NEWLINE;
    }

    // leave = "LEAVE" newline

    /**
     * Cette méthode retourne un message de type "LEAVE" formaté selon les règles de la grammaire, indiquant que l'utilisateur souhaite quitter la session.
     * @return
     */
    public static String buildLeave() {
        return "LEAVE" + NEWLINE;
    }

//    // --- Exemple d'utilisation ---
//    public static void main(String[] args) {
//        System.out.print(buildWelcome("server.local", "aB3!eF8*xY9@qW2^mN5+vC"));
//        System.out.print(buildOk());
//        System.out.print(buildOk("User successfully registered"));
//        System.out.print(buildMsg("alice@server.local", "Hello Bob!"));
//    }
}