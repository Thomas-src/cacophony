package org.helmo;

public record SpeakerConfig(String multicastAdress, int multicastPort, String networkInterface, String aesKey) {

    @Override
    public String toString() {
        String sb = "\n===== CONFIGURATION =====\n" +
                "Multicast addr   : " + multicastAdress + "\n" +
                "Multicast port   : " + multicastPort + "\n" +
                "Network interface: " + networkInterface + "\n" +
                "Aeskey: " + aesKey + "\n" +
                "============================\n";

        return sb;
    }
}
