package org.helmo.exceptions;

import java.io.IOException;

public class ConfigReaderException extends IOException {
    public ConfigReaderException(String message) {
        super(message);
    }
}
