package org.helmo.subscriber;

/**
 * Représente un abonné utilisateur dans le système.
 */
public interface IUserSubscriber {
    /**
     * Récupère le nom complet de l'abonné.
     *
     * @return Le nom complet sous forme de chaîne de caractères.
     */
    String getFullName();

    /**
     * Vérifie si l'abonné est stocké localement.
     *
     * @return vrai si l'abonné est local, faux sinon.
     */
    boolean isLocal();
}
