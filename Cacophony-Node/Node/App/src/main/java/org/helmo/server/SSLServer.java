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
 * Serveur SSL/TLS sécurisé utilisant le protocole TLSv1.3 et le chiffrement AES-256 GCM.
 *
 * Cette classe implémente un serveur capable d'établir des connexions sécurisées
 * en utilisant des certificats SSL/TLS. Elle gère automatiquement la localisation
 * du fichier de certificat (keystore) et configure les paramètres de sécurité
 * appropriés pour le socket serveur.
 *
 * Le serveur utilise exclusivement TLSv1.3 avec la suite de chiffrement TLS_AES_256_GCM_SHA384
 * pour garantir une sécurité maximale des communications.
 *<br>
 * Code inspiré de <a href="https://stackoverflow.com/questions/18787419/ssl-socket-connection">Stackoverflow</a>, consulté le 09/03/26
 */
public class SSLServer extends AbstractServer {

    private final Config config;

    /**
     * Construit un nouveau serveur SSL/TLS avec la configuration et les composants spécifiés.
     *
     * @param config        la configuration contenant les paramètres du certificat et du serveur
     * @param taskThread    le gestionnaire de threads pour l'exécution des tâches
     * @param activeSession le gestionnaire de sessions actives
     */
    public SSLServer(Config config, TaskThread taskThread, ActiveSession activeSession) {
        super(config, taskThread, activeSession);
        this.config = config;
    }

    /**
     * Localise le fichier keystore contenant le certificat SSL/TLS.
     * <p>
     * Cette méthode recherche le fichier de certificat dans plusieurs emplacements
     * pour supporter différents contextes d'exécution :
     * 1. Depuis le sous-répertoire 'Node' (ex: tâche 'run' de Gradle)
     * 2. Depuis la racine du projet (ex: exécution depuis l'IDE)
     * 3. Chemin par défaut en dernier recours
     *
     * @return le chemin vers le fichier keystore trouvé ou le chemin par défaut
     */
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

    /**
     * Crée un socket serveur SSL/TLS sécurisé sur le port spécifié.
     * <p>
     * Cette méthode initialise un SSLServerSocket avec les paramètres de sécurité suivants :
     * - Protocole : TLSv1.3 uniquement
     * - Suite de chiffrement : TLS_AES_256_GCM_SHA384
     * <p>
     * Le socket est configuré avec les gestionnaires de clés chargés depuis le keystore.
     *
     * @param port le numéro de port sur lequel le serveur doit écouter
     * @return un ServerSocket SSL/TLS configuré et prêt à accepter des connexions
     * @throws ServerException si une erreur se produit lors de la création ou de la configuration du socket
     */
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

    /**
     * Crée et initialise le KeyManagerFactory à partir du keystore PKCS12.
     * <p>
     * Cette méthode charge le certificat depuis le keystore en utilisant le mot de passe
     * configuré, puis initialise un KeyManagerFactory de type SunX509 avec ce keystore.
     *
     * @return le KeyManagerFactory initialisé contenant les clés et certificats
     * @throws KeyStoreException         si le type de keystore n'est pas supporté
     * @throws IOException               si une erreur d'entrée/sortie se produit lors de la lecture du keystore
     * @throws NoSuchAlgorithmException  si l'algorithme spécifié n'est pas disponible
     * @throws CertificateException      si un problème survient lors du chargement des certificats
     * @throws UnrecoverableKeyException si la clé ne peut pas être récupérée du keystore
     */
    private KeyManagerFactory createKey() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
        String keyStorePath = findKeyStoreFile();
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(new FileInputStream(keyStorePath), config.certificatePassword().toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, config.certificatePassword().toCharArray());
        return kmf;
    }
}
