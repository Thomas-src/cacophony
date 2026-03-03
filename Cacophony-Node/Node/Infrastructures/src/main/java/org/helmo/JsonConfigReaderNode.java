package org.helmo;


import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonConfigReaderNode {

    private final Gson gson;

    public JsonConfigReaderNode()
    {
        this.gson = new Gson();
    }

    private ConfigDto readConfig() throws IOException
    {
        String FILE_NAME = "config-node.json";
        Path path = Paths.get(FILE_NAME);
        try (BufferedReader reader = Files.newBufferedReader(path)){
            return gson.fromJson(reader, ConfigDto.class);
        }
    }






}
