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
};
