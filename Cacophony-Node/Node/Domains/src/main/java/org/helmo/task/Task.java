package org.helmo.task;

import org.helmo.client.ClientSession;

public record Task(String id, TypeTask type, ClientSession sender, String message) {
public void send(String message) {
    sender.send(message);
}

}

