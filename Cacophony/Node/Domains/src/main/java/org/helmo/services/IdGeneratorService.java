package org.helmo.services;

import org.helmo.Exception.ConfigSaveException;
import org.helmo.repository.IConfigRepository;

public class IdGeneratorService {
    private int count;
    private final IConfigRepository configRepository;

    public IdGeneratorService(int count, IConfigRepository configRepository) {
        this.count = count;
        this.configRepository = configRepository;
    }

    private int getNextCount() {
        count = count++;
        try {
            configRepository.saveId(count);
        } catch (ConfigSaveException ignored) {
        }
        return count++ % 99999; // pas une solution optimale mais meilleure approche avec le respect de la grammaire
    }

    public String getNextCountString() {
        return String.format("%05d", getNextCount());
    }

}
