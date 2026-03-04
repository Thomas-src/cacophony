package org.helmo;

import java.util.List;

public interface ISubscriber {
    String getName();
    void subscribe(ISubscriber subscriber);
    boolean isSubscribed(ISubscriber subscriber);
     List<ISubscriber> getSubscribers();
}
