
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

public class CPTModifiersExtractor {
    @Test
    public void testGetAllCPTModifiers() throws IOException {
        // Base URI
        RestAssured.baseURI = "https://darwinapi.edvak.com:3000";

        // API Endpoint
        String endpoint = "/billing/getAllcptModifiersLists";

        // Directory and File Setup
        String directoryPath = "src/test/resources/output";
        String outputFilePath = directoryPath + "/AllCPTModifiers.json";

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

        // Print raw response for debugging
        System.out.println("Response Body: " + response.getBody().asString());

        // Validate Status Code
        assertEquals(200, response.statusCode(), "Expected status code is not returned");

        // Extract the list of CPT modifiers and descriptions
        List<String> cptModifiers = response.jsonPath().getList("CPTModifier");
        List<String> cptDescriptions = response.jsonPath().getList("CPTDescription");

        // Debug: Print extracted data
        System.out.println("Extracted CPT Modifiers: " + cptModifiers);
        System.out.println("Extracted CPT Descriptions: " + cptDescriptions);

        // Validate that data exists
        if (cptModifiers == null || cptDescriptions == null || cptModifiers.isEmpty() || cptDescriptions.isEmpty()) {
            System.out.println("No valid CPT modifier data found!");
            return;
        }

        // Prepare JSON content
        StringBuilder jsonContent = new StringBuilder();
        jsonContent.append("[\n");
        for (int i = 0; i < cptModifiers.size(); i++) {
            String cptModifier = cptModifiers.get(i) != null ? cptModifiers.get(i) : "null";
            String cptDescription = cptDescriptions.get(i) != null ? cptDescriptions.get(i) : "null";

            jsonContent.append("  {\n");
            jsonContent.append("    \"CPTModifier\": \"").append(cptModifier).append("\",\n");
            jsonContent.append("    \"CPTDescription\": \"").append(cptDescription).append("\"\n");
            jsonContent.append("  }");
            if (i < cptModifiers.size() - 1) {
                jsonContent.append(",\n");
            }
        }
        jsonContent.append("\n]");

        // Write JSON content to file
        try (FileWriter file = new FileWriter(outputFilePath)) {
            file.write(jsonContent.toString());
            System.out.println("CPT Modifier data saved to JSON file: " + outputFilePath);
        }
    }
}
