package org.helmo.Exception;

import java.io.IOException;

public class ServerException extends IOException {
    public ServerException(String message) {
        super(message);
    }
}
