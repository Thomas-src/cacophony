package org.helmo.server;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.UserReaderException;
import org.helmo.Exception.UserSaveException;
import org.helmo.server.threads.WestThread;
import org.helmo.services.UserManagerService;
import org.helmo.task.IMessageSender;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Objects;

/**
 * Gère les sessions actives des utilisateurs connectés et la distribution des messages.
 */
public class ActiveSession {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Map<String, IMessageSender> sessions = new ConcurrentHashMap<>();
    private final UserManagerService userManagerService;

    /**
     * Initialise une nouvelle instance de ActiveSession.
     *
     * @param userManagerService Le service de gestion des utilisateurs pour la persistance des messages.
     */
    public ActiveSession(UserManagerService userManagerService) {
        this.userManagerService = userManagerService;
    }

    /**
     * Ajoute une nouvelle session utilisateur.
     *
     * @param fullName Le nom complet de l'utilisateur.
     * @param wt L'expéditeur de messages associé à la session.
     * @return true si la session a été ajoutée, false si l'utilisateur est déjà connecté.
     * @throws NullPointerException si wt est null.
     */
    public boolean add(String fullName, IMessageSender wt) {
        Objects.requireNonNull(wt, "WestThread is null");
        if (sessions.containsKey(fullName)) {
            return false;
        }
        if (fullName != null && !fullName.isBlank()) {
            sessions.put(fullName, wt);
        }
        return true;
    }

    /**
     * Supprime la session d'un utilisateur.
     *
     * @param fullName Le nom complet de l'utilisateur à déconnecter.
     */
    public void remove(String fullName) {
        if (fullName != null && !fullName.isBlank()) {
            sessions.remove(fullName);
        }
    }

    /**
     * Envoie un message à un utilisateur ou le sauvegarde s'il est hors ligne.
     *
     * @param fullName Le nom complet du destinataire.
     * @param message Le contenu du message à envoyer.
     */
    public void send(String fullName, String message) {
        if (fullName != null && !fullName.isBlank()) {
            if (!sessions.containsKey(fullName)) {
                try {
                    userManagerService.saveMessageForUser(fullName, message);
                    LOGGER.info("Message crypté enregistré pour {}", fullName);
                } catch (UserSaveException e) {
                    throw new RuntimeException(e);
                }
            } else {
                LOGGER.info("Message envoyé à {}, {}", fullName, message);
                sessions.get(fullName).send(message);
            }
        }
    }

    /**
     * Récupère et envoie tous les messages en attente pour un utilisateur qui vient de se connecter.
     *
     * @param fullName Le nom complet de l'utilisateur.
     */
    public void sendPastMessages(String fullName) {
        try {
            LOGGER.info("Vérification du user {}", fullName);
            //  afficher les messages à l'utilisateur qui se connecte (send)
            userManagerService.getUserMessagesAndEmpty(fullName).forEach(m->send(fullName, m));
        } catch (UserSaveException | UserReaderException e) {
            throw new RuntimeException(e);
        }
    }
}