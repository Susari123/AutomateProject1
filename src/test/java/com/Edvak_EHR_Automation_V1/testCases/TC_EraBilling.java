package com.Edvak_EHR_Automation_V1.testCases;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.Edvak_EHR_Automation_V1.pageObjects.BillingGenerateClaims;
import com.Edvak_EHR_Automation_V1.pageObjects.LoginPage;
import com.Edvak_EHR_Automation_V1.utilities.ApiIntegrationTest;
import com.Edvak_EHR_Automation_V1.utilities.DataReader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openqa.selenium.*;


import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import com.Edvak_EHR_Automation_V1.utilities.EncounterClaimStorage;


public class TC_EraBilling extends BaseClass{
	private static final String JSON_FILE_PATH = "C:\\Users\\sksusari\\Documents\\Test\\encounter_presence.json";
	private static Map<String, List<Map<String, String>>> encounterClaimData;
	DataReader dr = new DataReader();
    BillingGenerateClaims bi = new BillingGenerateClaims(driver);
    String encounterNumber ="";
   
    List<String> encounterNumbersList = new ArrayList<>();
//    
    @Test(priority = 1)
    public void EraREceived() throws InterruptedException {
    	
    	LoginPage lp = new LoginPage(driver);
        logger.info("********Test Starts Here********");
        driver.get(baseURL);
        driver.manage().window().maximize();
        
        lp.setUserName("souravsusari311@gmail.com");
        lp.setPassword("Admin@123456");

        WebElement loginButton = driver.findElement(By.xpath("/html/body/app-root/div/div/app-login/section/div/div/form/div[3]/sl-button"))
                                       .getShadowRoot().findElement(By.cssSelector("button"));
        new Actions(driver).moveToElement(loginButton).click().build().perform();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[normalize-space()='attach_money']")));
        Assert.assertTrue(driver.findElement(By.xpath("//header//h4[normalize-space()='dashboard']")).isDisplayed(), "Dashboard should be visible after login.");
        
        clickWithRetry(driver.findElement(By.xpath("//span[normalize-space()='attach_money']")), 3);
        logger.info("Billing button is clicked");
        Map<String, List<Map<String, String>>> encounterClaimData = loadEncounterClaimData();
        String claimId = null;
        for (List<Map<String, String>> claimsList : encounterClaimData.values()) {
            for (Map<String, String> claimData : claimsList) {
                if ("Awaiting to post".equals(claimData.get("status"))) {
                    claimId = claimData.get("claim_id");
                    break;
                }
            }
            if (claimId != null) {
                break;  // Stop searching once we find the first claim with "Awaiting to post"
            }
        }

        // Ensure that a claim ID with the desired status was found
        if (claimId != null) {
            ApiIntegrationTest api = new ApiIntegrationTest();
            api.createSampleERA(claimId);  // Pass the found claim ID into the API method
            logger.info("Sample ERA created with Claim ID: " + claimId + " (Status: Awaiting to post)");
        } else {
            logger.warn("No claim ID with status 'Awaiting to post' found in the JSON data.");
        }
    }
    public static Map<String, List<Map<String, String>>> loadEncounterClaimData() {
        encounterClaimData = new HashMap<>();

        try (FileReader reader = new FileReader(JSON_FILE_PATH)) {
            JSONObject json = new JSONObject(new JSONTokener(reader));
            JSONArray encounterPresenceArray = json.getJSONArray("encounter_presence_status");

            for (int i = 0; i < encounterPresenceArray.length(); i++) {
                JSONObject encounterObject = encounterPresenceArray.getJSONObject(i);
                String encounterNumber = encounterObject.getString("encounter_number");

                if (encounterObject.getBoolean("is_present")) {
                    JSONArray claimsArray = encounterObject.getJSONArray("claims");
                    List<Map<String, String>> claimsList = new ArrayList<>();

                    for (int j = 0; j < claimsArray.length(); j++) {
                        JSONObject claimObject = claimsArray.getJSONObject(j);
                        Map<String, String> claimData = new HashMap<>();
                        claimData.put("claim_id", claimObject.getString("claim_id"));
                        claimData.put("status", claimObject.getString("status"));
                        claimsList.add(claimData);
                    }
                    encounterClaimData.put(encounterNumber, claimsList);
                }
            }
        } catch (IOException e) {
            logger.error("Error reading JSON data from file.", e);
        }
		return encounterClaimData;
    }
    @Test(priority=1, dependsOnMethods = {"EraREceived"})
    public static void eRAClaim() throws InterruptedException {
        // Ensure encounterClaimData is loaded
        if (encounterClaimData == null) {
            loadEncounterClaimData(); // Load data if not already loaded
        }

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"tour-guide-billing-Step5\"]/td[1]/div/div/p")));
        
        WebElement eRaTab = driver.findElement(By.xpath("//sl-tab-group//sl-tab[4]"));
        eRaTab.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"sl-tab-panel-2\"]/app-era-list/ed-col/section/div/table/tbody")));
        
        Thread.sleep(2000);

        // Retrieve the first claim ID with status "Awaiting to post"
        String claimId = null;
        for (List<Map<String, String>> claimsList : encounterClaimData.values()) {
            for (Map<String, String> claimData : claimsList) {
                if ("Awaiting to post".equals(claimData.get("status"))) {
                    claimId = claimData.get("claim_id");
                    break;
                }
            }
            if (claimId != null) break;  // Exit once a match is found
        }

        if (claimId != null) {
            WebElement searchERA = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"sl-tab-panel-2\"]/app-era-list/ed-col/section/form/div/div/input")));
            searchERA.sendKeys(claimId);  // Use the claim ID in the search box
            logger.info("Searching for Claim ID: " + claimId);
        } else {
            logger.warn("No claim ID with status 'Awaiting to post' found in the JSON data.");
        }
        Thread.sleep(2000);
      String Check = driver.findElement(By.xpath("//td[4]/div")).getText(); 
      String StatusEra = driver.findElement(By.xpath("//td[2]/div/sl-badge")).getText(); 
      WebElement EraList = driver.findElement(By.xpath("//sl-tab-panel//tbody//tr[1]"));
      EraList.click();
      wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//img[contains(@src, 'loader.svg')]")));
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ed-drawer/ed-drawer-body/div[2]/div/input")));
      WebElement searchClaim = driver.findElement(By.xpath("//ed-drawer/ed-drawer-body/div[2]/div/input"));
      searchClaim.sendKeys(claimId);
      logger.info("Searched");
