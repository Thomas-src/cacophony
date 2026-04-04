package org.helmo.dto;

/**
 * Objet de transfert de données représentant la configuration du nœud.
 *
 * @param currentDomain      Le domaine actuel du nœud.
 * @param saltSizeInBytes    La taille du sel en octets pour le hachage.
 * @param multicastAddress   L'adresse IP utilisée pour le multicast.
 * @param multicastPort      Le port utilisé pour les communications multicast.
 * @param unicastPort        Le port utilisé pour les communications unicast.
 * @param relayPort          Le port utilisé pour le relais.
 * @param networkInterface   Le nom de l'interface réseau à utiliser.
 * @param base64AES          La clé AES encodée en Base64.
 * @param tls                Indique si TLS est activé.
 * @param certificateName    Le nom du fichier de certificat.
 * @param certificatePassword Le mot de passe du certificat.
 */
public record ConfigDto(String currentDomain,
                        int saltSizeInBytes,
                        String multicastAddress,
                        int multicastPort,
                        int unicastPort,
                        int relayPort,
                        String networkInterface,
                        String base64AES,
                        boolean tls,
                        String certificateName,
                        String certificatePassword,
                        int id
                        ) {
};
