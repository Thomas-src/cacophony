package org.helmo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Config;
import org.helmo.Exception.ConfigReaderException;
import org.helmo.Exception.ServerException;
import org.helmo.repository.IConfigReaderRepository;
import org.helmo.server.threads.TaskThread;


/**
 * Fabrique pour la création d'instances de serveur.
 * <p>
 * Cette classe fournit une méthode de fabrique pour créer des instances de serveur
 * appropriées (standard ou SSL/TLS) en fonction de la configuration fournie.
 * Elle utilise le patron de conception Factory pour encapsuler la logique de création
 * des serveurs.
 */
public class ServerFactory {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Crée une instance de serveur en fonction de la configuration fournie.
     * <p>
     * Cette méthode lit la configuration et instancie soit un serveur SSL/TLS
     * soit un serveur standard en fonction du paramètre TLS de la configuration.
     *
     * @param configReader  le repository de lecture de configuration utilisé pour récupérer la configuration
     * @param taskThread    le thread de tâches qui gérera les opérations asynchrones du serveur
     * @param activeSession la session active pour gérer les connexions actives
     * @return une instance de IServer (SSLServer ou Server) selon la configuration
     * @throws ServerException si une erreur se produit lors de la lecture de la configuration ou de la création du serveur
     */
    public static IServer createServer(IConfigReaderRepository configReader, TaskThread taskThread, ActiveSession activeSession) throws ServerException {
        try {
            Config config = configReader.getConfig();
            if (config.tls()) {
                return new SSLServer(config, taskThread, activeSession);
            } else {
                return new Server(config, taskThread, activeSession);
            }
        } catch (ConfigReaderException e) {
            LOGGER.error(e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }
}
