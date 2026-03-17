package org.helmo.client;

import org.helmo.server.ActiveSession;
import org.helmo.server.threads.WestThread;

public class ClientSession {

    private final String randomString;
    private Client client;
    private WestThread thread;
    private String bcryptHash;
    private boolean authenticated = false;
    private boolean shouldKeepOpen = true;

    public void setClient(Client client) {
        this.client = client;
    }
    public void setWestThread(WestThread thread) {
        this.thread = thread;
    }

    public Client getClient() {
        return client;
    }

    public ClientSession(String randomString) {
        this.randomString = randomString;
    }

    public String getRandomString() {
        return randomString;
    }

    public void setBcryptHash(String bcryptHash) {
        this.bcryptHash = bcryptHash;
    }

    public String getUsername() {
        return client.getName();
    }

    public void authenticate() {
        ActiveSession.add(getUserFullName(), thread);
        authenticated = true;
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
        ActiveSession.remove(client.getName());
    }


}
