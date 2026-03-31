package org.helmo.handler.speaker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.handler.CommandHandler;
import org.helmo.handler.SendHandler;
import org.helmo.handler.SubscribeHandler;
import org.helmo.server.ActiveSession;
import org.helmo.task.SpeakerTask;
import org.helmo.task.TaskContext;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PostHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private final ActiveSession activeSession;
    private final Set<String> processedIds = ConcurrentHashMap.newKeySet();

    public PostHandler(ActiveSession activeSession, SendHandler sendHandler, SubscribeHandler subscribeHandler) {
        this.activeSession = Objects.requireNonNull(activeSession, "activeSession is null");
    }

    @Override
    public String handle(List<String> args, TaskContext task) throws HandlerException {
        if (!(task instanceof SpeakerTask speakerTask)) {
            throw new HandlerException("POST requires a speaker context");
        }
        String idAtDomain = args.getFirst();
        if (!processedIds.add(idAtDomain)) {
            LOGGER.info("POST id dupliqué: {}", idAtDomain);
            return null;
        }
        LOGGER.info("Exécution du post: {}", idAtDomain);

        // attention sur msg => si contient un tag #salut => #salut@node1 pas #salut@node2 ???

       return null;
    }
}
