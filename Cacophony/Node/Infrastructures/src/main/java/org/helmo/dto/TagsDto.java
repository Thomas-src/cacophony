package org.helmo.dto;

import java.util.List;

/**
 * Représente un objet de transfert de données contenant une liste de tags.
 *
 * @param tags La liste des objets {@link TagDto}.
 */
public record TagsDto(List<TagDto> tags) {
}