//      wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-era/ed-drawer/ed-drawer-body/div[3]/div/div[1]")));
      Thread.sleep(2000);
      String claimIdEra = driver.findElement(By.xpath("//ed-drawer-body/div[3]/div/div[2]/div[1]/div[1]/p[2]")).getText();
      System.out.println(claimIdEra);
      System.out.println(claimId);
      if (claimIdEra.equals(claimId)) {
    	    logger.info("ClaimId is present ");
    	} else {
    	    logger.info("Not found");
    	}
      if (StatusEra.equals("Awaiting to post")) {
    	    WebElement postPayment = driver.findElement(By.xpath("//ed-drawer/ed-drawer-footer/sl-button[1]"));  
    	    WebElement processPayment = driver.findElement(By.xpath("//ed-drawer/ed-drawer-footer/sl-button[2]"));
    	    WebElement cancel = driver.findElement(By.xpath("//ed-drawer/ed-drawer-footer/sl-button[3]"));
    	    
    	    // Locate and extract the check number
    	    WebElement checkNumberElement = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-era/ed-drawer/ed-drawer-header/div[1]/h6/span"));
    	    String fullText = checkNumberElement.getText().replace(" ", ""); 
    	    String checkNumber = null;
    	    Pattern pattern = Pattern.compile("Check#:(\\S+?)\\)?$"); 
    	    Matcher matcher = pattern.matcher(fullText);
    	    if (matcher.find()) {
    	        checkNumber = matcher.group(1);
    	    }

    	    if (checkNumber != null) {
    	        logger.info("Extracted Check Number: " + checkNumber);
    	    } else {
    	        logger.warn("Check number not found in text: " + fullText);
    	    }

    	    if (checkNumber.equals(Check)) {
    	        logger.info("Check Number is present.");
    	    } else {
    	        logger.info("Check number is not present.");
    	    }

    	    System.out.println(checkNumber);
    	    System.out.println(Check);

    	    // Validate that each button is present and displayed
    	    Assert.assertTrue(postPayment.isDisplayed(), "Post Payment button should be displayed.");
    	    Assert.assertTrue(processPayment.isDisplayed(), "Process Payment button should be displayed.");
    	    Assert.assertTrue(cancel.isDisplayed(), "Cancel button should be displayed.");

    	    // Click actions and further verifications for "Awaiting to post" status
    	    postPayment.click();
    	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ngb-modal-window/div")));
    	    String Post = driver.findElement(By.xpath("//ngb-modal-window/div/div/ed-drawer/ed-drawer-header/h6")).getText();
    	    logger.info(Post);
    	    WebElement CancelPost = driver.findElement(By.xpath("//ngb-modal-window//ed-drawer//ed-drawer-footer//sl-button[contains(text(), 'Cancel')]"));
    	    CancelPost.click();
    	    logger.info("Cancel button is clicked");

    	    processPayment.click();
    	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ngb-modal-window/div")));
    	    String process = driver.findElement(By.xpath("//ngb-modal-window/div/div/ed-drawer/ed-drawer-header/h6")).getText();
    	    logger.info(process);
    	    
    	    WebElement continuebutton = driver.findElement(By.xpath("//ngb-modal-window/div/div/ed-drawer/ed-drawer-footer/sl-button[1]"));
    	    continuebutton.click();
    	    wait.until(ExpectedConditions.not(ExpectedConditions.attributeContains(postPayment, "class", "button--loading")));
    	    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//img[contains(@src, 'loader.svg')]")));
    	    WebElement LinkButton = driver.findElement(By.xpath("//ed-drawer/ed-drawer-footer/sl-button[1]"));
    	    Assert.assertTrue(LinkButton.isDisplayed(), "Post Payment button should be displayed.");
    	    
//    	    WebElement CancelProcess = driver.findElement(By.xpath("//ngb-modal-window//ed-drawer//ed-drawer-footer//sl-button[contains(text(), 'Cancel')]"));
//    	    CancelProcess.click();
//    	    logger.info("Cancel button is clicked for process");
    	 

    	} else if (StatusEra.equals("Processed")) {
    	    // Verify that Post Payment and Process Payment buttons are disabled
    	    WebElement postPayment = driver.findElement(By.xpath("//ed-drawer/ed-drawer-footer/sl-button[1]"));  
    	    WebElement processPayment = driver.findElement(By.xpath("//ed-drawer/ed-drawer-footer/sl-button[2]"));

    	    if (!postPayment.isEnabled()) {
    	        logger.info("Post Payment button is disabled as expected for 'Processed' status.");
    	    } else {
    	        logger.warn("Post Payment button should be disabled for 'Processed' status, but it is enabled.");
    	    }

    	    if (!processPayment.isEnabled()) {
    	        logger.info("Process Payment button is disabled as expected for 'Processed' status.");
    	    } else {
    	        logger.warn("Process Payment button should be disabled for 'Processed' status, but it is enabled.");
    	    }

    	    // Assert that both buttons are disabled for the "Processed" status
    	    Assert.assertFalse(postPayment.isEnabled(), "Post Payment button should be disabled for 'Processed' status.");
    	    Assert.assertFalse(processPayment.isEnabled(), "Process Payment button should be disabled for 'Processed' status.");
    	}
      
    }
       
	public void processEncounterClaimMap(Map<String, String> encounterClaimMap) {
        for (Map.Entry<String, String> entry : encounterClaimMap.entrySet()) {
            String encounter = entry.getKey();
            String claimNumber = entry.getValue();
            System.out.println("Encounter: " + encounter + ", Claim Number: " + claimNumber);

            // Use encounter and claimNumber as needed
        }
    }
	public void clickWithRetry(WebElement element, int maxRetries) {
        int retryCount = 0;
        boolean clicked = false;
        while (retryCount < maxRetries && !clicked) {
            try {
                element.click();
                clicked = true;
            } catch (ElementClickInterceptedException e) {
                logger.info("Attempt " + (retryCount + 1) + ": Element click intercepted. Retrying...");
                retryCount++;
            }
        }
        // Assertion to ensure element was clicked
//        Assert.assertTrue(clicked, "Element should be clicked within retry limit.");
    }
}


