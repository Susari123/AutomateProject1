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

    // Inner class to represent each encounter record
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

    // Method to read JSON file and return list of EncounterData
    public List<EncounterData> readEncounterDataFromJson() throws Exception {
        List<EncounterData> encounterDataList = new ArrayList<>();
        Gson gson = new Gson();

        try (FileReader reader = new FileReader("src/test/resources/output/encounters_with_status.json")) {
            JsonArray encounterArray = gson.fromJson(reader, JsonArray.class);

            for (JsonElement element : encounterArray) {
    try {
        JsonObject obj = element.getAsJsonObject();
        String encounterNumber = obj.get("encounter_number").getAsString();
        String status = obj.get("status").getAsString();
        encounterDataList.add(new EncounterData(encounterNumber, status));
    } catch (Exception ex) {
        System.err.println("Failed to parse element: " + element);
        ex.printStackTrace();
    }
}


        } catch (IOException e) {
            System.err.println("Error reading encounter JSON file:");
            e.printStackTrace();
        }

        return encounterDataList;
    }
}
