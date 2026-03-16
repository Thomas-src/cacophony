package org.helmo.handler;

import org.helmo.client.Client;

import java.util.Objects;

public class ClientState {
    private Client client;
    private boolean isConnected = false;

    public void authenticate(Client client) {
        Objects.requireNonNull(client);
        this.client = Client.Of(client);
        this.isConnected = true;
    }
    public boolean isConnected() {
        return isConnected;
    }

    public String getClientName() {
        return client ==  null ? null : client.getName();
    }

    public void disconnect() {
        isConnected = false;
        client = null;
    }

}
