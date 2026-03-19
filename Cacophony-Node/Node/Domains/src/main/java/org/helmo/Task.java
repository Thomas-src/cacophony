package org.helmo;

import org.helmo.client.ClientSession;
import org.helmo.server.threads.WestThread;

public class Task {
    private final ClientSession session;
    private final String rawLine;
    private final WestThread westThread;

    public Task(ClientSession session, String rawLine, WestThread westThread) {
        this.session = session;
        this.rawLine = rawLine;
        this.westThread = westThread;
    }

    public ClientSession getSession() {
        return session;
    }

    public String getRawLine() {
        return rawLine;
    }

    public WestThread getWestThread() {
        return westThread;
    }
}
