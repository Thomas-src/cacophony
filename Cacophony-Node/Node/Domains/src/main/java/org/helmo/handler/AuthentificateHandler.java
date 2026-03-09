package org.helmo.handler;

import org.helmo.Exception.HandlerException;
import org.helmo.repository.IUserRepository;

public class AuthentificateHandler implements  CommandHandler{

    private final IUserRepository userRepository;

    public AuthentificateHandler(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String handle(String message) throws HandlerException {
        return "ERR";
    }
}
