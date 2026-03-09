package org.helmo.handler;

import org.helmo.Exception.HandlerException;
import org.helmo.MessageBuilder;

import java.util.HashMap;
import java.util.Map;

public class CommandMenu {
    private final Map<String, CommandHandler> handlers = new HashMap<>();

    public void add(String command, CommandHandler handler) {
        handlers.put(command.toUpperCase(), handler);
    }

    public String execute(String command) throws HandlerException {
        String cmd = command.split(" ")[0].toUpperCase();

        CommandHandler handler = handlers.get(cmd);

        if (handler == null) {
            return MessageBuilder.buildError();
        }
        return handler.handle(command);
    }
}
