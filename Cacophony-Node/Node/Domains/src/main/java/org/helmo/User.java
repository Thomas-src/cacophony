package org.helmo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User implements Subscriber {
    private final String name;
    private final HashPassword hashPassword;

    private final List<Subscriber> subscribers = new ArrayList<Subscriber>();

    public User(String name, HashPassword hashPassword, List<Subscriber> subscribers) {
        this.name = name;
        this.hashPassword = hashPassword;
        if(subscribers != null) {
            this.subscribers.addAll(subscribers);
        }
    }
    public String getName() {
        return name;
    }

    public boolean isValidChallenge(String challenge) {
        return hashPassword.checkPassword(challenge);
    }

    public boolean isSubscribed(Subscriber subscriber) {
        Objects.requireNonNull(subscriber);
        return subscribers.contains(subscriber);
    }

    public void subscribe(Subscriber subscriber) {
        Objects.requireNonNull(subscriber);
        subscribers.add(subscriber);
    }
    public void unsubscribe(Subscriber subscriber) {
        Objects.requireNonNull(subscriber);
        subscribers.remove(subscriber);
    }

}
