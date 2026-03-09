package org.helmo.handler;

import org.helmo.Exception.HandlerException;

public interface CommandHandler {
    String handle(String message) throws HandlerException;
}