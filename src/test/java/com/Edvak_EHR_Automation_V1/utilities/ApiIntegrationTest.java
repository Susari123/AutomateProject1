package com.Edvak_EHR_Automation_V1.utilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.Random;

public class ApiIntegrationTest {

    // A method to generate random proportions that sum to 1
    private double[] generateRandomProportions() {
        Random random = new Random();

        // Generate two random numbers, allowing negative values
        double a = random.nextDouble() * (random.nextBoolean() ? 1 : -1);
        double b = random.nextDouble() * (random.nextBoolean() ? 1 : -1);

        // Round the first two values to one decimal place
        double roundedA = roundToOneDecimal(a);
        double roundedB = roundToOneDecimal(b);

        // Calculate the third value to ensure the sum is 1
        double roundedC = roundToOneDecimal(1 - roundedA - roundedB);

        // Adjust for rounding errors (if the sum deviates from 1 after rounding)
        double sum = roundedA + roundedB + roundedC;
        if (sum != 1.0) {
            roundedC += 1.0 - sum; // Correct the third value to make the sum exactly 1
            roundedC = roundToOneDecimal(roundedC); // Ensure it's still one decimal place
        }

        // Return the final rounded values
        return new double[]{roundedA, roundedB, roundedC};
    }

    // A utility method to round to one decimal place and remove leading zero for values between -1 and 1
    private double roundToOneDecimal(double value) {
        double roundedValue = Math.round(value * 10) / 10.0; // Round to one decimal place
        if (roundedValue > -1 && roundedValue < 1) {
            return Double.parseDouble(String.format("%.1f", roundedValue).replace("0.", "."));
        }
        return roundedValue;
    }

    // A method that calls the API with the given claim number
    public void createSampleERA(String claimNumber) {
        // Generate random proportions
        double[] proportions = generateRandomProportions();
        double insPaidPer = proportions[0];
        double insAdjustPer = proportions[1];
        double patientResPer = proportions[2];

        // Print the generated values
        System.out.println("Random Values: insPaidPer=" + insPaidPer + ", insAdjustPer=" + insAdjustPer + ", patientResPer=" + patientResPer);

        // Make the API call
        RestAssured.baseURI = "https://darwinapi.edvak.com:3000";
        Response response = RestAssured
            .given()
            .header("Content-Type", "application/json")
            .queryParam("encounterNumber", claimNumber) // Pass the claim number dynamically
            .queryParam("p_id", "6630b50147b247c7c8ee8ea7")
            .queryParam("insPaidPer", insPaidPer)
            .queryParam("insAdjustPer", insAdjustPer)
            .queryParam("patientResPer", patientResPer)
            .when()
            .post("/era/create-sample-era")
            .then()
            .extract()
            .response();

        // Print the response and assert the status code
        System.out.println("Response: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200, "API call failed.");
    }
}
