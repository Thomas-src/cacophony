package org.helmo.repository;


import org.helmo.Config;
import org.helmo.Exception.ConfigReaderException;

public interface IConfigReaderRepository {
    Config getConfig() throws ConfigReaderException;
}
