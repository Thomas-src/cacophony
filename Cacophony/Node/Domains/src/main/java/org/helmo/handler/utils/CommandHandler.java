package org.helmo.handler.utils;

import org.helmo.Exception.HandlerException;
import org.helmo.task.TaskContext;

import java.util.List;

/**
 * Interface définissant un gestionnaire de commandes.
 */
public interface CommandHandler {
    /**
     * Traite une commande avec les arguments et le contexte fournis.
     *
     * @param args    La liste des arguments de la commande.
     * @param session Le contexte de la tâche actuelle.
     * @return Le résultat de l'exécution de la commande sous forme de chaîne de caractères.
     * @throws HandlerException Si une erreur survient lors du traitement de la commande.
     */
    String handle(List<String> args, TaskContext session) throws HandlerException;
}