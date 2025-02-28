
import java.io.FileWriter;
import java.io.IOException;

import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.response.Response;

public class PracticeListData {
    @Test
    public void testExtractOTP() throws IOException {
        // Base URI
        RestAssured.baseURI = "https://ehr.edvak.com";

        // API Endpoint
        String endpoint = "/backend/practice/get-list";

        // Authorization Token
        String authToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJmaXJzdF9uYW1lIjoiRWR2YWsiLCJsYXN0X25hbWUiOiJNZWRpY2FsQ2VudGVyIiwiZW1haWwiOiJhZG1pbkBlaHIuY29tIiwiY29udGFjdCI6IjcxMzcxNDQzOTQiLCJpYXQiOjE3MzcwMDE1MTF9.uIB1dB7EvCrKUlUvnm6OjUO_En17yVuRKguKJDNC9Fs";

        // Send GET Request
        Response response = given()
                .header("Authorization", authToken)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json, text/plain, */*")
                .when()
                .get(endpoint)
                .then()
                .log().ifValidationFails()
                .extract()
                .response();

        // Validate Status Code
        assertEquals(200, response.statusCode(), "Expected status code is not returned");

        // Parse JSON Response
        String responseBody = response.asString();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Pretty-print JSON

        // Read JSON Response into JsonNode
        JsonNode rootNode = objectMapper.readTree(responseBody);

        // Print formatted JSON response
        String formattedJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        System.out.println("Full JSON Response:\n" + formattedJson);

        // Save formatted JSON to a file
        String jsonOutputPath = "src/test/resources/output/FullResponse.json";
        try (FileWriter jsonWriter = new FileWriter(jsonOutputPath)) {
            jsonWriter.write(formattedJson);
            System.out.println("Full JSON Response saved to file: " + jsonOutputPath);
        } catch (IOException e) {
            System.err.println("Error writing JSON response to file: " + e.getMessage());
            throw e;
        }

        // Search for the specific email and extract OTP
        String targetEmail = "edara@edvak.com";
        String otp = null;

        if (rootNode.isArray()) { // Assuming the response is a JSON array
            for (JsonNode node : rootNode) {
                if (node.has("email") && targetEmail.equals(node.get("email").asText())) {
                    otp = node.path("login_OTP").path("OTP").asText();
                    break;
                }
            }
        }

        if (otp != null) {
            System.out.println("OTP for email " + targetEmail + ": " + otp);
        } else {
            System.err.println("OTP not found for email: " + targetEmail);
        }

        // Save extracted OTP to a file
        String otpOutputPath = "src/test/resources/output/ExtractedOTP.txt";
        try (FileWriter otpWriter = new FileWriter(otpOutputPath)) {
            otpWriter.write("OTP for email " + targetEmail + ": " + otp);
            System.out.println("Extracted OTP saved to file: " + otpOutputPath);
        } catch (IOException e) {
            System.err.println("Error writing OTP to file: " + e.getMessage());
            throw e;
        }
    }
}
