package org.helmo;

public class Tag implements Subscriber{
    String name;
    public Tag(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
