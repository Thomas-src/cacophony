package org.helmo.server;

import org.helmo.Config;
import org.helmo.repository.ConfigReaderException;
import org.helmo.repository.IConfigReaderRepository;
import org.helmo.services.IOnboarding;

public class ServerFactory {

    public static IServer createServer(IOnboarding onboardingService, IConfigReaderRepository configReader) {
        try {
            Config config = configReader.getConfig();
            if (config.isTls()) {
                return new TLSServer(onboardingService, configReader);
            } else {
                return new Server(onboardingService, configReader);
            }
        }catch (ConfigReaderException e) {
            throw new RuntimeException(e);
        }
    }
}
