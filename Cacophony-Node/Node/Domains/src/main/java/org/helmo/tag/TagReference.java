package org.helmo.tag;

public class TagReference implements ITag{

    private final String fullName;
    public TagReference(String fullName) {
        this.fullName = fullName;
    }
    @Override
    public String getFullName() {
        return fullName;
    }

    @Override
    public boolean isInternal() {
        return false;
    }

}
