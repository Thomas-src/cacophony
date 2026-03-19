package org.helmo.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Config;
import org.helmo.Exception.ConfigReaderException;
import org.helmo.Exception.ServerException;
import org.helmo.handler.CommandMenu;
import org.helmo.repository.IConfigReaderRepository;
import org.helmo.server.threads.TaskThread;


public class ServerFactory {
    private static final Logger LOGGER = LogManager.getLogger();

    public static IServer createServer(CommandMenu menu, IConfigReaderRepository configReader, TaskThread taskThread) throws ServerException {
        try {
            Config config = configReader.getConfig();
            if (config.isTls()) {
                return new SSLServer(menu, config, taskThread);
            } else {
                return new Server(menu, config, taskThread);
            }
        } catch (ConfigReaderException e) {
            LOGGER.error(e.getMessage());
            throw new ServerException(e.getMessage());
        }
    }
}
