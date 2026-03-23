package org.helmo.mapper;

import org.helmo.Config;
import org.helmo.dto.*;
import java.util.Objects;

public class JsonConfigMapper {
    private static void validate(ConfigDto dto) {
        Objects.requireNonNull(dto, "ConfigDto cannot be null");
        // todo si besoin de vérifier d'autres éléments
    }

    public static Config ConfigfromDto(ConfigDto dto) {
        validate(dto);

        return new Config(
                dto.currentDomain(),
                dto.saltSizeInBytes(),
                dto.multicastAddress(),
                dto.multicastPort(),
                dto.unicastPort(),
                dto.relayPort(),
                dto.networkInterface(),
                dto.base64AES(),
                dto.tls(),
                dto.certificateName(),
                dto.certificatePassword()
        );
    }
}