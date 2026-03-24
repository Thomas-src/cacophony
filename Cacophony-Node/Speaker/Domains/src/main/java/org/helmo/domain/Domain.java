package org.helmo.domain;

/**
 * Représente un domaine connu du Speaker (nom, clé AES, etc.).
 */
public class Domain {
    private final String name;
    private final byte[] aesKey;
    private boolean active;
    // TODO: Le nodeAddress est final, mais il devrait être mutable pour être mis à jour après la découverte via ADVERT.
    private final String nodeAddress;
    // TODO: Le publicKey est final, mais il pourrait devoir être mis à jour si les clés peuvent être renouvelées.
    private final byte[] publicKey;

    public Domain(String name, byte[] aesKey, String nodeAddress, byte[] publicKey) {
        // TODO: Valider les paramètres du constructeur (ex: nom non nul, clé non vide).
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

    // TODO: Ajouter une méthode pour mettre à jour l'adresse du nœud.
    // public void setNodeAddress(String nodeAddress) { ... }

    // TODO: Ajouter une méthode pour mettre à jour la clé publique.
    // public void setPublicKey(byte[] publicKey) { ... }
}
