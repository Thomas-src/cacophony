package org.helmo.mapper;

import org.helmo.SpeakerConfig;
import org.helmo.dto.ConfigDto;

/**
 * Mapper pour convertir les objets de configuration JSON en objets de domaine.
 */
public class JsonConfigMapper {
    /**
     * Convertit un ConfigDto en un objet SpeakerConfig.
     *
     * @param dto Le DTO contenant les données de configuration.
     * @return Une instance de SpeakerConfig, ou null si le DTO est null.
     */
    public static SpeakerConfig configfromDto(ConfigDto dto) {
        if (dto == null) {
            return null;
        }

        return new SpeakerConfig(
                dto.multicastAddress(),
                dto.multicastPort(),
                dto.networkInterface(),
                dto.aesKey()
        );
    }
}
