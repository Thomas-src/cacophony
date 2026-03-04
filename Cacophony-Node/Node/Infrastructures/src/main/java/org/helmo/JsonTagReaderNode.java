package org.helmo;


import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.repository.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


public class JsonTagReaderNode implements ITagRepository {

    private static final String FILE_NAME = "tags.json";
    private static final Gson gson = new Gson();;
    private static final Path configPath = Paths.get(FILE_NAME);
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public List<Tag> getTags() throws TagReaderException {
        /*
        try (BufferedReader reader = Files.newBufferedReader(configPath)){
            TagDto dto = gson.fromJson(reader, TagDto.class);

            if (dto == null) {
                LOGGER.info("Fichier tag vide ou invalide");
                throw new TagReaderException("Fichier tag vide ou invalide");
            }

            return JsonMapper.mapTags(dto);
        }  catch (IOException ex) {
            LOGGER.error("Impossible de charger le fichier tag", ex);
            throw new TagReaderException("Impossible de charger le fichier tag");
        }
         */
        return null;
    }

    @Override
    public void saveTag(Tag tag) throws TagSaveException {

    }

}
