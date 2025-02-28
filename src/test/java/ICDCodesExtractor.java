

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

public class ICDCodesExtractor {
    @Test
    public void testGetICDCodes() throws IOException {
        // Base URI
        RestAssured.baseURI = "https://darwinapi.edvak.com:3000";

        // Define search terms
        String[] searchTerms = {"fever", "typhoid", "cough", "pain"}; // Add more search terms as needed

        // Directory and File Setup
        String directoryPath = "src/test/resources/output";
        String outputFilePath = directoryPath + "/ICDCodes.json";

        // Create the directory if it doesn't exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("Directory created at: " + directoryPath);
            } else {
                System.out.println("Failed to create directory at: " + directoryPath);
            }
        }

        // StringBuilder to accumulate JSON content for all search terms
        StringBuilder jsonContent = new StringBuilder();
        jsonContent.append("[\n");

        for (String searchTerm : searchTerms) {
            // API Endpoint with the current search term
            String endpoint = "/addicdcodes/getAlladdicdcodesLists?page=1&pageSize=100&search=" + searchTerm
                    + "&age=23&gender=Female&chargeScreen=false";

            // Send GET Request with headers
            Response response = given()
                    .header("Authorization",
                            "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNjY2MmQ3NDc4NjBlOWM1NjIwMTU0NjA2IiwicF9pZCI6IjY2MzBiNTAxNDdiMjQ3YzdjOGVlOGVhNyIsImZpcnN0X25hbWUiOiJHRU9SR0UiLCJsYXN0X25hbWUiOiJERUxPU0EiLCJlbWFpbCI6Imdlb3JnZWVkZWxvc2FAZWhyLmNvbSIsImlhdCI6MTczMzk4NDEwNX0.FpKlw8letnEO-f8l78fUjeio96Ofu03lS943Wrb4CmU")
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json, text/plain, */*")
                    .header("Location_ID", "954f0ddb2bf9")
                    .header("Moment", "America/New_York")
                    .when()
                    .get(endpoint)
                    .then()
                    .extract()
                    .response();

            // Validate Status Code
            assertEquals(200, response.statusCode(), "Expected status code is not returned");

            // Extract the list of ICD codes
            List<String> icdCodes = response.jsonPath().getList("list.ICDCodes");
            List<String> descriptions = response.jsonPath().getList("list.Description");

            // Debug: Print extracted data
            System.out.println("Search Term: " + searchTerm + " - Extracted ICD Codes: " + icdCodes);
            System.out.println("Search Term: " + searchTerm + " - Extracted Descriptions: " + descriptions);

            // Check if data is invalid or empty
            if (icdCodes == null || descriptions == null || icdCodes.isEmpty() || descriptions.isEmpty()) {
                System.out.println("No valid ICD code data found for search term: " + searchTerm);
                continue; // Move to the next search term
            }

            // Prepare JSON content for the current search term
            for (int i = 0; i < icdCodes.size(); i++) {
                String icdCode = icdCodes.get(i) != null ? icdCodes.get(i) : "null";
                String description = descriptions.get(i) != null ? descriptions.get(i) : "null";

                jsonContent.append("  {\n");
                jsonContent.append("    \"ICDCodes\": \"").append(icdCode).append("\",\n");
                jsonContent.append("    \"Description\": \"").append(description).append("\"\n");
                jsonContent.append("  },\n");
            }
        }

        // Remove the last comma and close the JSON array
        int lastCommaIndex = jsonContent.lastIndexOf(",");
        if (lastCommaIndex != -1) {
            jsonContent.deleteCharAt(lastCommaIndex);
        }
        jsonContent.append("\n]");

        // Write JSON content to file
        try (FileWriter file = new FileWriter(outputFilePath)) {
            file.write(jsonContent.toString());
            System.out.println("ICD Codes saved to JSON file: " + outputFilePath);
        }
    }
}
