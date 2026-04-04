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
    public Domain(String name, String key, List<String> messages) {
        this.name = name;
        this.aesKey = key;
        if(messages != null) {
            this.messages.addAll(messages);
        }
    }

    public String getName() {
        return name;
    }

    public String getAesKey() {
        return aesKey;
    }

    public boolean hasMessages() {
        return !messages.isEmpty();
    }

    public List<String> getMessages() {
        return messages;
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
