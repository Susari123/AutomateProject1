package com.Edvak_EHR_Automation_V1.utilities;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DataReaderFilter {

    // Method to read JSON from a file and return a HashMap
    public HashMap<String, String> getJsonDataToMapFilter() throws IOException {
        HashMap<String, String> dataMap = new HashMap<>();

        try {
            // Read the entire JSON file as a String
            String filePath = "C:\\Users\\sksusari\\Documents\\Test\\filter_cases.json";
            String jsonContent = new String(Files.readAllBytes(Paths.get(filePath)));

            // Log file content for debugging
            System.out.println("File content: " + jsonContent);

            // Convert the String content into a JSONObject
            JSONObject jsonObject = new JSONObject(jsonContent);

            // Store the status as a comma-separated string
            if (jsonObject.has("status")) {
                JSONArray statusArray = jsonObject.getJSONArray("status");
                dataMap.put("status", jsonArrayToString(statusArray));
            }

            // Store patient names
            if (jsonObject.has("patientNames")) {
                JSONArray patientNamesArray = jsonObject.getJSONArray("patientNames");
                dataMap.put("patientNames", jsonArrayToString(patientNamesArray));
            }

            // Store provider names
            if (jsonObject.has("providerNames")) {
                JSONArray providerNamesArray = jsonObject.getJSONArray("providerNames");
                dataMap.put("providerNames", jsonArrayToString(providerNamesArray));
            }

            // Store createdBy names
            if (jsonObject.has("createdByNames")) {
                JSONArray createdByNamesArray = jsonObject.getJSONArray("createdByNames");
                dataMap.put("createdByNames", jsonArrayToString(createdByNamesArray));
            }

            // Store date range
            if (jsonObject.has("dateRange")) {
                JSONObject dateRange = jsonObject.getJSONObject("dateRange");
                dataMap.put("fromDate", dateRange.getString("from"));
                dataMap.put("toDate", dateRange.getString("to"));
            }

        } catch (Exception e) {
            System.out.println("Error occurred while reading JSON data: " + e.getMessage());
            e.printStackTrace();
        }

        return dataMap;
    }

    // Utility method to convert a JSONArray to a comma-separated String
    private String jsonArrayToString(JSONArray jsonArray) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(jsonArray.getString(i));
        }
        return sb.toString();
    }
}