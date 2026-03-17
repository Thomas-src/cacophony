package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.MessageBuilder;
import org.helmo.client.ClientSession;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

public class CommandMenu {
    private static final Logger LOGGER = LogManager.getLogger();
    private final LinkedHashMap<Pattern, CommandHandler> handlers = new LinkedHashMap<>();

    public void add(Pattern command, CommandHandler handler) {
        handlers.put(command, handler);
    }

    public String execute(String command, ClientSession session) throws HandlerException {
        for (var entry : handlers.entrySet()) {
            Pattern pattern = entry.getKey();
            CommandHandler handler = entry.getValue();

            if (pattern.matcher(command).matches()) {
                return handler.handle(command, session);
            }
        }

        LOGGER.warn("Command {} not found", command);
        return MessageBuilder.buildError();
    }

}
