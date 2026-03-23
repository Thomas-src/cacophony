package org.helmo.repository;

import org.helmo.Exception.TagReaderException;
import org.helmo.Exception.TagSaveException;
import org.helmo.client.Client;
import org.helmo.tag.InternalTag;

import java.util.List;

public interface ITagRepository {
    List<InternalTag> getTags() throws TagReaderException;
    void saveTag(InternalTag internalTag) throws TagSaveException;

    void createTag(Client client, String tag)throws TagSaveException;

    void addUserToTag(Client client, String tag) throws TagSaveException;
}
