package org.helmo.task;

public record Task(String id, TypeTask type, TaskContext sender, String message) {
    public void send(String message) {
        sender.send(message);
    }
}

