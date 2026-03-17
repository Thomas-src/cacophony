package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.Exception.UserReaderException;
import org.helmo.Exception.UserSaveException;
import org.helmo.Grammar;
import org.helmo.MessageBuilder;
import org.helmo.client.Client;
import org.helmo.client.ClientSession;
import org.helmo.repository.IUserRepository;

import java.util.List;

public class SubscribeHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private final IUserRepository userRepository;

    public SubscribeHandler(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String handle(List<String> args, ClientSession session) throws HandlerException {
        LOGGER.info("\n===========\n" + args.toString() + "\n===========");
        LOGGER.info("\n===========\nSize:" + args.size() + "\n===========");
        if (args.isEmpty()) {
            throw new HandlerException("Demande invalide");
        }

        String target = args.getFirst();
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
        LOGGER.info("SUBSCRIBE USER");
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
            return MessageBuilder.buildError();
        }
        return MessageBuilder.buildOk();
    }

    private String tagSubscribe(String target) {
        LOGGER.info("SUBSCRIBE TAG");
        return MessageBuilder.buildError();
    }
}
