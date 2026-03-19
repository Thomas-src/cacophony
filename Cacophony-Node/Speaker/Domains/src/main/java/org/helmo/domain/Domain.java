package org.helmo.domain;

/**
 * Représente un domaine connu du Speaker (nom, clé AES, etc.).
 */
public class Domain {
    private final String name;
    private final byte[] aesKey;

    public Domain(String name, byte[] aesKey) {
        this.name = name;
        this.aesKey = aesKey;
    }

    public String getName() {
        return name;
    }

    public byte[] getAesKey() {
        return aesKey;
    }
}

