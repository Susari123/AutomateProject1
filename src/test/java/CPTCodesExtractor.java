

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

public class CPTCodesExtractor {
    @Test
    public void testGetCPTCodes() throws IOException {
        // Base URI
        RestAssured.baseURI = "https://darwinapi.edvak.com:3000";

        // Initialize variables for pagination
        int currentPage = 1; // Start from page 1
        int maxPages = 6; // Maximum page count

        // Directory and File Setup
        String directoryPath = "src/test/resources/output";
        String outputFilePath = directoryPath + "/CPTCodes.json";

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

        // StringBuilder to accumulate JSON content for all pages
        StringBuilder jsonContent = new StringBuilder();
        jsonContent.append("[\n");

        while (currentPage < maxPages) {
            // API Endpoint with the current page
            String endpoint = "/addcptcodes/getAlladdcptcodesLists?page=" + currentPage
                    + "&pageSize=100&search=&searchDes=";

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

            // Extract the list of CPT codes
            List<String> cptCodes = response.jsonPath().getList("list.CPTCode");
            List<String> descriptions = response.jsonPath().getList("list.Description");

            // Debug: Print extracted data
            System.out.println("Page " + currentPage + " - Extracted CPT Codes: " + cptCodes);
            System.out.println("Page " + currentPage + " - Extracted Descriptions: " + descriptions);

            // Check if data is invalid or empty and stop the loop
            if (cptCodes == null || descriptions == null || cptCodes.isEmpty() || descriptions.isEmpty()) {
                System.out.println("No valid CPT code data found on page " + currentPage);
                break; // Stop the loop
            }

            // Prepare JSON content for the current page
            for (int i = 0; i < cptCodes.size(); i++) {
                String cptCode = cptCodes.get(i) != null ? cptCodes.get(i) : "null";
                String description = descriptions.get(i) != null ? descriptions.get(i) : "null";

                jsonContent.append("  {\n");
                jsonContent.append("    \"CPTCode\": \"").append(cptCode).append("\",\n");
                jsonContent.append("    \"Description\": \"").append(description).append("\"\n");
                jsonContent.append("  },\n");
            }

            currentPage++;
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
            System.out.println("CPT Codes saved to JSON file: " + outputFilePath);
        }
    }
}
