package org.helmo.repository;

import org.helmo.Tag;

import java.util.List;

public interface ITagRepository {
    List<Tag> getTags() throws TagReaderException ;
    void saveTag(Tag tag) throws TagSaveException ;
}
