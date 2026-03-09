package org.helmo;

import org.helmo.client.Client;
import org.helmo.client.HashPassword;
import org.helmo.client.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class JsonMapper {
    // Liste vide réutilisable représentant "aucun tag" associé à un utilisateur,
    // afin d'éviter de recréer une nouvelle liste vide à chaque fois.
    private static final List<String> EMPTY_USER_TAGS = List.of();

    public static Config fromDto(ConfigDto dto) {
        validate(dto);

        return new Config(
                dto.currentDomain(),
                dto.saltSizeInBytes(),
                dto.multicastAddress(),
                dto.multicastPort(),
                dto.unicastPort(),
                dto.relayPort(),
                dto.networkInterface(),
                dto.base64AES(),
                dto.tls()
        );
    }

    private static void validate(ConfigDto dto) {
        Objects.requireNonNull(dto, "ConfigDto cannot be null");
        // todo si besoin de vérifier d'autres éléments
    }

    public static List<Client> mapUsers(UsersDto dto, String currentDomain) {
        List<UserDto> userDtos = dto.users() == null ? List.of() : dto.users();
        List<Client> clients = userDtos.stream().map(JsonMapper::createUser).toList();
        Map<String, Client> userMap = clients.stream().collect(Collectors.toMap(Client::getName, u -> u));
        resolveUserSubscribers(userDtos, userMap, currentDomain);
        return clients;
    }

    private static Client createUser(UserDto dto) {
        return new Client(
                dto.login(),
                new HashPassword(
                        dto.bcryptHash(),
                        dto.bcryptRound(),
                        dto.bcryptSalt()
                ),
                new ArrayList<>()
        );
    }

    public static List<Tag> mapTags(TagsDto dto, Map<String, Client> userMap, String currentDomain) {
        List<TagDto> tagDtos = dto.tags() == null ? List.of() : dto.tags();
        List<Tag> tags = tagDtos.stream().map(JsonMapper::createTag).toList();
        Map<String, Tag> tagMap = tags.stream().collect(Collectors.toMap(Tag::getName, t -> t));
        resolveTagSubscribers(tagDtos, tagMap, userMap, currentDomain);
        return tags;
    }

    private static Tag createTag(TagDto dto) {
        return new Tag(dto.tag(), new ArrayList<>());
    }
    private static void resolveUserSubscribers(List<UserDto> userDtos, Map<String, Client> userMap, String currentDomain) {
        for (UserDto userDto : userDtos) {
            Client client = userMap.get(userDto.login());
            if (userDto.subscribers() != null) {
                for (String subscriberName : userDto.subscribers()) {
                    ISubscriber subscriber = resolveSubscriber(subscriberName, currentDomain, userMap);
                    if (subscriber != null) {
                        client.subscribe(subscriber);
                    }
                }
            }
        }
    }

    private static void resolveTagSubscribers(List<TagDto> tagDtos, Map<String, Tag> tagMap, Map<String, Client> userMap, String currentDomain) {
        for (TagDto tagDto : tagDtos) {
            Tag tag = tagMap.get(tagDto.tag());

            if (tagDto.subscribers() != null) {
                for (String subscriberName : tagDto.subscribers()) {
                    ISubscriber subscriber = resolveSubscriber(subscriberName, currentDomain, userMap);
                    if (subscriber != null) {
                        tag.subscribe(subscriber);
                    }
                }
            }
        }
    }

    private static ISubscriber resolveSubscriber(String fullName, String currentDomain, Map<String, Client> userMap) {
        if (fullName == null) {
            return null;
        }
        int index = fullName.indexOf("@");
        if (index == -1) {
            return userMap.get(fullName);
        }
        String login = fullName.substring(0, index);
        String userDomain = fullName.substring(index + 1);
        if (userDomain.equals(currentDomain) && userMap.containsKey(login)) {
            // utilisateur interne au node
            return userMap.get(login);
        }
            // utilisateur externe au node
        return new RemoteUser(fullName);
    }

    public static UserDto toUserDto(Client client) {
        HashPassword hashPassword = client.getHashPassword();
        return new UserDto(
                client.getName(),
                hashPassword.storedBcryptHash(),
                hashPassword.bcryptRound(),
                hashPassword.bcryptSalt(),
                client.getSubscribers().stream().map(ISubscriber::getFullName).toList(),
                EMPTY_USER_TAGS
        );
    }

    public static TagDto toTagDto(Tag tag) {
        return new TagDto(
                tag.getName(),
                tag.getSubscribers().stream().map(ISubscriber::getFullName).toList()
        );

    }
}