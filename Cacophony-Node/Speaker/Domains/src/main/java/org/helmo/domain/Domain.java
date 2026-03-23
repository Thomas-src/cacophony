package org.helmo.domain;

/**
 * Représente un domaine connu du Speaker (nom, clé AES, etc.).
 */
public class Domain {
    private final String name;
    private final byte[] aesKey;
    private boolean active;
    private final String nodeAddress;
    private final byte[] publicKey;

    public Domain(String name, byte[] aesKey, String nodeAddress, byte[] publicKey) {
        this.name = name;
        this.aesKey = aesKey;
        this.nodeAddress = nodeAddress;
        this.publicKey = publicKey;
        this.active = true; // Par défaut, un domaine est actif
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

    public String getNodeAddress() {
        return nodeAddress;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }
}
