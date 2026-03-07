package org.helmo.client;

import org.helmo.ISubscriber;
import org.helmo.Tag;

import java.util.List;

public class Client {
    private String username;
    private BcryptPassword psw;
    private List<ISubscriber> subscribers;
    private List<Tag> tags;

    public Client() {
    }


    public static Client newClientFromOnboarding(String message) {
        if(message==null || !message.toUpperCase().startsWith("ONBOARDING")) {
            return null;
        }

        String[]  args = message.split(" ");
        Client actual =  new Client();
        actual.username = args[1];
        actual.psw = new BcryptPassword(args[3], Integer.parseInt(args[2]));

        return actual;
    }


    public String getName() {
        return username;
    }

    public String getFullPSW() {
        return psw.toString();
    }

    public String getHashPsw() {
        return psw.getHash();
    }

    public int getRotation() {
        return psw.getRotation();
    }

    public String getSalt() {
        return psw.getSalt();
    }
}
