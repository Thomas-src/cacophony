package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.MessageBuilder;
import org.helmo.task.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

public class CommandMenu {
    private static final Logger LOGGER = LogManager.getLogger();
    private final LinkedHashMap<Pattern, CommandHandler> handlers = new LinkedHashMap<>();

    public void add(Pattern command, CommandHandler handler) {
        handlers.put(command, handler);
    }

    public String execute(Task task) throws HandlerException {
        for (var entry : handlers.entrySet()) {
            Pattern pattern = entry.getKey();
            CommandHandler handler = entry.getValue();
            var matcher = pattern.matcher(task.message());

            // Extraitre les arguments
            if (matcher.matches()) {
                List<String> args = new ArrayList<>();
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    LOGGER.info("Arg {}: {}", i,  matcher.group(i));
                    args.add(matcher.group(i));
                }

                return handler.handle(args, task.sender());
            }
        }

        LOGGER.warn("Command {} not found", task.message());
        return MessageBuilder.buildError();
    }
}
