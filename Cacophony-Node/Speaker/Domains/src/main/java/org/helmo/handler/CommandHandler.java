package org.helmo.handler;

import org.helmo.exceptions.HandlerException;

import java.util.List;

public interface CommandHandler {
    void handle(List<String> args) throws HandlerException;
}
