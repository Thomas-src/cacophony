package org.helmo.mapper;

import org.helmo.dto.ConfigDto;

public class JsonConfigMapper {
    public static ConfigDto ConfigfromDto(ConfigDto dto) {
        if (dto == null) {
            return null;
        }

        return new ConfigDto(
                dto.MulticastAdress(),
                dto.MulticastPort(),
                dto.NetworkInterface()
        );
    }
}
