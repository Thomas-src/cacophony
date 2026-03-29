package org.helmo.handler;

import org.helmo.Exception.HandlerException;
import org.helmo.task.TaskContext;

import java.util.List;


public interface CommandHandler {
    String handle(List<String> args, TaskContext session) throws HandlerException;
}