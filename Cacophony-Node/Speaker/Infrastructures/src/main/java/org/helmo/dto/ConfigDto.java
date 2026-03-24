package org.helmo.dto;

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
public record ConfigDto(
        String MulticastAdress,
        int MulticastPort,
        String NetworkInterface
) {

};
