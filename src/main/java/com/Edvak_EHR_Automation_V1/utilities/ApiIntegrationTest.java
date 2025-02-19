package com.Edvak_EHR_Automation_V1.utilities;

import java.util.Random;

import org.testng.Assert;

import io.restassured.RestAssured;
import io.restassured.response.Response;
public class ApiIntegrationTest {
    private double[] generateRandomProportions() {
        Random random = new Random(); 
        double a = random.nextDouble() * (random.nextBoolean() ? 1 : -1);
        double b = random.nextDouble() * (random.nextBoolean() ? 1 : -1);     
        double roundedA = roundToOneDecimal(a);
        double roundedB = roundToOneDecimal(b);    
        double roundedC = roundToOneDecimal(1 - roundedA - roundedB);
        double sum = roundedA + roundedB + roundedC;
        if (sum != 1.0) {
            roundedC += 1.0 - sum; 
            roundedC = roundToOneDecimal(roundedC); 
        }
        return new double[]{roundedA, roundedB, roundedC};
    }  
    private double roundToOneDecimal(double value) {
        double roundedValue = Math.round(value * 10) / 10.0; 
        if (roundedValue > -1 && roundedValue < 1) {
            return Double.parseDouble(String.format("%.1f", roundedValue).replace("0.", "."));
        }
        return roundedValue;
    }
    public void createSampleERA(String claimNumber) {
        
        double[] proportions = generateRandomProportions();
        double insPaidPer = proportions[0];
        double insAdjustPer = proportions[1];
        double patientResPer = proportions[2];
        System.out.println("Random Values: insPaidPer=" + insPaidPer + ", insAdjustPer=" + insAdjustPer + ", patientResPer=" + patientResPer);     
        RestAssured.baseURI = "https://darwinapi.edvak.com:3000";
        Response response = RestAssured
            .given()
            .header("Content-Type", "application/json")
            .queryParam("encounterNumber", claimNumber) 
            .queryParam("p_id", "6630b50147b247c7c8ee8ea7")
            .queryParam("insPaidPer", insPaidPer)
            .queryParam("insAdjustPer", insAdjustPer)
            .queryParam("patientResPer", patientResPer)
            .when()
            .post("/era/create-sample-era")
            .then()
            .extract()
            .response();
        System.out.println("Response: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200, "API call failed.");
    }
}
