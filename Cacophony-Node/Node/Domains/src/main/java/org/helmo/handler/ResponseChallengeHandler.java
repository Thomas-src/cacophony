package org.helmo.handler;

import org.apache.logging.log4j.*;
import org.helmo.Exception.HandlerException;
import org.helmo.Exception.UserReaderException;
import org.helmo.client.Client;
import org.helmo.client.ClientSession;
import org.helmo.client.HashPassword;
import org.helmo.repository.IUserRepository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ResponseChallengeHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();


    public ResponseChallengeHandler() {
    }

    @Override
    public String handle(String message, ClientSession session) {

        String clientResponse = message.split(" ")[1];
        String challenge = session.getRandomString();

        Client client = session.getClient();

        if (client.verifyChallengeResponse(clientResponse, challenge)) {
            session.authenticate();
            LOGGER.info("Authentication successful for {}", session.getUsername());
            return "OK";
        }

        LOGGER.warn("Authentication failed for {}", session.getUsername());
        return "ERR";
    }
}