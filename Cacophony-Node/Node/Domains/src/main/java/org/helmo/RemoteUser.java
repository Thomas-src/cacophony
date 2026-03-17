package org.helmo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Représente un utilisateur ayant un compte sur un node
 */
public class RemoteUser implements ISubscriber {
    private final String name;
    private final String fullname;
    private final List<ISubscriber> subscribers = new ArrayList<>();


    public RemoteUser(String fullname) {
        this.fullname = fullname;
        this.name = fullname.split("@")[0];
    }
    @Override
    public String getName() {
        return name;
    }
    public String getFullName() {
        return fullname;
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

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RemoteUser {")
                .append("name='").append(name).append('\'')
                .append(", fullname='").append(fullname).append('\'');

        if (subscribers.isEmpty()) {
            sb.append(", subscribers=[]");
        } else {
            sb.append(", subscribers=[");
            for (int i = 0; i < subscribers.size(); i++) {
                sb.append(subscribers.get(i).getName());
                if (i < subscribers.size() - 1) sb.append(", ");
            }
            sb.append("]");
        }

        sb.append('}');
        return sb.toString();
    }
}