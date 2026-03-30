package org.helmo.mapper;

import org.helmo.SpeakerConfig;
import org.helmo.dto.ConfigDto;

public class JsonConfigMapper {
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
