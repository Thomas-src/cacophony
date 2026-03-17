package org.helmo.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.client.Client;
import org.helmo.repository.IUserRepository;
import org.helmo.Exception.UserReaderException;
import org.helmo.subscriber.IUserSubscriber;

import java.util.List;

public class UserManagerService {
    private final IUserRepository userRepository;
    public UserManagerService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<Client> getUsers() throws UserReaderException {
        return userRepository.getUsers();
    }

    public List<IUserSubscriber> getUserSubscribers(String username) throws UserReaderException {
        return getUsers().stream()
                .filter(u -> u.getFullName().equals(username))
                .findFirst()
                .map(Client::getSubscribers)
                .orElseThrow(() -> new UserReaderException("User not found: " + username));
    }
}

