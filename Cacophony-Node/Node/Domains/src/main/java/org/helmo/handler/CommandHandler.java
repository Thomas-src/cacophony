package org.helmo.handler;

import org.helmo.Exception.HandlerException;

import java.util.regex.Pattern;

public interface CommandHandler {
    String handle(String message) throws HandlerException;
    default Pattern getPattern() {
        return null;
    }

    default boolean validate(String message) {
        Pattern pattern = getPattern();
        return pattern == null || pattern.matcher(message).matches();
    }

}