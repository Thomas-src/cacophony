package org.helmo.repository;


import org.helmo.Config;

public interface IConfigReaderRepository {
    Config getConfig() throws ConfigReaderException;
}
