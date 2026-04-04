package org.helmo.dto;

import java.util.List;

/**
 * Objet de transfert de données représentant une liste de domaines.
 *
 * @param domains La liste des objets {@link DomainDto}.
 */
public record DomainsDto(List<DomainDto> domains) {
}