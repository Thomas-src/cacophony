package org.helmo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.repository.IConfigReaderRepository;
import org.helmo.server.IServer;
import org.helmo.server.ServerFactory;
import org.helmo.services.IOnboarding;
import org.helmo.services.OnboardingService;

import java.io.PrintStream;

public class Program {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {
              /* // Pour tester l'appel aux fichiers
              var configReader = new JsonConfigReaderNode();
            var reader = new JsonUserRepository(configReader.getConfig());

            var users = reader.getUsers();
            users.forEach(user -> {
                LOGGER.info("User: "+user.toString());
            });
            var reader2 = new JsonTagRepository(configReader.getConfig(), new UserManagerService(reader));
            var tags = reader2.getTags();
            tags.forEach(tag -> {
                LOGGER.info("Tag: " + tag.getName());
                tag.getSubscribers().forEach(subscriber ->
                LOGGER.info("  Subscriber: " + subscriber.toString())
            );
        });
            */

            try (PrintStream cout = new PrintStream(System.out)){
                // Création des services
                IConfigReaderRepository configReader = new JsonConfigReaderNode();
                IOnboarding onboardingService = new OnboardingService(new ViewServer(cout));

                IServer server = ServerFactory.createServer(onboardingService, configReader);
                // Création du serveur + demarrage
                server.run();
            }
    }
}