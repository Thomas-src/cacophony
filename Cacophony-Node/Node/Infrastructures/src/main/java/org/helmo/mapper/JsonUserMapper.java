package org.helmo.mapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.client.Client;
import org.helmo.client.HashPassword;
import org.helmo.dto.UserDto;
import org.helmo.dto.UsersDto;
import org.helmo.subscriber.NativeUser;
import org.helmo.subscriber.IUserSubscriber;
import org.helmo.subscriber.RemoteUser;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import org.helmo.Grammar;

public class JsonUserMapper {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<String> EMPTY_USER_TAGS = List.of();

    public static List<Client> mapUsers(UsersDto dto, String currentDomain) {
        List<UserDto> userDtos = dto.users() == null ? List.of() : dto.users();
        return userDtos.stream().map(u -> JsonUserMapper.createUser(u,currentDomain)).toList();
    }

    private static Client createUser(UserDto dto, String currentDomain) {
        List<IUserSubscriber> subscribers = new ArrayList<>();
        if (dto.subscribers() != null) {
            for (String subscriberName : dto.subscribers()) {
                if (subscriberName != null && !subscriberName.isBlank()) {
                    Matcher matcher = Grammar.P_USER_AT_DOMAIN.matcher(subscriberName);
                    if (matcher.matches()) {
                        // todo modifier
                        String domain = subscriberName.substring(subscriberName.indexOf("@") + 1);
                        if (domain.equals(currentDomain)) {
                              subscribers.add(new NativeUser(subscriberName));
                        } else {
                             subscribers.add(new RemoteUser(subscriberName));
                        }
                    } else {
                        // si pas de domaine => utilisateur interne
                        subscribers.add(new NativeUser(subscriberName));
                    }
                }
            }
            return new Client(
                    dto.login(),
                    new HashPassword(dto.bcryptHash(), dto.bcryptRound(), dto.bcryptSalt()),
                    subscribers,
                    currentDomain
            );
        }
        return null;
    }


    public static UserDto toUserDto(Client client) {
        HashPassword hashPassword = client.getHashPassword();
        return new UserDto(
                client.getName(),
                hashPassword.storedBcryptHash(),
                hashPassword.bcryptRound(),
                hashPassword.bcryptSalt(),
                client.getSubscribers().stream().map(IUserSubscriber::getFullName).toList(),
                EMPTY_USER_TAGS
        );
    }
}
