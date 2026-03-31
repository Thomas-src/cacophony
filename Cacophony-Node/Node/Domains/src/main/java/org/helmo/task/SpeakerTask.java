package org.helmo.task;

/**
 * Représente une tâche de diffusion de messages dans un domaine spécifique.
 */
public class SpeakerTask implements TaskContext {
    private final String domain;

    /**
     * Initialise une nouvelle instance de SpeakerTask.
     *
     * @param domain Le domaine associé à cette tâche.
     */
    public SpeakerTask(String domain) {
        this.domain = domain;
    }

    /**
     * Récupère le nom du domaine.
     *
     * @return Le nom du domaine.
     */
    @Override
    public String getDomain() {
        return domain;
    }

    /**
     * Envoie un message au domaine configuré.
     *
     * @param message Le contenu du message à envoyer.
     */
    @Override
    public void send(String message) {
        // todo
    }
}
