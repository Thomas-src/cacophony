package org.helmo;

/**
 * @param currentDomain todo transformer en record
 */
public record Config(String currentDomain, int saltSizeInBytes, String multicastAddress, int multicastPort,
                     int unicastPort, int relayPort, String networkInterface, String base64AES, boolean tls,
                     String certificateName, String certificatePassword) {

    /**
     * Généré par ia
     *
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