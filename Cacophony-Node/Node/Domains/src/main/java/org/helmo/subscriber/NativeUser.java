package org.helmo.subscriber;

public class NativeUser implements IUserSubscriber {
    private final String fullName;

    public NativeUser(String fullName) {
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
        return true;
    }

}
