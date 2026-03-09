package org.helmo.server;

import org.helmo.Config;
import org.helmo.Exception.ConfigReaderException;
import org.helmo.handler.CommandMenu;
import org.helmo.handler.OnboardingHandler;
import org.helmo.repository.IConfigReaderRepository;

public class ServerFactory {

    public static IServer createServer(CommandMenu menu, IConfigReaderRepository configReader) {
        try {
            Config config = configReader.getConfig();
            if (config.isTls()) {
                return new TLSServer(menu, configReader);
            } else {
                return new Server(menu, configReader);
            }
        }catch (ConfigReaderException e) {
            throw new RuntimeException(e);
        }
    }
}
