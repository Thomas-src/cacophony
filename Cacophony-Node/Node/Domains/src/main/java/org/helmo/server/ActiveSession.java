package org.helmo.server;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.server.threads.EastThread;
import org.helmo.server.threads.WestThread;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Objects;

public class ActiveSession {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, WestThread> sessions = new ConcurrentHashMap<>();

    public boolean add(String fullName, WestThread wt) {
        Objects.requireNonNull(wt, "WestThread is null");
        if (sessions.containsKey(fullName)) {
            return false;
        }
        if (fullName != null && !fullName.isBlank()) {
            sessions.put(fullName, wt);
            // todo ajouter les messages (send)
        }
        return true;
    }

    public void remove(String fullName) {
        if (fullName != null && !fullName.isBlank()) {
            sessions.remove(fullName);
        }
    }

    public void send(String fullName, String message) {
        if (fullName != null && !fullName.isBlank()) {
            if (!sessions.containsKey(fullName)) {
                // todo implémenter le stockage etc
                LOGGER.info("Le client {} n'est pas connecté", fullName);
            } else {
                LOGGER.info("Message envoyé à {}", fullName);
                sessions.get(fullName).send(message);
            }
        }
    }
}