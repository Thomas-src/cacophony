package org.helmo.repository;

import org.helmo.Exception.MessageReaderException;
import org.helmo.Exception.MessageSaveException;

import java.util.List;

public interface IMessageRepository {
    void saveMessage(String message) throws MessageSaveException;
    List<String> getAllAndDeleteMessages() throws MessageReaderException;
}
