package org.helmo;

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
    public void subscribe(ISubscriber subscriber) {
        subscribers.add(subscriber);
    }
    @Override
    public boolean isSubscribed(ISubscriber subscriber) {
        return subscribers.contains(subscriber);
    }
    @Override
    public List<ISubscriber> getSubscribers() {
        return List.copyOf(subscribers);
    }
}