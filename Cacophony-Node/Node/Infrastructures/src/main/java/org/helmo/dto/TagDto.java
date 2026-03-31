package org.helmo.dto;

import java.util.List;

/**
 * Objet de transfert de données représentant un tag.
 *
 * @param tag         Le nom du tag.
 * @param subscribers La liste des identifiants des abonnés à ce tag.
 */
public record TagDto(
        String tag,
        List<String> subscribers
) {}