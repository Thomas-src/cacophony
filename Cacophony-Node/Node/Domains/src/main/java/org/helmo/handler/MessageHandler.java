package org.helmo.handler;

import org.helmo.Exception.HandlerException;
import org.helmo.Grammar;
import org.helmo.client.ClientSession;

import java.util.regex.Pattern;

public class MessageHandler implements CommandHandler {
    @Override
    public String handle(String message, ClientSession session) throws HandlerException {
        return "ERR";
    }

}
