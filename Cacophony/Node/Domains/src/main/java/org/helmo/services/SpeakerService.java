package org.helmo.services;

import org.helmo.Exception.MessageSaveException;
import org.helmo.crypto.CryptoProvider;
import org.helmo.crypto.exceptions.EncryptionException;
import org.helmo.server.threads.EastThread;

/**
 * Service responsable de la communication sécurisée des messages.
 */
public class SpeakerService {
    private final CryptoProvider cryptoService;
    private final EastThread eastThread;

    /**
     * Initialise une nouvelle instance de SpeakerService.
     *
     * @param cryptoService Le fournisseur de services cryptographiques.
     * @param eastThread    Le thread de communication vers l'Est.
     */
    public SpeakerService(CryptoProvider cryptoService, EastThread eastThread) {
        this.cryptoService = cryptoService;
        this.eastThread = eastThread;
    }

    /**
     * Chiffre un message et l'envoie via le thread de communication.
     *
     * @param message Le message en clair à envoyer.
     * @throws EncryptionException  Si une erreur survient lors du chiffrement.
     * @throws MessageSaveException Si une erreur survient lors de la sauvegarde ou de l'envoi du message.
     */
    public void sendCryptedMessage(String message) throws EncryptionException, MessageSaveException {
        String cryptedMessage = cryptoService.encrypt(message);
        eastThread.sendMessage(cryptedMessage);
    }
}
