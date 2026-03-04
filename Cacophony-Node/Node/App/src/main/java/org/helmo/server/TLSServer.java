package org.helmo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.repository.IConfigReaderRepository;
import org.helmo.services.IOnboarding;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TLSServer implements IServer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IConfigReaderRepository configReader;

    // Services
    private final IOnboarding onboardingService;
    private boolean stop = false;
    private boolean isStarted = false;
    private boolean isConnected = false;
    ServerSocket listener;
    Socket client;

    public TLSServer(IOnboarding onboardingService, IConfigReaderRepository configReader) {
        this.onboardingService = onboardingService;
        this.configReader = configReader;
    }

    @Override
    public void run() {

    }

    @Override
    public void stopServer() {
        stop = true;
        try {
            if (listener != null) {
                listener.close();
            }
        } catch (IOException ex) {
            LOGGER.error("Une erreur s'est produite lors de l'arrêt du serveur",ex);
        }
    }
}
