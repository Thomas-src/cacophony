package org.helmo.client;

import org.helmo.ISubscriber;

import java.util.ArrayList;
import java.util.List;

public class Tag implements ISubscriber {

    private final String name;
    private final List<ISubscriber> subscribers = new ArrayList<>();

    public Tag(String name, List<ISubscriber> subscribers) {
        this.name = name;
        if(subscribers != null){
            this.subscribers.addAll(subscribers);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFullName() {
        return name;
    }

}