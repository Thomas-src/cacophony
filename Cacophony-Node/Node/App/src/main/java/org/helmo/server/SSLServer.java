package org.helmo.server;

import org.helmo.Config;
import org.helmo.Exception.ServerException;
import org.helmo.server.threads.TaskThread;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * Code inspiré de <a href="https://stackoverflow.com/questions/18787419/ssl-socket-connection">Stackoverflow</a>, consulté le 09/03/26
 */
public class SSLServer extends AbstractServer {

    private final Config config;

    public SSLServer(Config config, TaskThread taskThread, ActiveSession activeSession) {
        super(config, taskThread, activeSession);
        this.config = config;
    }

    private  String findKeyStoreFile() {
        String fileName = config.certificateName();

        // Case 1: Running from 'Node' subdirectory (e.g., Gradle's 'run' task)
        Path pathFromNodeDir = Paths.get("..", fileName);
        if (Files.exists(pathFromNodeDir)) {
            return pathFromNodeDir.toString();
        }

        // Case 2: Running from project root (e.g., IDE)
        Path pathFromRoot = Paths.get(fileName);
        if (Files.exists(pathFromRoot)) {
            return pathFromRoot.toString();
        }

        // Fallback to original path as a last resort
        return "../"+fileName;
    }

    @Override
    public ServerSocket createServerSocket(int port) throws ServerException {
        try {
            KeyManagerFactory kmf = createKey();
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

    private KeyManagerFactory createKey() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
        String keyStorePath = findKeyStoreFile();
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new FileInputStream(keyStorePath), config.certificatePassword().toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, config.certificatePassword().toCharArray());
        return kmf;
    }
}
