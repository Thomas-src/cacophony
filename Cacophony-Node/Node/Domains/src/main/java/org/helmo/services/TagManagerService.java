package org.helmo.services;

import org.helmo.Exception.TagReaderException;
import org.helmo.Exception.TagSaveException;
import org.helmo.client.Client;
import org.helmo.tag.InternalTag;
import org.helmo.repository.ITagRepository;
import org.helmo.subscriber.IUserSubscriber;

import java.util.Collections;
import java.util.List;

public class TagManagerService {
    private final ITagRepository tagRepository;
    public TagManagerService(ITagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<InternalTag> getTags() throws TagReaderException {
        return tagRepository.getTags();
    }

    public List<IUserSubscriber> getTagSubscriber(String tagName) throws TagReaderException {
        return getTags().stream()
                .filter(u -> u.getFullName().equals(tagName)) // todo equalsIgnoreCase ????
                .findFirst()
                .map(InternalTag::getSubscribers)
                .orElse(Collections.emptyList());
    }

    public void createTag(Client client, String tag) throws TagSaveException {
        tagRepository.createTag(client, tag);
    }

    public void addUserToTag(Client client, String tag) throws TagSaveException {
        tagRepository.addUserToTag(client, tag);
    }
}
