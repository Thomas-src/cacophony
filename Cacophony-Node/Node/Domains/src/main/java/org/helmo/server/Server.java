package org.helmo.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 5002; // 130013 ?
    private boolean stop = false;
    private boolean isSarted = false;

    public Server(PrintStream cout) {
        ServerSocket listener = null;
        Socket client = null; // List de client ?
        try {
            listener = new ServerSocket(PORT);
            isSarted = true;
            cout.println("Server listening on port " + PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}
