package org.helmo.handler;

import org.helmo.Exception.HandlerException;
import org.helmo.Grammar;
import org.helmo.MessageBuilder;
import org.helmo.repository.IUserRepository;

import java.util.regex.Pattern;

public class AuthentificateHandler implements  CommandHandler{

    private final IUserRepository userRepository;

    public AuthentificateHandler(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String handle(ClientState state,String message) throws HandlerException {
        if(state.isConnected()) { // si authentifié problème => pas sur authentification
            return MessageBuilder.buildError();
        }

        return MessageBuilder.buildError();
    }

    @Override
    public Pattern getPattern() {
        return Grammar.P_USER_AT_DOMAIN;
    }
}
