package org.helmo.task;

/**
 * Représente le contexte d'exécution d'une tâche.
 */
public interface TaskContext {
    /**
     * Récupère le nom du domaine associé à ce contexte.
     *
     * @return Le nom du domaine.
     */
    String getDomain();

    /**
     * Envoie un message dans le cadre de ce contexte.
     *
     * @param message Le contenu du message à envoyer.
     */
    void send(String message);
}
