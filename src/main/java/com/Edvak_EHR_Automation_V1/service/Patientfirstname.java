package com.Edvak_EHR_Automation_V1.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

@Service
public class Patientfirstname {

    public void testGetPatientData(String token, String defaultLocation, String defaultLocationTimeZone) throws IOException {
        // Base API URL
        RestAssured.baseURI = "https://darwinapi.edvak.com:3000";
    
        int currentPage = 0;
        int maxPages = 6;
    
        // File Setup
        String directoryPath = "src/test/resources/output";
        String outputFilePath = directoryPath + "/PatientDetails.json";
        File directory = new File(directoryPath);
        if (!directory.exists()) directory.mkdirs();
    
        StringBuilder jsonContent = new StringBuilder();
        jsonContent.append("[\n");
    
        while (currentPage < maxPages) {
            String endpoint = "/patients/patientsList?page=" + currentPage + "&pageSize=20&searchTerm=&status=false";
    
            // Send GET Request
            Response response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json, text/plain, */*")
                .header("Location_ID", defaultLocation)
                .header("Moment", defaultLocationTimeZone)
                .when()
                .get(endpoint)
                .then()
                .extract()
                .response();
    
            // Validate Response
            if (response.statusCode() != 200) {
                System.out.println("❌ API Error: " + response.statusCode());
                break;
            }
    
            // Extract 'result' as a List<Object>
List<Object> result = response.jsonPath().getList("result");

if (result != null && result.size() >= 2) {
    // Get the second item which is the actual patient list
    List<Map<String, Object>> patients = (List<Map<String, Object>>) result.get(1);

    if (patients == null || patients.isEmpty()) {
        System.out.println("No patient data found on page " + currentPage);
        break;
    }

    for (Map<String, Object> patient : patients) {
        String firstName = patient.getOrDefault("first_name", "null").toString();
        String lastName = patient.getOrDefault("last_name", "null").toString();
        boolean selfPay = patient.getOrDefault("selfPay", false).equals(true);

        // Extract insurances properly
        boolean hasInsuranceField = false;
        Object insurancesObject = patient.get("insurances");

        if (insurancesObject instanceof List) {
            List<Map<String, Object>> insurances = (List<Map<String, Object>>) insurancesObject;
            for (Map<String, Object> insurance : insurances) {
                if (insurance.containsKey("0") && insurance.get("0") == null) {
                    hasInsuranceField = true;
                    break;
                }
            }
        }

        // Append Data to JSON
        jsonContent.append("  {\n");
        jsonContent.append("    \"first_name\": \"").append(firstName).append("\",\n");
        jsonContent.append("    \"last_name\": \"").append(lastName).append("\",\n");
        jsonContent.append("    \"selfPay\": ").append(selfPay).append(",\n");
        jsonContent.append("    \"hasInsuranceField\": ").append(hasInsuranceField).append("\n");
        jsonContent.append("  },\n");
    }

} else {
    System.out.println("Error: Invalid result structure on page " + currentPage);
    break;
}
    
            currentPage++;
        }
    
        // Remove last comma and close JSON array
        int lastCommaIndex = jsonContent.lastIndexOf(",");
        if (lastCommaIndex != -1) {
            jsonContent.deleteCharAt(lastCommaIndex);
        }
        jsonContent.append("\n]");
    
        // Write JSON output to file
        try (FileWriter file = new FileWriter(outputFilePath)) {
            file.write(jsonContent.toString());
            System.out.println("✅ Patient details saved to: " + outputFilePath);
        }
    }
    
}
