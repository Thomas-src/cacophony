package org.helmo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.domain.DomainService;
import org.helmo.handler.AdvertHandler;
import org.helmo.handler.CommandMenu;
import org.helmo.network.SpeakerTcpConnector;
import org.helmo.crypto.CryptoService;

public class Program {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {

        // todo ajouter post msg
        try {
            JsonDomainRepository domainRepository = new JsonDomainRepository();
            JsonConfigReaderSpeaker configReader = new JsonConfigReaderSpeaker();
            SpeakerConfig speakerConfig = configReader.getConfig();

            CryptoService crypto = new CryptoService(speakerConfig.);
            DomainService domainService = new DomainService(domainRepository);

            SpeakerTcpConnector connector = new SpeakerTcpConnector(crypto, domainService);

            CommandMenu menu = new CommandMenu(crypto, connector);
            menu.add(Message.P_ADVERT, new AdvertHandler(connector,domainService));


            LOGGER.info(speakerConfig.toString());

            Speaker speaker = new Speaker(speakerConfig, menu);
            speaker.start();
        } catch (Exception e) {
            LOGGER.error("Erreur lors du démarrage du programme", e);
        }


    }
}
