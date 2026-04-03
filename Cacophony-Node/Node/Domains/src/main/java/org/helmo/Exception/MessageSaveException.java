package org.helmo.Exception;

import java.io.IOException;

public class MessageSaveException extends IOException {
    public MessageSaveException(String message) {
        super(message);
    }
}
