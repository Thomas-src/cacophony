package org.helmo.network;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.crypto.CryptoService;
import org.helmo.crypto.exceptions.DecryptionException;
import org.helmo.domain.Domain;
import org.helmo.handler.CommandMenu;

public class NodeTCPConnection extends Thread  {
    private static final Logger LOGGER = LogManager.getLogger();

    private final Domain domain;
    private final Socket socket;
    private final PrintWriter writer;
    private final CommandMenu tcpMenu;
    private final CryptoService cryptoService;
    private final ConnectionListener listener;

    public NodeTCPConnection(Domain domain, Socket socket, CommandMenu tcpMenu, CryptoService cryptoService, ConnectionListener connectionListener) throws IOException  {
        this.tcpMenu = tcpMenu;
        this.domain = domain;
        this.socket = socket;
        this.cryptoService = cryptoService;
        this.listener = connectionListener;
        this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        setName("TCPConnection");
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    try {
                        LOGGER.info("[TCP:{}] reçu (chiffré) : {}", domain.getName(), line);
                        String decrypted = cryptoService.decrypt(line, domain.getAesKey());
                        LOGGER.info("[TCP:{}] reçu (déchiffré) : {}", domain.getName(), decrypted);
                        tcpMenu.execute(decrypted);
                    } catch (DecryptionException e) {
                        LOGGER.info("Erreur décryption du message reçu: {}, {}",line, e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error(e);
            LOGGER.info("[TCP:{}] connexion fermée", domain.getName());
        } finally {
            listener.onConnectionClosed(domain.getName());
        }
    }



    public void sendMessage(String line) {
        writer.println(line);
    }

    public void close() {
        interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.error("Erreur fermeture du socket {}", domain, e);
        }
    }

    public String getDomain() {
        return domain.getName();
    }
}
