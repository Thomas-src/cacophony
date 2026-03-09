package org.helmo.repository;

import org.helmo.Exception.TagReaderException;
import org.helmo.Exception.TagSaveException;
import org.helmo.client.Tag;

import java.util.List;

public interface ITagRepository {
    List<Tag> getTags() throws TagReaderException;
    void saveTag(Tag tag) throws TagSaveException;
}
