package com.Edvak_EHR_Automation_V1.utilities;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataReader {

    /**
     * Reads a limited number of entries from a JSON file without loading all the data into memory.
     *
     * @param limit the maximum number of entries to read
     * @return a List of HashMaps containing the limited JSON data
     * @throws IOException if an I/O error occurs
     */
    public List<HashMap<String, String>> getLimitedJsonData(int limit) throws IOException {
        // File path
        File file = new File("C:\\Users\\sksusari\\git\\ehr-webapp-automation\\src\\test\\resources\\output\\CombinedData.json");

        if (!file.exists()) {
            throw new IOException("File not found: " + file.getAbsolutePath());
        }

        // ObjectMapper for mapping JSON objects
        ObjectMapper mapper = new ObjectMapper();

        // JsonFactory to stream data
        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createParser(file);

        List<HashMap<String, String>> limitedData = new ArrayList<>();
        int count = 0;

        // Ensure JSON array starts
        if (parser.nextToken() != JsonToken.START_ARRAY) {
            throw new IOException("Invalid JSON format: Expected an array");
        }

        // Iterate over JSON array
        while (parser.nextToken() == JsonToken.START_OBJECT && count < limit) {
            HashMap<String, String> entry = mapper.readValue(parser, HashMap.class);
            limitedData.add(entry);
            count++;
        }

        parser.close();
        return limitedData;
    }
}
