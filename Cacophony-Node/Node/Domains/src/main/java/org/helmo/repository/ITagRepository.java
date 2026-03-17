package org.helmo.repository;

import org.helmo.Exception.TagReaderException;
import org.helmo.Exception.TagSaveException;
import org.helmo.tag.InternalTag;

import java.util.List;

public interface ITagRepository {
    List<InternalTag> getTags() throws TagReaderException;
    void saveTag(InternalTag internalTag) throws TagSaveException;
}
