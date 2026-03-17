package org.helmo.mapper;

import org.helmo.ISubscriber;
import org.helmo.subscriber.RemoteUser;
import org.helmo.client.Client;
import org.helmo.client.Tag;
import org.helmo.dto.TagDto;
import org.helmo.dto.TagsDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonTagMapper {
    private static final List<String> EMPTY_USER_TAGS = List.of();

    public static List<Tag> mapTags(TagsDto dto, Map<String, Client> userMap, String currentDomain) {
        List<TagDto> tagDtos = dto.tags() == null ? List.of() : dto.tags();
        List<Tag> tags = tagDtos.stream().map(JsonTagMapper::createTag).toList();
        Map<String, Tag> tagMap = tags.stream().collect(Collectors.toMap(Tag::getName, t -> t));
        return tags;
    }
    private static Tag createTag(TagDto dto) {
        return new Tag(dto.tag(), new ArrayList<>());
    }


    public static TagDto toTagDto(Tag tag) {
        return new TagDto(
                tag.getName(),
                new ArrayList<>() // todo à modifier
        );

    }

}
