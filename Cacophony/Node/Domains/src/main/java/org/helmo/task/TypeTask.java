package org.helmo.task;

import org.helmo.Message;

import java.util.regex.Pattern;

/**
 * Représente les différents types de tâches traitées par le système.
 * Chaque type est associé à un motif (Pattern) de message spécifique.
 */
public enum TypeTask {
    /** Tâche d'accueil d'un nouveau membre. */
    ONBOARDING(Message.P_CMD_ONBOARDING),

    /** Tâche de départ d'un membre. */
    LEAVE(Message.P_CMD_LEAVE),

    /** Tâche liée aux informations utilisateur. */
    USER(Message.P_CMD_USER),

    /** Tâche d'envoi de message. */
    SEND(Message.P_CMD_SEND),

    /** Tâche de réponse à une requête. */
    RESPONSE(Message.P_CMD_RESPONSE),

    /** Tâche d'abonnement. */
    SUBSCRIBE(Message.P_CMD_SUBSCRIBE),

    /** Tâche de publication de contenu. */
    POST(Message.P_POST);

    private final Pattern pattern;

    /**
     * Constructeur interne pour associer un motif à un type de tâche.
     *
     * @param pattern Le motif regex correspondant à la commande.
     */
    TypeTask(Pattern pattern) {
        this.pattern = pattern;
    }

    /**
     * Récupère le motif regex associé à ce type de tâche.
     *
     * @return Le Pattern correspondant.
     */
    public Pattern getPattern() { return pattern; }
}
