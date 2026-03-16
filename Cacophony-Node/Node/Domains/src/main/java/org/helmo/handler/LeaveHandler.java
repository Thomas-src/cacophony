package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.client.ClientSession;


public class LeaveHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();



    public String handle(String message, ClientSession session) {
        // Le CommandMenu garantit que ce handler n'est appelé que pour la commande LEAVE.
        // 1. On met le flag à true pour signaler la fermeture prochaine.
        session.setShouldClose(true);
        // 2. On prépare un message de confirmation pour le client.
        LOGGER.info("Client leaving");
        return "OK";

    }
}
