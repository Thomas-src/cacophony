package org.helmo.client;

public class ClientSession {

    private String randomString;
    private String username;
    private String bcryptHash;
    private boolean authenticated = false;
    private Client client;

    public void setClient(Client client) {
        this.client = client;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setBcryptHash(String bcryptHash) {
        this.bcryptHash = bcryptHash;
    }

    public String getBcryptHash() {
        return bcryptHash;
    }

    public String getUsername() {
        return username;
    }

    public void authenticate(){
        authenticated = true;
    }

    public boolean isAuthenticated(){
        return authenticated;
    }
}
