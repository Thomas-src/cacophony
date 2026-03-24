package org.helmo;

import org.helmo.handler.AdvertHandler;
import org.helmo.handler.CommandMenu;
import org.helmo.repository.IConfigReaderRepository;

public class Program {
    public static void main(String[] args) {

        CommandMenu menu =  new CommandMenu();
        menu.add(Message.P_ADVERT, new AdvertHandler());

        try {
            // charger config
            // init le  serveur => attend une connexion
            SpeakerConfig speakerConfig = IConfigReaderRepository;
            Speaker speaker =  new Speaker(speakerConfig,menu);
            speaker.start();
        } catch (Exception e) {

        }



    }
}
