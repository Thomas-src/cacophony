package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.crypto.CryptoProvider;
import org.helmo.domain.Domain;
import org.helmo.exceptions.HandlerException;
import org.helmo.network.SpeakerTcpConnector;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommandMenu {
    private static final Logger LOGGER = LogManager.getLogger();
    private final LinkedHashMap<Pattern, CommandHandler> handlers = new LinkedHashMap<>();

    public CommandMenu() {
    }

    public void add(Pattern command, CommandHandler handler) {
        handlers.put(command, handler);
    }

    public void execute(String message) throws HandlerException {
        if (tryExecute(message)) {
            return;
        }
        LOGGER.warn("[UDP] Command {} not found", message);
    }

    private boolean tryExecute(String message) throws HandlerException {
        for (var entry : handlers.entrySet()) {
            Pattern pattern = entry.getKey();
            CommandHandler handler = entry.getValue();
            Matcher matcher = pattern.matcher(message);

            if (matcher.matches()) {
                executeCommand(handler, matcher);
                return true;
            }
        }
        return false;
    }

    private void executeCommand(CommandHandler handler, Matcher matcher) throws HandlerException {
        List<String> args = new ArrayList<>();
        for (int i = 1; i <= matcher.groupCount(); i++) {
            args.add(matcher.group(i));
        }

        handler.handle(args);
    }
}
