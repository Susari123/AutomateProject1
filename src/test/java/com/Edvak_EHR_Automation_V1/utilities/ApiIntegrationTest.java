package com.Edvak_EHR_Automation_V1.utilities;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ApiIntegrationTest {

    @Test
    public void createSampleERA(String dynamicEncounterNumber) {
        RestAssured.baseURI = "https://darwinapi.edvak.com:3000";
        Response response = RestAssured
            .given()
            .header("Content-Type", "application/json")  
            .queryParam("encounterNumber", dynamicEncounterNumber) 
            .queryParam("p_id", "6630b50147b247c7c8ee8ea7")
            .queryParam("insPaidPer", ".2")
            .queryParam("insAdjustPer", ".3")
            .queryParam("patientResPer", ".5")
            .when()
            .post("/era/create-sample-era")  
            .then()
            .extract()
            .response();
        
        System.out.println("Response: " + response.asString());
        Assert.assertEquals(response.getStatusCode(), 200, "API call failed.");
    }



}