

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class PaginatedAPIDataExtractor {
    @Test
    public void testFetchAllClaimsData() throws IOException {
        // Base URI
        RestAssured.baseURI = "https://darwinapi.edvak.com:3000";

        // API Endpoint
        String endpoint = "/billing/getclaims";

        // Directory and File Setup
        String directoryPath = "src/test/resources/output";
        String outputFilePath = directoryPath + "/AllClaimsData.json";

        // Create the directory if it doesn't exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("Directory created at: " + directoryPath);
            } else {
                System.err.println("Failed to create directory at: " + directoryPath);
                return;
            }
        }

        // Headers setup
        String authorizationToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNjYzMGI1MDE0N2IyNDdjN2M4ZWU4ZWE3IiwicF9pZCI6IjY2MzBiNTAxNDdiMjQ3YzdjOGVlOGVhNyIsImZpcnN0X25hbWUiOiJSb2JlcnQiLCJsYXN0X25hbWUiOiJXaWxsaWFtc1R3byIsImVtYWlsIjoic291cmF2c3VzYXJpMzExQGdtYWlsLmNvbSIsImlhdCI6MTczNDMyOTM1NH0.4kLZejzPlMXrFF4h-opZNXIMS6Qp3mRaZsde0KMpJVA"; // Replace with a valid token

        // Request Body Template
        JSONObject requestBody = new JSONObject();
        requestBody.put("limit", 20);
        requestBody.put("offset", 0);

        // Variables to aggregate data
        JSONArray allClaimsData = new JSONArray();
        boolean hasMoreData = true;

        // Fetch data in pages
        while (hasMoreData) {
            // Send POST Request with headers and body
            Response response = RestAssured.given()
                    .header("Authorization", authorizationToken)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json, text/plain, */*")
                    .header("Location_ID", "954f0ddb2bf9")
                    .header("Moment", "America/New_York")
                    .header("userid", "6630b50147b247c7c8ee8ea7")
                    .body(requestBody.toString())
                    .when()
                    .post(endpoint)
                    .then()
                    .extract()
                    .response();

            // Debugging Information
            System.out.println("API Request Offset: " + requestBody.getInt("offset"));
            System.out.println("API Response Code: " + response.getStatusCode());

            if (response.getStatusCode() != 200) {
                // Log error details and fail the test
                System.err.println("Error: API call failed with status code " + response.getStatusCode());
                System.err.println("Response Body: " + response.getBody().asString());
                throw new RuntimeException("API call failed! Expected 200 but got " + response.getStatusCode());
            }

            // Parse the response into JSONArray
            JSONArray responseData = new JSONArray(response.getBody().asString());

            // Append to the aggregated data
            for (int i = 0; i < responseData.length(); i++) {
                allClaimsData.put(responseData.getJSONObject(i));
            }

            // Check if the response contains fewer records than the limit
            if (responseData.length() < 20) {
                hasMoreData = false; // Stop pagination
            } else {
                // Update offset for the next page
                int currentOffset = requestBody.getInt("offset");
                requestBody.put("offset", currentOffset + 20);
            }

            System.out.println("Fetched " + responseData.length() + " records.");
        }

        // Write the aggregated data to a JSON file
        try (FileWriter file = new FileWriter(outputFilePath)) {
            file.write(allClaimsData.toString(4)); // Pretty-print JSON
            System.out.println("All claims data saved to JSON file: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("Error writing JSON data to file: " + e.getMessage());
        }
    }
}
