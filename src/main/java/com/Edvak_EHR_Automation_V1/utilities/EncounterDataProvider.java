package com.Edvak_EHR_Automation_V1.utilities;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EncounterDataProvider {

    // Helper class to store encounter data
    public static class EncounterData {
        private String encounterNumber;
        private String status;

        public EncounterData(String encounterNumber, String status) {
            this.encounterNumber = encounterNumber;
            this.status = status;
        }

        public String getEncounterNumber() {
            return encounterNumber;
        }

        public String getStatus() {
            return status;
        }
    }

    // Method to read encounter data from JSON file
    public List<EncounterData> readEncounterDataFromJson() {
        List<EncounterData> encounterDataList = new ArrayList<>();
        Gson gson = new Gson();

        try (FileReader reader = new FileReader("encounters_with_status.json")) {
            // Read the JSON array from the file
            JsonArray encounterArray = gson.fromJson(reader, JsonArray.class);

            // Iterate through each element in the JSON array
            for (JsonElement element : encounterArray) {
                JsonObject encounterObject = element.getAsJsonObject();

                // Extract the encounter number and status
                String encounterNumber = encounterObject.get("encounter_number").getAsString();
                String status = encounterObject.get("status").getAsString();

                // Add to the list of encounter data
                encounterDataList.add(new EncounterData(encounterNumber, status));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return encounterDataList;
    }

}