package org.helmo.task;

import org.helmo.Message;

import java.util.regex.Pattern;

public enum TypeTask {
    ONBOARDING(Message.P_CMD_ONBOARDING),
    LEAVE(Message.P_CMD_LEAVE),
    USER(Message.P_CMD_USER),
    SEND(Message.P_CMD_SEND),
    RESPONSE(Message.P_CMD_RESPONSE),
    SUBSCRIBE(Message.P_CMD_SUBSCRIBE),
    POST(Message.P_POST);

    private final Pattern pattern;

    TypeTask(Pattern pattern) {
        this.pattern = pattern;
    }

    public Pattern getPattern() { return pattern; }


}
