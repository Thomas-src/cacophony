package org.helmo.dto;

import java.util.List;

/**
 * Objet de transfert de données représentant une liste d'utilisateurs.
 *
 * @param users La liste des objets {@link UserDto}.
 */
public record UsersDto(List<UserDto> users) {
}
