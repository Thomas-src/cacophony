package org.helmo.task;

import org.helmo.client.Client;
import org.helmo.server.ActiveSession;
import org.helmo.server.threads.WestThread;

/**
 * Représente une session client active au sein du système.
 */
public class ClientSession implements TaskContext {

    private final String randomString;
    private Client client;
    private WestThread thread;
    private boolean authenticated = false;
    private boolean shouldKeepOpen = true;
    private String domain;
    private ActiveSession activeSession;

    /**
     * Définit le client associé à cette session.
     *
     * @param client Le client à associer.
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Récupère le client associé à la session.
     *
     * @return Le client.
     */
    public Client getClient() {
        return client;
    }

    /**
     * Initialise une nouvelle session client.
     *
     * @param randomString  Chaîne aléatoire pour l'identification.
     * @param thread        Le thread de communication associé.
     * @param domain        Le domaine de la session.
     * @param activeSession Le gestionnaire des sessions actives.
     */
    public ClientSession(String randomString, WestThread thread, String domain,ActiveSession activeSession) {
        this.randomString = randomString;
        this.thread = thread;
        this.domain = domain;
        this.activeSession = activeSession;
    }

    /**
     * Récupère la chaîne aléatoire de la session.
     *
     * @return La chaîne aléatoire.
     */
    public String getRandomString() {
        return randomString;
    }

    /**
     * Récupère le nom d'utilisateur du client.
     *
     * @return Le nom d'utilisateur.
     */
    public String getUsername() {
        return client.getName();
    }

    /**
     * Tente d'authentifier la session auprès du gestionnaire de sessions.
     *
     * @return true si l'authentification a réussi, sinon false.
     */
    public boolean setAuthenticate() {
        return authenticated = activeSession.add(getUserFullName(), thread);
    }

    /**
     * Vérifie si la session est authentifiée.
     *
     * @return true si authentifiée.
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * Récupère le nom complet de l'utilisateur.
     *
     * @return Le nom complet.
     */
    public String getUserFullName() {
        return client.getFullName();
    }

    /**
     * Indique si la connexion doit rester ouverte.
     *
     * @return true si elle doit rester ouverte.
     */
    public boolean isShouldKeepOpen() {
        return shouldKeepOpen;
    }

    /**
     * Ferme la session et la retire des sessions actives.
     */
    public void closeSession() {
        this.shouldKeepOpen = false;
        activeSession.remove(client.getFullName());
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public void send(String message) {
        this.thread.send(message);
    }

    /**
     * Vérifie la réponse au challenge d'authentification.
     */
    public boolean verifyChallenge(String clientResponse, String challengeResponse) {
        return client.verifyChallengeResponse(clientResponse, challengeResponse);
    }
}
