package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.Exception.UserReaderException;
import org.helmo.Exception.UserSaveException;
import org.helmo.Grammar;
import org.helmo.Message;
import org.helmo.client.Client;
import org.helmo.client.ClientSession;
import org.helmo.repository.IUserRepository;
import org.helmo.subscriber.NativeUser;

import java.util.List;

public class SubscribreHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private final IUserRepository userRepository;

    public SubscribreHandler(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String handle(List<String> args, ClientSession session) throws HandlerException {
        LOGGER.info("\n===========\n" + args.toString() + "\n===========");

        if (args.size() < 2) {
            throw new HandlerException("Demande invalide");
        }

        String target = args.get(1);
        String response = "";

        if (Grammar.P_TAG_AT_DOMAIN.matcher(target).matches()) {
            response = tagSubscribe(target);
        } else if (Grammar.P_USER_AT_DOMAIN.matcher(target).matches()) {
            response = userSubscribe(target, session);
        } else {
            throw new HandlerException("Demande invalide");
        }

        return response;
    }


    private String userSubscribe(String target, ClientSession session) {
        LOGGER.info("SUBSCRIBE");
        List<Client> clients;
        String targetName = target.split("@")[0];
        try {
            clients = userRepository.getUsers();
            for (Client client : clients) {
                if (targetName.equals(client.getName())) {
                    LOGGER.info("\n========" + session.getUsername() + "========\n");
                    String sub = session.getUsername() + "@node1.swilabus.xyz";
                    userRepository.addSubscribersFor(client, sub);
                }
            }
        } catch (UserReaderException | UserSaveException e) {
            return "ERR";
        }
        return "OK";
    }

    private String tagSubscribe(String target) {
        return "ERR";
    }
}
