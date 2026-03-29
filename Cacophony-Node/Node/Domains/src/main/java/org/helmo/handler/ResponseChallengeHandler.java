package org.helmo.handler;

import org.apache.logging.log4j.*;
import org.helmo.Exception.HandlerException;
import org.helmo.MessageBuilder;
import org.helmo.task.ClientSession;
import org.helmo.task.TaskContext;

import java.util.List;

public class ResponseChallengeHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    public ResponseChallengeHandler() {
    }

    @Override
    public String handle(List<String> args, TaskContext task) throws HandlerException {
        if (!(task instanceof ClientSession session)) {
            throw new HandlerException("Task auth nécessite une session client");
        }

        if(session.isAuthenticated()) {
            return MessageBuilder.buildError();
        }
        LOGGER.info("Response");
        String clientResponse = args.getFirst();
        String challenge = session.getRandomString();

        if (session.verifyChallenge(clientResponse, challenge)) {
            LOGGER.info("Authentication successful for {}", session.getUsername());
            return session.setAuthenticate() ? MessageBuilder.buildOk() : MessageBuilder.buildError();
        }

        LOGGER.warn("Authentication failed for {}", session.getUsername());
        return MessageBuilder.buildError();
    }
}