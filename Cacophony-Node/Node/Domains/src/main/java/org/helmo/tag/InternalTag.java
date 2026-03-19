package org.helmo.tag;

import org.helmo.subscriber.IUserSubscriber;

import java.util.ArrayList;
import java.util.List;

public class InternalTag implements ITag {

    private final String fullName;
    private final List<IUserSubscriber> subscribers = new ArrayList<>();

    public InternalTag(String fullName, List<IUserSubscriber> subscribers) {
        this.fullName = fullName;
        if(subscribers != null){
            this.subscribers.addAll(subscribers);
        }
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    public List<IUserSubscriber> getSubscribers() {
        return subscribers;
    }
}