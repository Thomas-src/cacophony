package org.helmo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class JsonMapper {

    public static Config fromDto(ConfigDto dto) {
        validate(dto);
        List<User> users = mapUsers(dto);
        List<Tag> tags = mapTags(dto);
        resolveSubscriptions(dto, users, tags);

        return buildConfig(dto, users, tags);
    }

    private static void validate(ConfigDto dto) {
        Objects.requireNonNull(dto, "ConfigDto cannot be null");
        // todo si besoin de vérifier d'autres éléments
    }

    private static Config buildConfig(ConfigDto dto, List<User> users, List<Tag> tags) {
        return new Config(
                dto.currentDomain(),
                dto.saltSizeInBytes(),
                dto.multicastAddress(),
                dto.multicastPort(),
                dto.unicastPort(),
                dto.relayPort(),
                dto.networkInterface(),
                dto.base64AES(),
                dto.tls(),
                users,
                tags
        );
    }

    private static List<User> mapUsers(ConfigDto dto) {
        List<UserDto> userDtos = dto.users() == null ? List.of() : dto.users();

        return userDtos.stream()
                .map(JsonMapper::mapUser)
                .toList();
    }

    private static User mapUser(UserDto dto) {
        return new User(
                dto.login(),
                new HashPassword(
                        dto.bcryptHash(),
                        dto.bcryptRound(),
                        dto.bcryptSalt()
                ),
                new ArrayList<>()
        );
    }

    private static List<Tag> mapTags(ConfigDto dto) {
        List<TagDto> tagDtos = dto.tags() == null ? List.of() : dto.tags();

        return tagDtos.stream()
                .map(JsonMapper::mapTag)
                .toList();
    }

    private static Tag mapTag(TagDto dto) {
        return new Tag(dto.tag(), new ArrayList<>());
    }

    private static void resolveSubscriptions(ConfigDto dto, List<User> users, List<Tag> tags) {
        Map<String, User> userMap = users.stream().collect(Collectors.toMap(User::getName, u -> u));
        Map<String, Tag> tagMap = tags.stream().collect(Collectors.toMap(Tag::getName, t -> t));

        resolveTagSubscribers(dto, userMap, tagMap);
        resolveUserSubscribers(dto, userMap);
    }

    private static void resolveTagSubscribers(ConfigDto dto, Map<String, User> userMap, Map<String, Tag> tagMap) {
        if (dto.tags() == null) {
            return;
        }

        for (TagDto tagDto : dto.tags()) {
            Tag tag = tagMap.get(tagDto.tag());

            if (tagDto.subscribers() == null) {
                continue;
            }
            for (String subscriberName : tagDto.subscribers()) {

                ISubscriber subscriber = resolveSubscriber(
                        subscriberName,
                        dto.currentDomain(),
                        userMap
                );

                if (subscriber != null) {
                    tag.subscribe(subscriber);
                }
            }
        }
    }

    private static void resolveUserSubscribers(ConfigDto dto, Map<String, User> userMap) {
        if (dto.users() == null) {
            return;
        }

        for (UserDto userDto : dto.users()) {
            User user = userMap.get(userDto.login());

            if (userDto.subscribers() == null) {
                continue;
            }

            for (String subscriberName : userDto.subscribers()) {

                ISubscriber subscriber = resolveSubscriber(
                        subscriberName,
                        dto.currentDomain(),
                        userMap
                );

                if (subscriber != null) {
                    user.subscribe(subscriber);
                }
            }
        }
    }

    private static ISubscriber resolveSubscriber(String fullName, String currentDomain, Map<String, User> userMap) {
        if (fullName == null) {
            return null;
        }
        int index = fullName.indexOf("@");

        if (index > 0) {
            String domain = fullName.substring(index + 1);

            if (domain.equals(currentDomain)) {
                String login = fullName.substring(0, index);
                User localUser = userMap.get(login);

                if (localUser != null) {
                    return localUser;
                }
            }
        }

        return new RemoteUser(fullName);
    }
}