package org.helmo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.domain.DomainService;
import org.helmo.handler.AdvertHandler;
import org.helmo.handler.CommandMenu;
import org.helmo.handler.PostHandler;
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

            CryptoService crypto = new CryptoService();
            DomainService domainService = new DomainService(domainRepository, crypto, speakerConfig);

            CommandMenu tcpMenu = new CommandMenu();
            CommandMenu udpMenu = new CommandMenu();

            SpeakerTcpConnector connector = new SpeakerTcpConnector(crypto, tcpMenu);


            udpMenu.add(Message.P_ADVERT, new AdvertHandler(connector, domainService, crypto, speakerConfig));
            tcpMenu.add(Message.P_POST, new PostHandler(connector, crypto, domainService, domainRepository, speakerConfig));

            LOGGER.info(speakerConfig.toString());

            Speaker speaker = new Speaker(speakerConfig, udpMenu, connector);
            speaker.start();
        } catch (Exception e) {
            LOGGER.error("Erreur lors du démarrage du programme", e);
        }


    }
}
