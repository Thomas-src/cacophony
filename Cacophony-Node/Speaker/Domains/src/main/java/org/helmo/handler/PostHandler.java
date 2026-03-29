package org.helmo.handler;

import org.helmo.exceptions.HandlerException;
import org.helmo.network.SpeakerTcpConnector;

import java.util.List;

public class PostHandler implements CommandHandler{

    private final SpeakerTcpConnector connector;
    public PostHandler(SpeakerTcpConnector connector) {
        this.connector = connector;
    }

    @Override
    public void handle(List<String> args) throws HandlerException {
    // todo
        // 1. décrypter le message avec la clé node1 puis envoyer vers le node2 crypté avec sa clé
    }
}
