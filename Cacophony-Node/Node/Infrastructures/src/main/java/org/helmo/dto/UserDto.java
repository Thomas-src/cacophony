package org.helmo.dto;

import java.util.List;

/**
 * Objet de transfert de données (DTO) représentant un utilisateur.
 *
 * @param login        L'identifiant unique de l'utilisateur.
 * @param bcryptHash   Le hash du mot de passe généré avec BCrypt.
 * @param bcryptRound  Le nombre d'itérations (coût) utilisé pour le hachage.
 * @param bcryptSalt   Le sel utilisé pour le hachage du mot de passe.
 * @param subscribers  La liste des identifiants des abonnés de l'utilisateur.
 * @param userTags     La liste des tags ou centres d'intérêt associés à l'utilisateur.
 */
public record UserDto(
        String login,
        String bcryptHash,
        int bcryptRound,
        String bcryptSalt,
        List<String> subscribers,
        List<String> userTags
) {}