package org.helmo;

public class Config {
// todo transformer en record
    private final String currentDomain;
    private final int saltSizeInBytes;
    private final String multicastAddress;
    private final int multicastPort;
    private final int unicastPort;
    private final int relayPort;
    private final String networkInterface;
    private final String base64AES;
    private final boolean tls;
    private final String certificateName;
    private final String certificatePassword;

    public Config(String currentDomain,
                  int saltSizeInBytes,
                  String multicastAddress,
                  int multicastPort,
                  int unicastPort,
                  int relayPort,
                  String networkInterface,
                  String base64AES,
                  boolean tls,
                  String certificateName,
                  String certificatePassword) {

        this.currentDomain = currentDomain;
        this.saltSizeInBytes = saltSizeInBytes;
        this.multicastAddress = multicastAddress;
        this.multicastPort = multicastPort;
        this.unicastPort = unicastPort;
        this.relayPort = relayPort;
        this.networkInterface = networkInterface;
        this.base64AES = base64AES;
        this.tls = tls;
        this.certificateName = certificateName;
        this.certificatePassword = certificatePassword;

    }

    public String getCertificateName() {
        return certificateName;
    }
    public String getCertificatePassword() {
        return certificatePassword;
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

    /**
     * Généré par ia
     * @return
     */
    @Override
    public String toString() {

        String sb = "\n===== CONFIGURATION =====\n" +
                "Domain           : " + currentDomain + "\n" +
                "Salt size        : " + saltSizeInBytes + "\n" +
                "Multicast addr   : " + multicastAddress + "\n" +
                "Multicast port   : " + multicastPort + "\n" +
                "Unicast port     : " + unicastPort + "\n" +
                "Relay port       : " + relayPort + "\n" +
                "Network interface: " + networkInterface + "\n" +
                "TLS              : " + tls + "\n" +
                "============================\n";

        return sb;
    }
}