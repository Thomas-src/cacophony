package org.helmo.mapper;

import org.helmo.Config;
import org.helmo.dto.*;
import java.util.Objects;

/**
 * Mapper responsable de la conversion entre les objets de configuration JSON et les objets du domaine.
 */
public class JsonConfigMapper {
    /**
     * Valide l'intégrité du DTO de configuration.
     *
     * @param dto Le DTO à valider.
     * @throws NullPointerException si le dto est nul.
     */
    private static void validate(ConfigDto dto) {
        Objects.requireNonNull(dto, "ConfigDto cannot be null");
        // si besoin de vérifier d'autres éléments
    }

    /**
     * Convertit un {@link ConfigDto} en un objet {@link Config}.
     *
     * @param dto Le DTO source.
     * @return Une nouvelle instance de Config.
     */
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
                dto.certificatePassword(),
                dto.id()
        );
    }
    public static ConfigDto duplicate(ConfigDto dto, int currentId) {
        return new ConfigDto(
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
                dto.certificatePassword(),
                currentId
        );

    }
}