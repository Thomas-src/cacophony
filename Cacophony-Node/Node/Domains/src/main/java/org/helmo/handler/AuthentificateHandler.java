package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.Exception.UserReaderException;
import org.helmo.Grammar;
import org.helmo.client.Client;
import org.helmo.client.ClientSession;
import org.helmo.repository.IUserRepository;

import java.util.List;
import java.util.regex.Pattern;

public class AuthentificateHandler implements  CommandHandler{
    private static final Logger LOGGER = LogManager.getLogger();


    private final IUserRepository userRepository;

    public AuthentificateHandler(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String handle(String message, ClientSession session) throws HandlerException {
        String username = message.split(" ")[1].trim();
        Client user = null;

        try {
            List<Client> clients = userRepository.getUsers();
            for (Client client : clients) {
                if (client.getName().equals(username)) {
                    user = client;
                    break;
                }
            }

        } catch (UserReaderException e) {
            user = null;
        }

        if(user == null){
            return "ERR";
        }

        session.setClient(user);

        String fullhash = user.getHashPassword().toString();

        session.setUsername(username);
        session.setBcryptHash(fullhash);

        String response = "CHALLENGE " + user.getHashPassword().bcryptRound() + " " + user.getHashPassword().bcryptSalt();
        LOGGER.info(response);
        return response;
    }


}
