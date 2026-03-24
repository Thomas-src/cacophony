package org.helmo.exceptions;

public class ConfigReaderException extends Exception {
    public ConfigReaderException(String message) {
        super(message);
    }

    public ConfigReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigReaderException(Throwable cause) {
        super(cause);
    }

    public ConfigReaderException() {
        	super("Error reading configuration");
    }
}
