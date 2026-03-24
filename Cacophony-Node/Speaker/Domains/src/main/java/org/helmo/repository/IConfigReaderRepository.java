package org.helmo.repository;

import org.helmo.SpeakerConfig;
import org.helmo.exceptions.ConfigReaderException;

public interface IConfigReaderRepository {
    SpeakerConfig getConfig() throws ConfigReaderException;
}
