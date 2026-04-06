package org.helmo.services;

import org.helmo.Exception.MessageSaveException;
import org.helmo.crypto.CryptoProvider;
import org.helmo.crypto.exceptions.EncryptionException;
import org.helmo.server.threads.EastThread;


public class SpeakerService {
    private final CryptoProvider cryptoService;
    private final EastThread eastThread;

    public SpeakerService(CryptoProvider cryptoService, EastThread eastThread) {
        this.cryptoService = cryptoService;
        this.eastThread = eastThread;
    }

    public void sendCryptedMessage(String message) throws EncryptionException, MessageSaveException {
        String cryptedMessage = cryptoService.encrypt(message);
        eastThread.sendMessage(cryptedMessage);
    }
}
