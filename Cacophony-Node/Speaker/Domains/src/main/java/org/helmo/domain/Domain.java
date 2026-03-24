package org.helmo.domain;

import java.util.Base64;

/**
 * Représente un domaine connu du Speaker (nom, clé AES, etc.).
 */
public class Domain {
    private final String name;
    private final byte[] aesKey;
    private boolean active;

    public Domain(String name, String key) {
        this.name = name;
        this.aesKey = Base64.getDecoder().decode(key); // todo à vérifier
    }

    public String getName() {
        return name;
    }

    public byte[] getAesKey() {
        return aesKey;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }


    @Override
    public String toString() {
        return name;
    }
}
