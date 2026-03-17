package org.helmo.client;

import org.helmo.subscriber.IUserSubscriber;

import java.util.ArrayList;
import java.util.List;

public class Tag {

    private final String name;
    private final List<IUserSubscriber> subscribers = new ArrayList<>();

    public Tag(String name, List<IUserSubscriber> subscribers) {
        this.name = name;
        if(subscribers != null){
            this.subscribers.addAll(subscribers);
        }
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return name;
    }

}