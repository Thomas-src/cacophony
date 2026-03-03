package org.helmo;

import org.helmo.views.IViewServer;

import java.io.PrintStream;

public class ViewServer implements IViewServer {

    PrintStream cout;
    public ViewServer(PrintStream cout) {
        this.cout = cout;
    }

    @Override
    public void showMessage(String message) {
            cout.println(message.trim());
    }
}
