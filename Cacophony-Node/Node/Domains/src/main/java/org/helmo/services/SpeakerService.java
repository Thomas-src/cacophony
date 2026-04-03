package org.helmo.services;

import org.helmo.Exception.MessageSaveException;
import org.helmo.crypto.CryptoProvider;
import org.helmo.crypto.exceptions.EncryptionException;
import org.helmo.repository.IMessageRepository;
import org.helmo.server.threads.EastThread;


public class SpeakerService {
    private final CryptoProvider cryptoService;
    private final EastThread eastThread;
    private final IMessageRepository messageRepository;
    public SpeakerService(CryptoProvider cryptoService, EastThread eastThread, IMessageRepository messageRepository) {
        this.cryptoService = cryptoService;
        this.eastThread = eastThread;
        this.messageRepository = messageRepository;
    }

    public void sendCryptedMessage(String message) throws EncryptionException, MessageSaveException {
        String cryptedMessage =  cryptoService.encrypt(message);
            if(eastThread.isConnected()) {
                eastThread.sendMessage(cryptedMessage);
            } else {
                messageRepository.saveMessage(message);
            }
    }
}
