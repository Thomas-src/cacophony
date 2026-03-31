package org.helmo.server;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.server.threads.EastThread;
import org.helmo.server.threads.WestThread;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Objects;

/**
 * Gère les sessions actives des clients connectés.
 */
public class ActiveSession {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, WestThread> sessions = new ConcurrentHashMap<>();
    private EastThread eastThread;

    /**
     * Ajoute une nouvelle session utilisateur.
     *
     * @param fullName Le nom complet de l'utilisateur.
     * @param wt Le thread associé au client (WestThread).
     * @return true si l'ajout est réussi, false sinon.
     */
    public boolean add(String fullName, WestThread wt) {
        Objects.requireNonNull(wt, "WestThread is null");
        if (sessions.containsKey(fullName)) {
            return false; // todo que faire si le user se connecte avec +2 appareils ?
        }
        if (fullName != null && !fullName.isBlank()) {
            sessions.put(fullName, wt);
        }
        return true;
    }

    /**
     * Supprime une session utilisateur.
     *
     * @param fullName Le nom complet de l'utilisateur à supprimer.
     */
    public void remove(String fullName) {
        if (fullName != null && !fullName.isBlank()) {
            sessions.remove(fullName);
        }
    }

    /**
     * Envoie un message à un utilisateur spécifique.
     *
     * @param fullName Le nom du destinataire.
     * @param message Le contenu du message.
     */
    public void send(String fullName, String message) {
        if (fullName != null && !fullName.isBlank()) {
            if (!sessions.containsKey(fullName)) {
                LOGGER.info("Le client {} n'est pas connecté", fullName);
            } else {
                LOGGER.info("Message envoyé à {}", fullName);
                sessions.get(fullName).send(message);
            }
        }
    }

    /**
     * Définit le thread de communication vers l'Est.
     *
     * @param eastThread Le thread East.
     */
    public void setEastThread(EastThread eastThread) {
        this.eastThread = eastThread;
    }

    /**
     * Envoie un message au haut-parleur (Speaker).
     *
     * @param msg Le message à envoyer.
     */
    public void sendSpeaker(String msg) {
        LOGGER.info("Message envoyé au speaker : " + msg);
    }
}