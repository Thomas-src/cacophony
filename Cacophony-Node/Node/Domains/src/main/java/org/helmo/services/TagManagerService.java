package org.helmo.services;

import org.helmo.Exception.TagReaderException;
import org.helmo.client.Tag;
import org.helmo.repository.ITagRepository;
import org.helmo.subscriber.IUserSubscriber;
import java.util.List;

public class TagManagerService {
    private final ITagRepository tagRepository;
    public TagManagerService(ITagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> getTags() throws TagReaderException {
        return tagRepository.getTags();
    }

    public List<IUserSubscriber> getTagSubscriber(String tagName) throws TagReaderException {
        return getTags().stream()
                .filter(u -> u.getFullName().equals(tagName))
                .findFirst()
                .map(Tag::getSubscribers)
                .orElse(null);
    }
}
