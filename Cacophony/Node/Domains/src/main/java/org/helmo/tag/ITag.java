package org.helmo.tag;

/**
 * Représente un tag (étiquette) au sein du système.
 */
public interface ITag {
    /**
     * Récupère le nom complet du tag.
     *
     * @return Le nom complet sous forme de chaîne de caractères.
     */
    String getFullName();

    /**
     * Vérifie si le tag est interne au système.
     *
     * @return true si le tag est interne, false sinon.
     */
    boolean isInternal();
}
