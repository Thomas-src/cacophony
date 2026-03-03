package org.helmo.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class Server implements Runnable {
    private static final int PORT = 5002; // 130013 ?
    private boolean stop = false;
    private boolean isSarted = false;
    private boolean isConnected = false;
    ServerSocket listener;
    Socket client;
    PrintStream cout;

    public Server(PrintStream cout) {
        this.cout = cout;
    }

    @Override
    public void run() {
        try {
            listener = new ServerSocket(PORT);
            isSarted = true;
            cout.println("Server listening on port " + PORT);

            while (!stop) {

                client = listener.accept();
                isConnected = true;
                cout.println("Client connected : " + client.getInetAddress() + ":" + client.getPort());

                BufferedReader fromClient = new BufferedReader(
                        new InputStreamReader(client.getInputStream(), Charset.forName("UTF-8")));

                PrintWriter toClient = new PrintWriter(
                        new OutputStreamWriter(client.getOutputStream(), Charset.forName("UTF-8")), true);

                toClient.println("TODO"); // Il faut envoyer WELCOME + le reste

                String line;

                while ((line = fromClient.readLine()) != null) {

                    cout.println("Message reçu : " + line);

                    if (line.startsWith("ONBOARDING")) {
                        cout.println("Nouvelle inscription");
                    }
                }

                client.close();
                isConnected = false;
            }

        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

}