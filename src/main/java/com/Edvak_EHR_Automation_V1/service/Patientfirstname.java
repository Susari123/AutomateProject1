package com.Edvak_EHR_Automation_V1.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

@Service
public class Patientfirstname {

    private static final Logger logger = LoggerFactory.getLogger(Patientfirstname.class);

    public void testGetPatientData(String token, String defaultLocation, String defaultLocationTimeZone) throws IOException {
        RestAssured.baseURI = "https://darwinapi.edvak.com:3000";

        int currentPage = 0;
        int maxPages = 6;

        String directoryPath = "src/test/resources/output";
        String outputFilePath = directoryPath + "/PatientDetails.json";
        File directory = new File(directoryPath);
        if (!directory.exists()) directory.mkdirs();

        StringBuilder jsonContent = new StringBuilder();
        jsonContent.append("[\n");

        while (currentPage < maxPages) {
            String endpoint = "/patients/patientsList?page=" + currentPage + "&pageSize=20&searchTerm=&status=false";

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

            if (response.statusCode() != 200) {
                logger.error("❌ API Error on page {}: {}", currentPage, response.statusCode());
                break;
            }

            List<Object> result = response.jsonPath().getList("result");

            if (result != null && result.size() >= 2) {
                List<Map<String, Object>> patients = (List<Map<String, Object>>) result.get(1);

                if (patients == null || patients.isEmpty()) {
                    logger.warn("No patient data found on page {}", currentPage);
                    break;
                }

                for (Map<String, Object> patient : patients) {
                    String firstName = (String) patient.get("first_name");
                    String lastName = (String) patient.get("last_name");
                    boolean selfPay = Boolean.TRUE.equals(patient.get("selfPay"));

                    boolean hasInsuranceField = false;
                    Object insurancesObject = patient.get("insurances");

                    if (insurancesObject instanceof List) {
                        List<Map<String, Object>> insurances = (List<Map<String, Object>>) insurancesObject;
                        for (Map<String, Object> insurance : insurances) {
                            Object nested = insurance.get("insurances");
                            if (nested instanceof Map) {
                                Map<String, Object> nestedMap = (Map<String, Object>) nested;
                                if (nestedMap.containsKey("0")) {
                                    hasInsuranceField = nestedMap.get("0") != null;
                                    break;
                                }
                            }
                        }
                    }

                    jsonContent.append("  {\n");
                    jsonContent.append("    \"first_name\": ").append(firstName != null ? "\"" + firstName + "\"" : null).append(",\n");
                    jsonContent.append("    \"last_name\": ").append(lastName != null ? "\"" + lastName + "\"" : null).append(",\n");
                    jsonContent.append("    \"selfPay\": ").append(selfPay).append(",\n");
                    jsonContent.append("    \"hasInsuranceField\": ").append(hasInsuranceField).append("\n");
                    jsonContent.append("  },\n");
                }

            } else {
                logger.error("Invalid result structure on page {}", currentPage);
                break;
            }

            currentPage++;
        }

        // Trim last comma
        int lastCommaIndex = jsonContent.lastIndexOf(",");
        if (lastCommaIndex != -1) {
            jsonContent.deleteCharAt(lastCommaIndex);
        }
        jsonContent.append("\n]");

        try (FileWriter file = new FileWriter(outputFilePath)) {
            file.write(jsonContent.toString());
            logger.info("✅ Patient details saved to: {}", outputFilePath);
        }
    }
}
