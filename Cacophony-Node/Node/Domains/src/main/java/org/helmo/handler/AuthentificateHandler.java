package org.helmo.handler;

import org.helmo.Exception.HandlerException;
import org.helmo.Grammar;
import org.helmo.repository.IUserRepository;

import java.util.regex.Pattern;

public class AuthentificateHandler implements  CommandHandler{

    private final IUserRepository userRepository;

    public AuthentificateHandler(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String handle(String message) throws HandlerException {
        return "ERR";
    }

    @Override
    public Pattern getPattern() {
        return Grammar.P_USER_AT_DOMAIN;
    }
}
