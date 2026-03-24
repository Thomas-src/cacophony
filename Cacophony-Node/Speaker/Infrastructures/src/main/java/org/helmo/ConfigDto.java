package org.helmo;

import java.util.List;

/**
 * DTO de configuration pour le Speaker, utilisé pour sérialiser/désérialiser les paramètres de configuration.
 *
 * @param currentDomain
 * @param saltSizeInBytes
 * @param multicastAddress
 * @param multicastPort
 * @param unicastPort
 * @param relayPort
 * @param networkInterface
 * @param base64AES
 * @param tls
 */
public record ConfigDto(String currentDomain,
                        int saltSizeInBytes,
                        String multicastAddress,
                        int multicastPort,
                        int unicastPort,
                        int relayPort,
                        String networkInterface,
                        String base64AES,
                        boolean tls
) {
    // TODO: Ajouter une validation des champs de la configuration.
    // Par exemple, vérifier que les ports sont dans une plage valide, que les adresses ne sont pas nulles, etc.
    // Cette validation pourrait être faite dans un constructeur compact.
};
