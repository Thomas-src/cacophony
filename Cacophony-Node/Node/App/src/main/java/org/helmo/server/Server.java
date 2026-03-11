package org.helmo.server;


import org.helmo.Config;
import org.helmo.Exception.ServerException;
import org.helmo.handler.CommandMenu;

import java.io.IOException;
import java.net.ServerSocket;

public class Server extends AbstractServer {
    public Server(CommandMenu menu, Config config) {
        super(menu, config);
    }

    @Override
    public ServerSocket createServerSocket(int port)  throws ServerException {
       try {
           return new ServerSocket(port);
       } catch (IOException e) {
           throw new ServerException(e.toString());
       }
    }
}