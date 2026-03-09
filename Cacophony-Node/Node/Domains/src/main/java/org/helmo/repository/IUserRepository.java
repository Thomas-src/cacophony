package org.helmo.repository;
import org.helmo.Exception.UserReaderException;
import org.helmo.Exception.UserSaveException;
import org.helmo.client.Client;
import java.util.List;

public interface IUserRepository {
    List<Client> getUsers() throws UserReaderException;
    void saveUsers(Client client) throws UserSaveException;
}
