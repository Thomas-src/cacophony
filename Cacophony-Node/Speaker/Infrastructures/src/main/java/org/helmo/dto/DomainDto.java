package org.helmo.dto;

import java.util.List;

/**
 * Objet de Transfert de Données (DTO) représentant la configuration d'un domaine.
 * Cet enregistrement est utilisé pour transporter les données liées aux domaines,
 * particulièrement dans les scénarios impliquant la sérialisation et la désérialisation.
 *
 * @param domain    L'identifiant unique ou le nom du domaine.
 * @param base64AES La chaîne encodée en AES (Base64) associée au domaine,
 *                  couramment utilisée pour les opérations cryptographiques sécurisées.
 */
public record DomainDto(
        String domain,
        String base64AES
) {
}
