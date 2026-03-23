package org.helmo.services;

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
                .orElse(null);
    }
    public Client findUser(String username) throws UserReaderException {
        List<Client> users = getUsers();
        for (Client user : users) {
            if (user.getFullName().equals(username)) {
                return user;
            }
        }
        return null;
    }
}

