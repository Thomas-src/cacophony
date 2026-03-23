package org.helmo.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.Exception.HandlerException;
import org.helmo.MessageBuilder;
import org.helmo.client.ClientSession;
import org.helmo.server.ActiveSession;

import java.util.List;


public class LeaveHandler implements CommandHandler {
    public String handle(List<String> args, ClientSession session) {
        session.closeSession();

        return MessageBuilder.buildOk();
    }
}
