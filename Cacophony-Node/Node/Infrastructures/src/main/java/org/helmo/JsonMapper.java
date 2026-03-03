package org.helmo;

import java.util.Objects;

public class JsonMapper {

    public static Config fromDto(ConfigDto dto) {
        Objects.requireNonNull(dto);

        return new Config(
                dto.currentDomain(),
                dto.saltSizeInBytes(),
                dto.multicastAddress(),
                dto.multicastPort(),
                dto.unicastPort(),
                dto.relayPort(),
                dto.networkInterface(),
                dto.base64AES(),
                dto.tls()
        );
    }
}