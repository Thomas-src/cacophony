package org.helmo;

import org.helmo.Exception.ConfigReaderException;
import org.helmo.handler.AuthentificateHandler;
import org.helmo.handler.CommandMenu;
import org.helmo.repository.IConfigReaderRepository;
import org.helmo.repository.IUserRepository;
import org.helmo.server.IServer;
import org.helmo.server.ServerFactory;
import org.helmo.handler.OnboardingHandler;

import java.io.PrintStream;

public class Program {

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
                IUserRepository userRepository = new JsonUserRepository(configReader.getConfig());

                CommandMenu menu = new CommandMenu();
                menu.add("ONBOARDING", new OnboardingHandler(userRepository));
                menu.add("USER", new AuthentificateHandler(userRepository));

                // Création du serveur + demarrage
                IServer server = ServerFactory.createServer(menu, configReader);
                server.run();
            } catch (ConfigReaderException e) {
                throw new RuntimeException(e);
            }
    }
}