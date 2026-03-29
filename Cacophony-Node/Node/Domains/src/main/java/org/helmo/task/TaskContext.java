package org.helmo.task;

public interface TaskContext {
    String getDomain();
    void send(String message);
}
