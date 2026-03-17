package org.helmo.services;

import org.helmo.ISubscriber;
import org.helmo.client.Client;
import org.helmo.repository.IUserRepository;
import org.helmo.Exception.UserReaderException;

import java.util.List;

public class UserManagerService  {
    private final IUserRepository userRepository;

    public UserManagerService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<Client> getUsers() throws UserReaderException {
        return userRepository.getUsers();
    }

    public List<ISubscriber> getUserSubscribers(String username) throws UserReaderException {
        return getUsers().stream()
                .filter(u -> u.getName().equals(username))
                .findFirst()
                .map(Client::getSubscribers)
                .orElseThrow(() -> new UserReaderException("User not found"));
    }
}
