package org.helmo.handler;

import org.helmo.Exception.HandlerException;
import org.helmo.MessageBuilder;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class CommandMenu {
    private final LinkedHashMap<Pattern, CommandHandler> handlers = new LinkedHashMap<>(); // ordre important

    public void add(Pattern command, CommandHandler handler) {
        handlers.put(command, handler);
    }

    public String execute(ClientState state, String command) throws HandlerException {
        for (var entry : handlers.entrySet()) {
            Pattern pattern = entry.getKey();
            CommandHandler handler = entry.getValue();
            if (pattern.matcher(command).matches()) {
                return handler.handle(state, command);
            }
        }

        return MessageBuilder.buildError();
    }
}
