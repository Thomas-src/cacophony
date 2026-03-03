package org.helmo.repository;


import org.helmo.Config;

public interface ConfigRepository {
    Config getConfig() throws ConfigReaderException;
}
