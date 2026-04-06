package org.helmo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.ConfigReaderException;
import org.helmo.Exception.ServerException;
import org.helmo.crypto.CryptoService;
import org.helmo.handler.*;
import org.helmo.handler.speaker.PostHandler;
import org.helmo.handler.utils.CommandMenu;
import org.helmo.repository.IConfigRepository;
import org.helmo.repository.IMessageRepository;
import org.helmo.repository.ITagRepository;
import org.helmo.repository.IUserRepository;
import org.helmo.server.ActiveSession;
import org.helmo.server.IServer;
import org.helmo.server.ServerFactory;
import org.helmo.server.threads.AdvertThread;
import org.helmo.server.threads.EastThread;
import org.helmo.server.threads.TaskThread;
import org.helmo.services.*;
import org.helmo.services.handler.MessageService;
import org.helmo.services.handler.SubscriberService;

import java.io.PrintStream;

public class Program {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {

        try (PrintStream cout = new PrintStream(System.out)) {
            // Création des repository & services

            IConfigRepository configRepository = new JsonConfigRepository();
            Config config = configRepository.getConfig();

            CryptoService cryptoService = new CryptoService(config.base64AES());

            IUserRepository userRepository = new JsonUserRepository(config);
            UserManagerService userManagerService = new UserManagerService(userRepository, cryptoService);
            ActiveSession activeSession = new ActiveSession(userManagerService);
            ITagRepository tagRepository = new JsonTagRepository(config, userManagerService);
            TagManagerService tagManagerService = new TagManagerService(tagRepository);
            MessageService messageService = new MessageService(userManagerService, tagManagerService);


            CommandMenu menu = new CommandMenu();
            AdvertThread advertThread = new AdvertThread(config);
            IMessageRepository messageRepository = new JsonNodeMessageRepository();
            TaskThread taskthread = new TaskThread(menu);
            EastThread eastThread = new EastThread(config, taskthread, cryptoService, messageRepository);
            IdGeneratorService idGeneratorService = new IdGeneratorService(config.id(), configRepository);


            SpeakerService speakerService = new SpeakerService(cryptoService, eastThread);
            SendHandler sendHandler = new SendHandler(messageService, activeSession, speakerService, idGeneratorService);


            SubscriberService subscriberService = new SubscriberService(userManagerService,tagManagerService,speakerService,idGeneratorService);
            SubscribeHandler subscribeHandler = new SubscribeHandler(subscriberService);
            menu.add(Message.P_CMD_ONBOARDING, new OnboardingHandler(userManagerService));
            menu.add(Message.P_CMD_USER, new AuthentificateHandler(userManagerService));
            menu.add(Message.P_CMD_SEND, sendHandler);
            menu.add(Message.P_CMD_SUBSCRIBE, subscribeHandler);
            menu.add(Message.P_CMD_RESPONSE, new ResponseChallengeHandler());
            menu.add(Message.P_CMD_LEAVE, new LeaveHandler());
            menu.add(Message.P_POST, new PostHandler(activeSession, subscriberService, tagManagerService, speakerService,userManagerService, messageService));

            taskthread.start();
            eastThread.start();
            advertThread.start();
            IServer server = ServerFactory.createServer(configRepository, taskthread, activeSession);

            server.run();


        } catch (ConfigReaderException e) {
            LOGGER.error("Erreur lors de la lecture de la config node", e);
        } catch (ServerException e) {
            LOGGER.error("Erreur lors de la création du serveur", e);
        }
    }
}