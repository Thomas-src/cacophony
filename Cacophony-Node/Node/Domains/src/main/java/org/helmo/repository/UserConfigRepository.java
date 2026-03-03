package org.helmo.repository;

import org.helmo.User;

import java.util.List;

public interface UserConfigRepository {
    void saveUser(User user);
    List<User> getUsers();
}
