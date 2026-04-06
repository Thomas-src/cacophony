package org.helmo.task;

/**
 * Interface définissant un mécanisme d'envoi de messages.
 */
public interface IMessageSender {
    /**
     * Envoie un message textuel.
     *
     * @param message Le contenu du message à envoyer.
     */
    void send(String message);
}
