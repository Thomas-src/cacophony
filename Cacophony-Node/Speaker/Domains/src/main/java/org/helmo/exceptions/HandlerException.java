package org.helmo.exceptions;

import java.io.IOException;

public class HandlerException extends IOException {
    public HandlerException(String message) {
        super(message);
    }
}
