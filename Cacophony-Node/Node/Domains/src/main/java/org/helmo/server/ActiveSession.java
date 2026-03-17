package org.helmo.server;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.server.threads.WestThread;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Objects;

public class ActiveSession {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final Map<String, WestThread> sessions = new ConcurrentHashMap<String, WestThread>();

    public static void add(String name, WestThread wt) {
        Objects.requireNonNull(wt, "WestThread is null");
        if (name != null && !name.isBlank()) {
            sessions.put(name, wt); // todo que faire si le user se connecte avec +2 appareils ?
        }
    }

    public static void remove(String name) {
        if (name != null && !name.isBlank()) {
            sessions.remove(name);
        }
    }

    public static void send(String name, String message) {
        if (name != null && !name.isBlank()) {
            if(!sessions.containsKey(name)) {
                LOGGER.info("Le client {} n'est pas connecté", name);
            }
             sessions.get(name).send(message);
        }
    }
}