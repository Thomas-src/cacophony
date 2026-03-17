package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.client.ClientSession;
import org.helmo.repository.IUserRepository;

import java.util.List;

public class SubscribreHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private final IUserRepository userRepository;

    public SubscribreHandler(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String handle(List<String> args, ClientSession session) throws HandlerException {
        return "";
    }
}
