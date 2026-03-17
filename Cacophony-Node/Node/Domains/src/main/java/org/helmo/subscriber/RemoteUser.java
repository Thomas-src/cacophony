package org.helmo.subscriber;

public class RemoteUser implements IUserSubscriber {
    private final String fullName;

    public RemoteUser(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public String getShortName() {
        return fullName;
    }

    @Override
    public boolean isLocal() {
        return false;
    }

}
