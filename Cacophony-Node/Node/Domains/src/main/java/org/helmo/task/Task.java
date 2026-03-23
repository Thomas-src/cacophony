package org.helmo.task;

import org.helmo.server.threads.WestThread;

public record Task(String id, TypeTask type, WestThread sender, String receiver, String message) {
public void send(String message) {
    sender.send(message);
}

}

