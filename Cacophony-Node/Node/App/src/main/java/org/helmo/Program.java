package org.helmo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.ConfigReaderException;
import org.helmo.Exception.ServerException;
import org.helmo.handler.*;
import org.helmo.repository.IConfigReaderRepository;
import org.helmo.repository.ITagRepository;
import org.helmo.repository.IUserRepository;
import org.helmo.server.IServer;
import org.helmo.server.ServerFactory;
import org.helmo.server.threads.TaskThread;
import org.helmo.services.TagManagerService;
import org.helmo.services.UserManagerService;

import java.io.PrintStream;

public class Program {
    private static final Logger LOGGER = LogManager.getLogger();
    public static void main(String[] args) {

            try (PrintStream cout = new PrintStream(System.out)){
                // Création des repository & services
                IConfigReaderRepository configReader = new JsonConfigReaderNode();
                Config config = configReader.getConfig();

                IUserRepository userRepository = new JsonUserRepository(config);
                UserManagerService userManagerService = new UserManagerService(userRepository);

                ITagRepository tagRepository = new JsonTagRepository(config, userManagerService);
                TagManagerService tagManagerService = new TagManagerService(tagRepository);

                CommandMenu menu = new CommandMenu();
                menu.add(Message.P_CMD_ONBOARDING, new OnboardingHandler(userManagerService));
                menu.add(Message.P_CMD_USER, new AuthentificateHandler(userManagerService));
                menu.add(Message.P_CMD_SEND, new SendHandler(userManagerService, tagManagerService));
                menu.add(Message.P_CMD_SUBSCRIBE, new SubscribeHandler(userRepository));
                menu.add(Message.P_CMD_RESPONSE, new ResponseChallengeHandler());
                menu.add(Message.P_CMD_LEAVE, new LeaveHandler());

                TaskThread taskthread = new TaskThread(menu);
                taskthread.start();

                // Création du serveur + démarrage
                IServer server = ServerFactory.createServer(configReader, taskthread);
                server.run();
            } catch (ConfigReaderException | ServerException e) {
                LOGGER.error("Le serveur n'a pas pu démarrer",e);
            }
    }
}