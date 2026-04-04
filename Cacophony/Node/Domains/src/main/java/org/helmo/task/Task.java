package org.helmo.task;

/**
 * Représente une tâche à exécuter au sein du système.
 *
 * @param id      L'identifiant unique de la tâche.
 * @param type    Le type de la tâche.
 * @param sender  Le contexte de l'expéditeur permettant de répondre.
 * @param message Le contenu du message associé à la tâche.
 */
public record Task(String id, TypeTask type, TaskContext sender, String message) {

    /**
     * Envoie un message en utilisant le contexte de l'expéditeur.
     *
     * @param message Le message à envoyer.
     */
    public void send(String message) {
        sender.send(message);
    }
}
