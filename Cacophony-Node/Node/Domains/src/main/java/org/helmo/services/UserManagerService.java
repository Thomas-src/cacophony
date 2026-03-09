package org.helmo.services;

import org.helmo.User;
import org.helmo.repository.IUserRepository;
import org.helmo.Exception.UserReaderException;

import java.util.List;

public class UserManagerService  {
    private final IUserRepository userRepository;

    public UserManagerService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() throws UserReaderException {
        return userRepository.getUsers();
    }
}
