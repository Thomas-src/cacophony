package org.helmo.server;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.UserSaveException;
import org.helmo.server.threads.WestThread;
import org.helmo.services.UserManagerService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Objects;

public class ActiveSession {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, WestThread> sessions = new ConcurrentHashMap<>();
    private final UserManagerService userManagerService;

    public ActiveSession(UserManagerService userManagerService) {
        this.userManagerService = userManagerService;
    }

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
                try {
                    userManagerService.saveMessageForUser(fullName, message);
                    LOGGER.info("Message crypté enregistré pour {}", fullName);
                } catch (UserSaveException e) {
                    throw new RuntimeException(e); // todo
                }
            } else {
                LOGGER.info("Message envoyé à {}", fullName);
                sessions.get(fullName).send(message);
            }
        }
    }
}