package org.helmo.handler;

import org.helmo.Exception.HandlerException;


public interface CommandHandler {
    String handle(ClientState state, String message) throws HandlerException;

}