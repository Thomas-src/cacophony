package org.helmo.dto;

/**
 * DTO de configuration pour le Speaker, utilisé pour sérialiser/désérialiser les paramètres de configuration.
 *
 */
public record ConfigDto(
        String multicastAddress,
        int multicastPort,
        String networkInterface
) {

};
