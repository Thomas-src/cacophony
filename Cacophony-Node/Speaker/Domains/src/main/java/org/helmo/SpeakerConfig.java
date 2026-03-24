package org.helmo;

public record SpeakerConfig(String multicastAdress, int multicastPort, String networkInterface) {

    @Override
    public String toString() {
        String sb = "\n===== CONFIGURATION =====\n" +
                "Multicast addr   : " + multicastAdress + "\n" +
                "Multicast port   : " + multicastPort + "\n" +
                "Network interface: " + networkInterface + "\n" +
                "============================\n";

        return sb;
    }
}
