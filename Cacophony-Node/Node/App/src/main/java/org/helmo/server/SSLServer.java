package org.helmo.server;

import org.helmo.Config;
import org.helmo.Exception.ServerException;
import org.helmo.handler.CommandMenu;
import org.helmo.server.threads.TaskThread;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.security.KeyStore;

/**
 * Code inspiré de <a href="https://stackoverflow.com/questions/18787419/ssl-socket-connection">Stackoverflow</a>, consulté le 09/03/26
 */
public class SSLServer extends AbstractServer {

    public SSLServer(CommandMenu menu, Config config, TaskThread taskThread) {
        super(menu, config,  taskThread);
    }

    @Override
    public ServerSocket createServerSocket(int port) throws ServerException {
        try {
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(new FileInputStream("Node/star.swilabus.xyz.p12"), "labo2026".toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, "labo2026".toCharArray());
            SSLContext sc = SSLContext.getInstance("TLSv1.3");
            sc.init(kmf.getKeyManagers(), null, null);

            SSLServerSocketFactory ssf = sc.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) ssf.createServerSocket(port);

            serverSocket.setEnabledProtocols(new String[]{"TLSv1.3"});
            serverSocket.setEnabledCipherSuites(new String[]{"TLS_AES_256_GCM_SHA384"});

            return serverSocket;

        } catch (Exception e) {
            throw new ServerException(e.getMessage());
        }
    }
}