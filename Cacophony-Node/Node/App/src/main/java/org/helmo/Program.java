package org.helmo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.ConfigReaderException;
import org.helmo.Exception.ServerException;
import org.helmo.crypto.CryptoService;
import org.helmo.handler.*;
import org.helmo.handler.speaker.PostHandler;
import org.helmo.repository.IConfigReaderRepository;
import org.helmo.repository.ITagRepository;
import org.helmo.repository.IUserRepository;
import org.helmo.server.ActiveSession;
import org.helmo.server.IServer;
import org.helmo.server.ServerFactory;
import org.helmo.server.threads.AdvertThread;
import org.helmo.server.threads.EastThread;
import org.helmo.server.threads.TaskThread;
import org.helmo.services.SubscriberService;
import org.helmo.services.TagManagerService;
import org.helmo.services.UserManagerService;

import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;

public class Program {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {

        try (PrintStream cout = new PrintStream(System.out)) {
            // Création des repository & services
            ActiveSession activeSession = new ActiveSession();
            IConfigReaderRepository configReader = new JsonConfigReaderNode();
            Config config = configReader.getConfig();

            IUserRepository userRepository = new JsonUserRepository(config);
            UserManagerService userManagerService = new UserManagerService(userRepository);

            ITagRepository tagRepository = new JsonTagRepository(config, userManagerService);
            TagManagerService tagManagerService = new TagManagerService(tagRepository);

            SubscriberService subscriberService = new SubscriberService(userManagerService, tagManagerService);
            CryptoService cryptoService = new CryptoService();
            CommandMenu menu = new CommandMenu();
            AdvertThread advertThread = new AdvertThread(config);


            TaskThread taskthread = new TaskThread(menu);
            EastThread eastThread = new EastThread(config, taskthread, activeSession, cryptoService);


            SendHandler sendHandler = new SendHandler(subscriberService, activeSession, eastThread, config);
            SubscribeHandler subscribeHandler = new SubscribeHandler(userManagerService, tagManagerService);

            menu.add(Message.P_CMD_ONBOARDING, new OnboardingHandler(userManagerService));
            menu.add(Message.P_CMD_USER, new AuthentificateHandler(userManagerService));
            menu.add(Message.P_CMD_SEND, sendHandler);
            menu.add(Message.P_CMD_SUBSCRIBE, subscribeHandler);
            menu.add(Message.P_CMD_RESPONSE, new ResponseChallengeHandler());
            menu.add(Message.P_CMD_LEAVE, new LeaveHandler());
            menu.add(Message.P_POST, new PostHandler(activeSession, sendHandler, subscribeHandler));

            taskthread.start();
            eastThread.start();

            IServer server = ServerFactory.createServer(configReader, taskthread, activeSession);

            server.run();

            advertThread.start();
        } catch (ConfigReaderException e) {
            LOGGER.error("Erreur lors de la lecture de la config node", e);
        } catch (ServerException e) {
            LOGGER.error("Erreur lors de la création du serveur", e);
        }
    }
}