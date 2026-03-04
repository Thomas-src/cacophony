package org.helmo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RemoteUser implements ISubscriber {
    private final String name;
    private final List<ISubscriber> subscribers = new ArrayList<>();


    public RemoteUser(String name) {
        this.name = name;
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
        Objects.requireNonNull(subscriber);
        return subscribers.contains(subscriber);
    }

    @Override
    public List<ISubscriber> getSubscribers() {
        return List.copyOf(subscribers);
    }
}