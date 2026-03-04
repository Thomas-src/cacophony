package org.helmo;

import java.util.ArrayList;
import java.util.List;

public class Config {

    private final String currentDomain;
    private final int saltSizeInBytes;
    private final String multicastAddress;
    private final int multicastPort;
    private final int unicastPort;
    private final int relayPort;
    private final String networkInterface;
    private final String base64AES;
    private final boolean tls;

    private final List<User> users = new ArrayList<User>();
    private final List<Tag> tags = new ArrayList<Tag>();

    public Config(String currentDomain,
                  int saltSizeInBytes,
                  String multicastAddress,
                  int multicastPort,
                  int unicastPort,
                  int relayPort,
                  String networkInterface,
                  String base64AES,
                  boolean tls,
                  List<User> users,
                  List<Tag> tags) {

        this.currentDomain = currentDomain;
        this.saltSizeInBytes = saltSizeInBytes;
        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;
        this.unicastPort = unicastPort;
        this.relayPort = relayPort;
        this.networkInterface = networkInterface;
        this.base64AES = base64AES;
        this.tls = tls;
        if(users != null) {
            this.users.addAll(users);
        }
        if(tags != null) {
            this.tags.addAll(tags);
        }

    }

    public String getCurrentDomain() {
        return currentDomain;
    }

    public int getSaltSizeInBytes() {
        return saltSizeInBytes;
    }

    public String getMulticastAddress() {
        return multicastAddress;
    }

    public int getMulticastPort() {
        return multicastPort;
    }

    public int getUnicastPort() {
        return unicastPort;
    }

    public int getRelayPort() {
        return relayPort;
    }

    public String getNetworkInterface() {
        return networkInterface;
    }

    public String getBase64AES() {
        return base64AES;
    }

    public boolean isTls() {
        return tls;
    }

    /*
    Généré par IA
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n===== CONFIGURATION =====\n");
        sb.append("Domain           : ").append(currentDomain).append("\n");
        sb.append("Salt size        : ").append(saltSizeInBytes).append("\n");
        sb.append("Multicast addr   : ").append(multicastAddress).append("\n");
        sb.append("Multicast port   : ").append(multicastPort).append("\n");
        sb.append("Unicast port     : ").append(unicastPort).append("\n");
        sb.append("Relay port       : ").append(relayPort).append("\n");
        sb.append("Network interface: ").append(networkInterface).append("\n");
        sb.append("TLS              : ").append(tls).append("\n");

        sb.append("\n--- USERS ---\n");
        if (users.isEmpty()) {
            sb.append("No users configured\n");
        } else {
            for (User user : users) {
                sb.append("User: ").append(user.getName()).append("\n");

                if (user.getSubscribers().isEmpty()) {
                    sb.append("  Subscribers: none\n");
                } else {
                    sb.append("  Subscribers:\n");
                    for (ISubscriber s : user.getSubscribers()) {
                        sb.append("    - ").append(s.getName()).append("\n");
                    }
                }
            }
        }

        sb.append("\n--- TAGS ---\n");
        if (tags.isEmpty()) {
            sb.append("No tags configured\n");
        } else {
            for (Tag tag : tags) {
                sb.append("Tag: ").append(tag.getName()).append("\n");

                if (tag.getSubscribers().isEmpty()) {
                    sb.append("  Subscribers: none\n");
                } else {
                    sb.append("  Subscribers:\n");
                    for (ISubscriber s : tag.getSubscribers()) {
                        sb.append("    - ").append(s.getName()).append("\n");
                    }
                }
            }
        }

        sb.append("============================\n");

        return sb.toString();
    }
}