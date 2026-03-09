package org.helmo.repository;
import org.helmo.Exception.UserReaderException;
import org.helmo.Exception.UserSaveException;
import org.helmo.User;
import java.util.List;

public interface IUserRepository {
    List<User> getUsers() throws UserReaderException;
    void saveUsers(User user) throws UserSaveException;
}
