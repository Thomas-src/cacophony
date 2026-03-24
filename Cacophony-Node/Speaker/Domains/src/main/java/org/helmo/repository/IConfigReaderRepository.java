package org.helmo.repository;


import org.helmo.Config;
import org.helmo.exceptions.ConfigReaderException;

public interface IConfigReaderRepository {
    Config getConfig() throws ConfigReaderException;
}
