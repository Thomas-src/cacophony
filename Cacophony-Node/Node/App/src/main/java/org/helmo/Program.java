package org.helmo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.ConfigReaderException;
import org.helmo.Exception.ServerException;
import org.helmo.handler.*;
import org.helmo.repository.IConfigReaderRepository;
import org.helmo.repository.IUserRepository;
import org.helmo.server.IServer;
import org.helmo.server.ServerFactory;

import java.io.PrintStream;

public class Program {
    private static final Logger LOGGER = LogManager.getLogger();
    public static void main(String[] args) {

            try (PrintStream cout = new PrintStream(System.out)){
                // Création des services
                IConfigReaderRepository configReader = new JsonConfigReaderNode();
                IUserRepository userRepository = new JsonUserRepository(configReader.getConfig());

                CommandMenu menu = new CommandMenu();
                menu.add("ONBOARDING", new OnboardingHandler(userRepository));
                menu.add("USER", new AuthentificateHandler(userRepository));
                menu.add("RESPONSE", new ResponseChallengeHandler());
                menu.add("SEND", new MessageHandler());

                // Création du serveur + demarrage
                IServer server = ServerFactory.createServer(menu, configReader);
                server.run();
            } catch (ConfigReaderException | ServerException e) {
                LOGGER.error("Le serveur n'a pas pu démarrer",e);
            }
    }
}