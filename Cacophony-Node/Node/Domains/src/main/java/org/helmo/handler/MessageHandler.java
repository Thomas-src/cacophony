package org.helmo.handler;

import org.helmo.Exception.HandlerException;
import org.helmo.Grammar;
import org.helmo.MessageBuilder;

import java.util.regex.Pattern;

public class MessageHandler implements CommandHandler {
    @Override
    public String handle(ClientState state, String message) throws HandlerException {

        return MessageBuilder.buildError();
    }
}
