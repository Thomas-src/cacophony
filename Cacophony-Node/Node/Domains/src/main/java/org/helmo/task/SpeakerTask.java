package org.helmo.task;

public class SpeakerTask implements TaskContext {
    private final String domain;

    public SpeakerTask(String domain) {
        this.domain = domain;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public void send(String message) {
        // todo
    }
}
