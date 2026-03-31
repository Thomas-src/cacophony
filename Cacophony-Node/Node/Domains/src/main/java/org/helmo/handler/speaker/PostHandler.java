package org.helmo.handler.speaker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.Message;
import org.helmo.handler.CommandHandler;
import org.helmo.handler.SendHandler;
import org.helmo.handler.SubscribeHandler;
import org.helmo.server.ActiveSession;
import org.helmo.task.SpeakerTask;
import org.helmo.task.TaskContext;

import java.util.ArrayList;
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

        String message = args.getLast();
        var matcherMSG = Message.P_CMD_MSG.matcher(message);
        var matcherSUB = Message.P_CMD_MSG.matcher(message);
        if(matcherMSG.matches()) {
            for (int i = 1; i <= matcherMSG.groupCount(); i++) {
                args.add(matcherMSG.group(i));
            }
            doMsg(args);
        } else if(matcherSUB.matches()) {
            for (int i = 1; i <= matcherSUB.groupCount(); i++) {
                args.add(matcherSUB.group(i));
            }
            doSub(args);
        }

        // soit msg => soit user@domain soit tag@domain



        // soit subscribe user@domain soit tag@domain


       return null;
    }


    private void doMsg(List<String> args) {
        LOGGER.info("do msg");
    }

    private void doSub(List<String> args) {
        LOGGER.info("do sub");
    }
}
