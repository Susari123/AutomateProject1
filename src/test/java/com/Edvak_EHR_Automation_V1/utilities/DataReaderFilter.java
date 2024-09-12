package com.Edvak_EHR_Automation_V1.utilities;
 
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class DataReaderFilter {

    public HashMap<String, Object> getJsonDataToMapFilter() throws IOException {
        // Path to your JSON file
        File file = new File("C:\\Users\\sksusari\\Documents\\Test\\filter_cases.json");

        // ObjectMapper to read the JSON
        ObjectMapper mapper = new ObjectMapper();

        // Use readValue() instead of readValues() to read a single object
        HashMap<String, Object> dataMap = mapper.readValue(file, new TypeReference<HashMap<String, Object>>() {});

        return dataMap;
    }
}
