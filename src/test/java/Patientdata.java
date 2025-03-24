import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

public class Patientdata {

    public void fetchAndSavePatientData(String authToken, String locationId) throws IOException {
        // Base URI
        RestAssured.baseURI = "https://darwinapi.edvak.com:3000";

        String endpoint = "/patients/patientsList?page=0&pageSize=20&searchTerm=&status=false";

        // Send GET Request with headers
        Response response = given()
                .header("Authorization", "Bearer " + authToken)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json, text/plain, */*")
                .header("Location_ID", locationId)
                .header("Moment", "America/New_York")
                .when()
                .get(endpoint)
                .then()
                .log().all()
                .extract()
                .response();

        // Validate response status
        if (response.statusCode() != 200) {
            throw new RuntimeException("Failed to fetch patient data. Status Code: " + response.statusCode());
        }

        String directoryPath = "src/test/resources/output";
        String outputFilePath = directoryPath + "/PatientData.js";

        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String responseBody = response.asString();
        String jsContent = "const practiceSettingResponse = " + responseBody + ";\nexport default practiceSettingResponse;";

        try (FileWriter file = new FileWriter(outputFilePath)) {
            file.write(jsContent);
            System.out.println("✅ Patient data saved to: " + outputFilePath);
        }
    }
}
