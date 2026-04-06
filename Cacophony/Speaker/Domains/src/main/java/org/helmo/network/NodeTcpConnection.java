package org.helmo.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.crypto.CryptoService;
import org.helmo.crypto.exceptions.DecryptionException;
import org.helmo.domain.Domain;
import org.helmo.exceptions.HandlerException;
import org.helmo.handler.CommandMenu;

/**
 * Représente une connexion TCP active avec un nœud distant.
 * Cette classe gère la lecture continue des messages entrants, leur déchiffrement et leur exécution.
 */
public class NodeTcpConnection extends Thread  {
    private static final Logger LOGGER = LogManager.getLogger();

    private final Domain domain;
    private final Socket socket;
    private final PrintWriter writer;
    private final CommandMenu tcpMenu;
    private final CryptoService cryptoService;
    private final ConnectionListener listener;

    /**
     * Initialise une nouvelle connexion TCP avec un domaine spécifique.
     *
     * @param domain Le domaine distant associé à cette connexion.
     * @param socket Le socket réseau ouvert.
     * @param tcpMenu Le menu de commandes pour traiter les messages reçus.
     * @param cryptoService Le service utilisé pour le déchiffrement.
     * @param connectionListener L'écouteur pour notifier la fermeture de la connexion.
     * @throws IOException Si une erreur survient lors de l'accès aux flux du socket.
     */
    public NodeTcpConnection(Domain domain, Socket socket, CommandMenu tcpMenu, CryptoService cryptoService, ConnectionListener connectionListener) throws IOException  {
        this.tcpMenu = tcpMenu;
        this.domain = domain;
        this.socket = socket;
        this.cryptoService = cryptoService;
        this.listener = connectionListener;
        this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
        setName("TCPConnection");
    }

    /**
     * Boucle principale d'écoute. Lit les messages entrants ligne par ligne,
     * les déchiffre et les transmet au menu de commandes.
     */
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
                    } catch (HandlerException e) {
                        LOGGER.info("Erreur exécution de la commande: {}",e.getMessage());
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


    /**
     * Envoie un message brut (généralement déjà chiffré) via le socket.
     *
     * @param line La chaîne de caractères à envoyer.
     */
    public void sendMessage(String line) {
        writer.println(line);
    }

    /**
     * Ferme proprement la connexion en interrompant le thread et en fermant le socket.
     */
    public void close() {
        interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.error("Erreur fermeture du socket {}", domain, e);
        }
    }

    /**
     * Récupère le nom du domaine associé à cette connexion.
     *
     * @return Le nom du domaine.
     */
    public String getDomain() {
        return domain.getName();
    }
}
