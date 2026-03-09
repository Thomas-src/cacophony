package org.helmo.handler;

import org.helmo.Exception.HandlerException;
import org.helmo.Grammar;

import java.util.regex.Pattern;

public class MessageHandler implements CommandHandler {
    @Override
    public String handle(String message) throws HandlerException {
        if (!validate(message)) {
            return "ERR"; // message invalide
        }

        return "ERR";
    }

    @Override
    public Pattern getPattern() {
        return Grammar.P_MESSAGE;
    }
}
