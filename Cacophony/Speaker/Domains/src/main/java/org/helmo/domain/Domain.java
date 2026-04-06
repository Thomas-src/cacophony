package org.helmo.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Représente un domaine connu du Speaker (nom, clé AES, etc.).
 */
public class Domain {
    private final String name;
    private final String aesKey;
    private boolean active = false;

    private final List<String> messages = new ArrayList<>();

    /**
     * Construit un nouveau domaine.
     * @param name Le nom du domaine.
     * @param key La clé AES associée.
     * @param messages La liste initiale des messages.
     */
    public Domain(String name, String key, List<String> messages) {
        this.name = name;
        this.aesKey = key;
        if(messages != null) {
            this.messages.addAll(messages);
        }
    }

    /**
     * @return Le nom du domaine.
     */
    public String getName() {
        return name;
    }

    /**
     * @return La clé AES du domaine.
     */
    public String getAesKey() {
        return aesKey;
    }

    /**
     * Vérifie si le domaine contient des messages.
     * @return true s'il y a des messages, sinon false.
     */
    public boolean hasMessages() {
        return !messages.isEmpty();
    }

    /**
     * @return La liste des messages du domaine.
     */
    public List<String> getMessages() {
        return messages;
    }

    /**
     * Vérifie si le domaine est actif.
     * @return true si actif, sinon false.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Définit le domaine comme étant actif.
     */
    public void setActive() {
        this.active = true;
    }


    @Override
    public String toString() {
        return name;
    }
}
