package org.helmo.dto;

import java.util.List;

/**
 * Objet de transfert de données représentant une liste de messages.
 *
 * @param messages La liste des messages textuels.
 */
public record MessagesDto(List<String> messages)  {

}
