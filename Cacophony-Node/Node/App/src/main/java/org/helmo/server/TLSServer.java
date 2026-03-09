package org.helmo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.handler.CommandMenu;
import org.helmo.handler.OnboardingHandler;
import org.helmo.repository.IConfigReaderRepository;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TLSServer implements IServer {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IConfigReaderRepository configReader;

    // Services
    private final CommandMenu menu;
    private boolean stop = false;
    private boolean isStarted = false;
    private boolean isConnected = false;
    ServerSocket listener;
    Socket client;

    public TLSServer(CommandMenu menu, IConfigReaderRepository configReader) {
        this.menu = menu;
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
