package org.helmo.server;


import org.helmo.Config;
import org.helmo.Exception.ServerException;
import org.helmo.server.threads.TaskThread;

import java.io.IOException;
import java.net.ServerSocket;

public class Server extends AbstractServer {
    public Server(Config config, TaskThread taskThread, ActiveSession activeSession) {
        super(config, taskThread, activeSession);
    }

    @Override
    public ServerSocket createServerSocket(int port) throws ServerException {
        try {
            return new ServerSocket(port);
        } catch (IOException e) {
            throw new ServerException(e.toString());
        }
    }
}