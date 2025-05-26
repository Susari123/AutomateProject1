package com.Edvak_EHR_Automation_V1.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.FileWriter;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

public class ClaimIddata {

    // ✅ No @Test here — it's a utility method
    public void testClaimIdData(WebDriver driver) throws Exception {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        driver.get("https://darwinapi.edvak.com");

        String token = (String) js.executeScript("return window.localStorage.getItem('token');");
        String userDetailsRaw = (String) js.executeScript("return window.localStorage.getItem('user_details');");

        if (userDetailsRaw == null || userDetailsRaw.trim().isEmpty()) {
            throw new IllegalStateException("Unable to retrieve 'user_details' from localStorage.");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode userDetails = mapper.readTree(userDetailsRaw);

        String p_id = userDetails.path("p_id").asText();
        String userid = userDetails.path("user_id").asText();
        String moment = userDetails.path("moment").asText();
        String location_id = userDetails.path("location_id").asText();

        // === Step 2: API call ===
        RestAssured.baseURI = "https://darwinapi.edvak.com:3000";
        String endpoint = "/billing/getclaims";

        String requestBody = "{"
                + "\"p_id\":\"" + p_id + "\","
                + "\"per_page\":20,"
                + "\"page\":1,"
                + "\"flag\":true,"
                + "\"sort\":true,"
                + "\"status\":[],"
                + "\"patient_id\":[],"
                + "\"provider\":[],"
                + "\"updated_by\":[],"
                + "\"sortOrder\":-1,"
                + "\"sortKey\":\"claimStart\","
                + "\"cursor\":null"
                + "}";

        Response response = given()
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json, text/plain, */*")
                .header("location_id", location_id)
                .header("moment", moment)
                .header("p_id", p_id)
                .header("pid", p_id)
                .header("userid", userid)
                .body(requestBody)
                .when()
                .post(endpoint)
                .then()
                .extract()
                .response();

        assertEquals(response.statusCode(), 200, "Expected status code 200");

        JsonNode root = mapper.readTree(response.getBody().asString());
        JsonNode resultArray = root.path("result");

        JsonArray outputArray = new JsonArray();

        if (resultArray.isArray()) {
            for (JsonNode item : resultArray) {
                JsonNode flatDoc = item.path("flat_doc");
                int status = flatDoc.path("status").asInt();
                if (status == 21 || status == 29) {
                    String encounterNumber = flatDoc.path("encounterNumber").asText();
                    JsonObject obj = new JsonObject();
                    obj.addProperty("encounter_number", encounterNumber);
                    obj.addProperty("status", String.valueOf(status));
                    outputArray.add(obj);
                }
            }
        }

        // === Save JSON to file ===
        String outputDirPath = "src/test/resources/output";
        File directory = new File(outputDirPath);
        if (!directory.exists()) directory.mkdirs();

        File outputFile = new File(outputDirPath + "/encounters_with_status.json");
        try (FileWriter file = new FileWriter(outputFile)) {
            file.write(outputArray.toString());
            System.out.println("✅ Encounters saved to: " + outputFile.getPath());
        }
    }
}
