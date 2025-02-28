
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

public class Patientfirstname {
    @Test
    public void testGetPatientdata() throws IOException {
        // Base URI
        RestAssured.baseURI = "https://darwinapi.edvak.com:3000";

        // Initialize variables for pagination
        int currentPage = 0;
        int maxPages = 6;

        // Directory and File Setup
        String directoryPath = "src/test/resources/output";
        String outputFilePath = directoryPath + "/PatientDetails.json";

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
            String endpoint = "/patients/patientsList?page=" + currentPage + "&pageSize=20&searchTerm=&status=false";

            // Send GET Request with headers
            Response response = given()
                    .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNjYzMGI1MDE0N2IyNDdjN2M4ZWU4ZWE3IiwicF9pZCI6IjY2MzBiNTAxNDdiMjQ3YzdjOGVlOGVhNyIsImZpcnN0X25hbWUiOiJSb2JlcnQiLCJsYXN0X25hbWUiOiJXaWxsaWFtc1R3byIsImVtYWlsIjoic291cmF2c3VzYXJpMzExQGdtYWlsLmNvbSIsImlhdCI6MTczMzIxNjM2NX0.-vtVXqoa4wc4OyYOC5nlQF6e-RWoKh91oFlx2X3kJRU")
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json, text/plain, */*")
                    .header("Location_ID", "e324cebe6f29")
                    .header("Moment", "America/Chicago")
                    .when()
                    .get(endpoint)
                    .then()
                    .extract()
                    .response();

            // Validate Status Code
            assertEquals(200, response.statusCode(), "Expected status code is not returned");

            // Extract the list of patients (second element in the "result" array)
            List<String> firstNames = response.jsonPath().getList("result[1].first_name");
            List<String> lastNames = response.jsonPath().getList("result[1].last_name");
            List<Boolean> selfPayStatuses = response.jsonPath().getList("result[1].selfPay");

            // Debug: Print extracted data
            System.out.println("Page " + currentPage + " - Extracted First Names: " + firstNames);
            System.out.println("Page " + currentPage + " - Extracted Last Names: " + lastNames);
            System.out.println("Page " + currentPage + " - Extracted SelfPay Statuses: " + selfPayStatuses);

            // Check if data is invalid or empty and stop the loop
            if (firstNames == null || lastNames == null || selfPayStatuses == null ||
                    firstNames.isEmpty() || lastNames.isEmpty() || selfPayStatuses.isEmpty()) {
                System.out.println("No valid patient data found on page " + currentPage);
                break; // Stop the loop
            }

            // Prepare JSON content for the current page
            for (int i = 0; i < firstNames.size(); i++) {
                String firstName = firstNames.get(i) != null ? firstNames.get(i) : "null";
                String lastName = lastNames.get(i) != null ? lastNames.get(i) : "null";
                Boolean selfPay = selfPayStatuses.get(i) != null ? selfPayStatuses.get(i) : false;

                // Check if the `insurances` field contains {"0": null}
                Boolean hasInsuranceField = false;
                List<Object> insurancesArray = response.jsonPath().getList("result[1][" + i + "].insurances");

                if (insurancesArray != null && !insurancesArray.isEmpty()) {
                    for (Object insurance : insurancesArray) {
                        if (insurance instanceof Map) {
                            Object nestedInsurance = ((Map<?, ?>) insurance).get("insurances");
                            if (nestedInsurance instanceof Map) {
                                hasInsuranceField = ((Map<?, ?>) nestedInsurance).containsKey("0");
                                // Check if key "0" exists and its value is null
                                if (hasInsuranceField) {
                                    hasInsuranceField = ((Map<?, ?>) nestedInsurance).get("0") != null;
                                }
                            }
                        }
                    }
                }

                jsonContent.append("  {\n");
                jsonContent.append("    \"first_name\": \"").append(firstName).append("\",\n");
                jsonContent.append("    \"last_name\": \"").append(lastName).append("\",\n");
                jsonContent.append("    \"selfPay\": ").append(selfPay).append(",\n");
                jsonContent.append("    \"hasInsuranceField\": ").append(hasInsuranceField).append("\n");
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
            System.out.println("Patient details saved to JSON file: " + outputFilePath);
        }
    }
}
