package org.helmo.handler;

import org.helmo.Exception.HandlerException;
import org.helmo.client.ClientSession;

import java.util.List;


public interface CommandHandler {
    String handle(List<String> args, ClientSession session) throws HandlerException;
}