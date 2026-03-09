package org.helmo.server;

import org.helmo.Config;
import org.helmo.handler.CommandMenu;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;

public class SSLServer extends AbstractServer {

    public SSLServer(CommandMenu menu, Config config) {
        super(menu, config);
    }

    @Override
    public ServerSocket createServerSocket(int port) throws IOException {
        System.setProperty("javax.net.ssl.keyStore", "Node/star.swilabus.xyz.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "labo2026");
        System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");

        SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        return factory.createServerSocket(port);
    }
}
