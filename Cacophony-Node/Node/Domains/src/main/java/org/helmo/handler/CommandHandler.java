package org.helmo.handler;

import org.helmo.Exception.HandlerException;
import org.helmo.client.ClientSession;


public interface CommandHandler {
    String handle(String message, ClientSession session) throws HandlerException;
}