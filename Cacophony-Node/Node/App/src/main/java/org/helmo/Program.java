package org.helmo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.repository.ConfigReaderException;
import org.helmo.server.Server;
import org.helmo.services.IOnboarding;
import org.helmo.services.OnboardingService;
import org.helmo.views.IViewServer;

import java.io.PrintStream;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Program {
    private static final Logger LOGGER = LogManager.getLogger();


    public static void main(String[] args) throws ConfigReaderException {

        var config = new JsonConfigReaderNode();

        System.out.println(config.getConfig().getCurrentDomain());
        try (PrintStream cout = new PrintStream(System.out)){
            // Création des services
            IOnboarding onboardingService = new OnboardingService(new ViewServer(cout));

            // Création du serveur + demarrage
            Runnable server = new Server(onboardingService);
            server.run();
        }
    }
}