package org.helmo.services;

public class IdGeneratorService {
    private int count;

    public IdGeneratorService(int count) {
        this.count = count;
    }

    private int getNextCount() {
        return count++ %99999; // pas une solution optimale mais meilleure approche avec le respect de la grammaire
    }

    public String getNextCountString() {
        return String.format("%05d", getNextCount());
    }

}
