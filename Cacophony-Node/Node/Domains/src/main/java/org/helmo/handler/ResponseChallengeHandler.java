package org.helmo.handler;

import org.apache.logging.log4j.*;
import org.helmo.Exception.HandlerException;
import org.helmo.Exception.UserReaderException;
import org.helmo.MessageBuilder;
import org.helmo.client.Client;
import org.helmo.client.ClientSession;
import org.helmo.client.HashPassword;
import org.helmo.repository.IUserRepository;
import org.helmo.server.ActiveSession;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class ResponseChallengeHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();


    public ResponseChallengeHandler() {
    }

    @Override
    public String handle(List<String> args, ClientSession session) {
        if(session.isAuthenticated()) {
            return MessageBuilder.buildError();
        }
        String clientResponse = args.getFirst();
        String challenge = session.getRandomString();

        Client client = session.getClient();

        if (client.verifyChallengeResponse(clientResponse, challenge)) {
            session.authenticate();
            LOGGER.info("Authentication successful for {}", session.getUsername());
            return MessageBuilder.buildOk();
        }

        LOGGER.warn("Authentication failed for {}", session.getUsername());
        return MessageBuilder.buildError();
    }
}