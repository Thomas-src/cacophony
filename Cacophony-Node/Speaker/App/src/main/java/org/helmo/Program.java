package org.helmo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.domain.Domain;
import org.helmo.handler.AdvertHandler;
import org.helmo.handler.CommandMenu;
import org.helmo.network.SpeakerTcpConnector;
import org.helmo.security.CryptoService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Program {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void main(String[] args) {

        // todo ajouter post msg
        try {
            JsonDomainRepository domainRepository = new JsonDomainRepository();

            List<Domain> domainList = domainRepository.getDomains();
            LOGGER.info("{} domaines", domainList.size());
            Map<String, Domain> domainMap = domainList.stream().collect(Collectors.toMap(Domain::getName, d -> d));


            JsonConfigReaderSpeaker configReader = new JsonConfigReaderSpeaker();
            CryptoService crypto = new CryptoService();
            SpeakerTcpConnector connector = new SpeakerTcpConnector(crypto);

            CommandMenu menu =  new CommandMenu();
            menu.add(Message.P_ADVERT, new AdvertHandler(connector,domainMap));

            SpeakerConfig speakerConfig = configReader.getConfig();
            LOGGER.info(speakerConfig.toString());

            Speaker speaker =  new Speaker(speakerConfig,menu);
            speaker.start();
        } catch (Exception e) {
            LOGGER.error("Erreur lors du démarrage du programme", e);
        }



    }
}
