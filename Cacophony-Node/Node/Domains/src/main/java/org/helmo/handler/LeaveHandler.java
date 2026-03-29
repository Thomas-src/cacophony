package org.helmo.handler;

import org.helmo.Exception.HandlerException;
import org.helmo.MessageBuilder;
import org.helmo.task.ClientSession;
import org.helmo.task.TaskContext;

import java.util.List;


public class LeaveHandler implements CommandHandler {
    public String handle(List<String> args, TaskContext task) throws HandlerException {
        if (!(task instanceof ClientSession session)) {
            throw new HandlerException("Task leave nécessite une session client");
        }
        session.closeSession();

        return MessageBuilder.buildOk();
    }
}
