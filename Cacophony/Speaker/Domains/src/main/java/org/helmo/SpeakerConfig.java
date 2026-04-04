package org.helmo;

/**
 * Représente la configuration nécessaire pour le Speaker.
 *
 * @param multicastAdress  L'adresse de multidiffusion utilisée pour la communication.
 * @param multicastPort    Le port utilisé pour la multidiffusion.
 * @param networkInterface Le nom de l'interface réseau à utiliser.
 * @param aesKey           La clé de chiffrement AES pour sécuriser les échanges.
 */
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
