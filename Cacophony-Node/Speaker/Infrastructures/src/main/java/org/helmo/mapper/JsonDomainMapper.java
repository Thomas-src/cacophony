package org.helmo.mapper;

import org.helmo.domain.Domain;
import org.helmo.dto.DomainDto;
import org.helmo.dto.DomainsDto;

import java.util.List;

/**
 * Classe utilitaire pour mapper les objets DomainDto vers des objets Domain.
 * Permet la conversion des données de transfert (DTO) en objets de domaine.
 */
public class JsonDomainMapper {
    /**
     * Convertit un DomainDto en objet Domain.
     *
     * @param dto L'objet DomainDto à convertir
     * @return Un nouvel objet Domain créé à partir des données du DTO
     */
    public static Domain DomainFromDto(DomainDto dto) {
        return new Domain(
                dto.domain(),
                dto.base64AES()
        );
    }

    /**
     * Convertit un DomainDto en liste d'objets Domain.
     *
     * @param dtos La liste d'objets DomainDto à convertir
     * @return Une liste contenant un seul objet Domain créé à partir du DTO
     */
    public static List<Domain> mapToDomains(DomainsDto dtos) {
        List<DomainDto> domainsDtos = dtos == null ? List.of() : dtos.domains();
        return domainsDtos.stream().map(JsonDomainMapper::DomainFromDto).toList();


    }
}
