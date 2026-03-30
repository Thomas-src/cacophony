package org.helmo.handler.speaker;

import org.helmo.Exception.HandlerException;
import org.helmo.handler.CommandHandler;
import org.helmo.task.SpeakerTask;
import org.helmo.task.TaskContext;

import java.util.List;

public class PostHandler implements CommandHandler {


    @Override
    public String handle(List<String> args, TaskContext task) throws HandlerException {
        if (!(task instanceof SpeakerTask session)) {
            throw new HandlerException("Task post requires a speaker session");
        }

        return null;
    }
}
