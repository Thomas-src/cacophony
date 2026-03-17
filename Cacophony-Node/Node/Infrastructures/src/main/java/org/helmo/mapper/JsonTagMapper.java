package org.helmo.mapper;

import org.helmo.client.Client;
import org.helmo.subscriber.IUserSubscriber;
import org.helmo.subscriber.NativeUser;
import org.helmo.subscriber.RemoteUser;
import org.helmo.tag.InternalTag;
import org.helmo.dto.TagDto;
import org.helmo.dto.TagsDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonTagMapper {
    private static final List<String> EMPTY_USER_TAGS = List.of();

    public static List<InternalTag> mapTags(TagsDto dto, Map<String, Client> userMap, String currentDomain) {
        List<TagDto> tagDtos = dto.tags() == null ? List.of() : dto.tags();
        return tagDtos.stream().map(t-> {
            return createTag(t, currentDomain);
        }).toList();
    }
    private static InternalTag createTag(TagDto dto, String currentDomain) {
        List<IUserSubscriber> subscribers = new ArrayList<>();
        for (String subscriberName : dto.subscribers()) {
            // todo à modifier (si possible exeternaliser)
            String domain = subscriberName.substring(subscriberName.indexOf("@") + 1);
            if (domain.equals(currentDomain)) {
                subscribers.add(new NativeUser(subscriberName));
            } else {
                subscribers.add(new RemoteUser(subscriberName));
            }
        }

        return new InternalTag(dto.tag(), subscribers);
    }


    public static TagDto toTagDto(InternalTag internalTag) {
        return new TagDto(
                internalTag.getFullName(),
                internalTag.getSubscribers().stream().map(IUserSubscriber::getFullName).toList() // todo à vérifier ???
        );

    }

}
