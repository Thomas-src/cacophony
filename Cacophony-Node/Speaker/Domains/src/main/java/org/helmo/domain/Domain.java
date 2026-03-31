package org.helmo.domain;

import java.util.Base64;

/**
 * Représente un domaine connu du Speaker (nom, clé AES, etc.).
 */
public class Domain {
    private final String name;
    private final String aesKey;
    private boolean active = false;

    public Domain(String name, String key) {
        this.name = name;
        this.aesKey = key; // todo à vérifier
    }

    public String getName() {
        return name;
    }

    public String getAesKey() {
        return aesKey;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive() {
        this.active = true;
    }


    @Override
    public String toString() {
        return name;
    }
}
