package org.helmo.handler;

import org.helmo.exceptions.HandlerException;

import java.util.List;

public class AdvertHandler implements CommandHandler{
    @Override
    public String handle(List<String> args) throws HandlerException {
        return "ERR";
    }
}
