package org.helmo.task;

import org.helmo.client.Client;
import org.helmo.server.ActiveSession;
import org.helmo.server.threads.WestThread;

public class ClientSession implements TaskContext {

    private final String randomString;
    private Client client;
    private WestThread thread;
    private boolean authenticated = false;
    private boolean shouldKeepOpen = true;
    private String domain;
    private ActiveSession activeSession;

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getClient() {
        return client;
    }

    public ClientSession(String randomString, WestThread thread, String domain,ActiveSession activeSession) {
        this.randomString = randomString;
        this.thread = thread;
        this.domain = domain;
        this.activeSession = activeSession;
    }

    public String getRandomString() {
        return randomString;
    }

    public String getUsername() {
        return client.getName();
    }

    public boolean setAuthenticate() {
        return authenticated = activeSession.add(getUserFullName(), thread);
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getUserFullName() {
        return client.getFullName();
    }

    public boolean isShouldKeepOpen() {
        return shouldKeepOpen;
    }

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

    public boolean verifyChallenge(String clientResponse, String challengeResponse) {
        return client.verifyChallengeResponse(clientResponse, challengeResponse);
    }
}

