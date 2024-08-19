package com.Edvak_EHR_Automation_V1.utilities;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class DataReader {

    /**
     * Reads JSON data from a file and converts it to a List of HashMaps.
     *
     * @param filePath the path to the JSON file
     * @return a List of HashMaps containing the JSON data
     * @throws IOException if an I/O error occurs
     */
	public List<HashMap<String, String>> getJsonDataToMap() throws IOException {
        File file = new File("C:\\Users\\sksusari\\Documents\\Test\\Billing.json");
        String jsonContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        System.out.println("JSON Content: " + jsonContent);
        
        //System.out.println("JSON Content: " + jsonContent);
        // Convert JSON string to List of HashMaps using Jackson
        ObjectMapper mapper = new ObjectMapper();

        List<HashMap<String, String>> lst = new ArrayList<>();
        
    
        lst = mapper.readValue(jsonContent, new TypeReference<List<HashMap<String, String>>>() {});

        return lst;
    }
	
}
