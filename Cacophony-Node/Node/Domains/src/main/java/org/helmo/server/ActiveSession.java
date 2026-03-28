package org.helmo.server;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.server.threads.WestThread;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Objects;

public class ActiveSession {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<String, WestThread> sessions = new ConcurrentHashMap<>();

    public static boolean add(String fullName, WestThread wt) {
        Objects.requireNonNull(wt, "WestThread is null");
        if(sessions.containsKey(fullName)) {
            return false; // todo que faire si le user se connecte avec +2 appareils ?
        }
        if (fullName != null && !fullName.isBlank()) {
            sessions.put(fullName, wt);
        }
        return true;
    }

    public static void remove(String fullName) {
        if (fullName != null && !fullName.isBlank()) {
            sessions.remove(fullName);
        }
    }

    public static void send(String fullName, String message) {
        if (fullName != null && !fullName.isBlank()) {
            if(!sessions.containsKey(fullName)) {
                LOGGER.info("Le client {} n'est pas connecté", fullName);
            } else {
                LOGGER.info("Message envoyé à {}", fullName);
                sessions.get(fullName).send(message);
            }
        }
    }
}