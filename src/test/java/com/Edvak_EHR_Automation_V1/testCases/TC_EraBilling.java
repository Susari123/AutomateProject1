package com.Edvak_EHR_Automation_V1.testCases;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import com.Edvak_EHR_Automation_V1.pageObjects.BillingGenerateClaims;
import com.Edvak_EHR_Automation_V1.pageObjects.LoginPage;
import com.Edvak_EHR_Automation_V1.utilities.ApiIntegrationTest;
import com.Edvak_EHR_Automation_V1.utilities.DataReader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.gargoylesoftware.htmlunit.javascript.host.URL;
//import com.Edvak_EHR_Automation_V1.utilities.EncounterClaimStorage;


public class TC_EraBilling extends BaseClass {
    private static final String JSON_FILE_PATH = "C:\\Users\\sksusari\\Documents\\Test\\encounter_presence.json";
    private static Map<String, List<Map<String, String>>> encounterClaimData;
    DataReader dr = new DataReader();
    BillingGenerateClaims bi = new BillingGenerateClaims(driver);
    String encounterNumber = "";

    List<String> encounterNumbersList = new ArrayList<>();

    @Test(priority = 1)
    public void EraREceived() throws InterruptedException {
        initializeEncounterClaimData();  // Initialize encounterClaimData

        if (encounterClaimData == null || encounterClaimData.isEmpty()) {
            logger.error("Encounter Claim Data is null or empty. Cannot process claims.");
            return;
        }

        LoginPage lp = new LoginPage(driver);
        logger.info("******** Test Starts Here ********");
        driver.get(baseURL);
        driver.manage().window().maximize();

        lp.setUserName("souravsusari311@gmail.com");
        lp.setPassword("Edvak@321");

        WebElement loginButton = driver.findElement(By.xpath("/html/body/app-root/div/div/app-login/section/div/div/form/div[3]/sl-button"))
                                       .getShadowRoot().findElement(By.cssSelector("button"));
        new Actions(driver).moveToElement(loginButton).click().build().perform();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//nav/a[5]/span[1]/sl-icon")));

        clickWithRetry(driver.findElement(By.xpath("//nav/a[5]/span[1]/sl-icon")), 3);
        logger.info("Billing button is clicked");

        StringBuilder claimIdsBuilder = new StringBuilder();

        for (List<Map<String, String>> claimsList : encounterClaimData.values()) {
            for (Map<String, String> claimData : claimsList) {
                String status = claimData.get("status");
                if ("Awaiting to post".equals(status) || "Awaiting to transmit".equals(status)) {
                    if (claimIdsBuilder.length() > 0) {
                        claimIdsBuilder.append(",");
                    }
                    claimIdsBuilder.append(claimData.get("claim_id"));
                }
            }
        }

        String claimIds = claimIdsBuilder.toString();
        if (!claimIds.isEmpty()) {
            ApiIntegrationTest api = new ApiIntegrationTest();
            api.createSampleERA(claimIds);
            logger.info("Sample ERA created with Claim IDs: " + claimIds);
        } else {
            logger.warn("No claim IDs with status 'Awaiting to post' or 'Awaiting to transmit' found in the JSON data.");
        }
    }

    private void initializeEncounterClaimData() {
        try {
            logger.info("Initializing Encounter Claim Data from JSON file: " + JSON_FILE_PATH);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(JSON_FILE_PATH));
            
            // Check if rootNode contains the key "encounter_presence_status"
            if (rootNode.get("encounter_presence_status") == null) {
                logger.error("Invalid JSON structure: Missing 'encounter_presence_status' key.");
                return;
            }

            encounterClaimData = new HashMap<>();
            for (JsonNode encounterNode : rootNode.get("encounter_presence_status")) {
                if (!encounterNode.has("encounter_number")) {
                    logger.warn("Skipping entry due to missing 'encounter_number'.");
                    continue;
                }

                String encounterNumber = encounterNode.get("encounter_number").asText();
                List<Map<String, String>> claimsList = new ArrayList<>();

                if (encounterNode.has("claims")) {
                    for (JsonNode claimNode : encounterNode.get("claims")) {
                        if (!claimNode.has("claim_id") || !claimNode.has("status")) {
                            logger.warn("Skipping claim due to missing 'claim_id' or 'status'.");
                            continue;
                        }

                        Map<String, String> claimData = new HashMap<>();
                        claimData.put("claim_id", claimNode.get("claim_id").asText());
                        claimData.put("status", claimNode.get("status").asText());
                        claimsList.add(claimData);
                    }
                } else {
                    logger.warn("No 'claims' found for encounter number: " + encounterNumber);
                }

                encounterClaimData.put(encounterNumber, claimsList);
            }

            logger.info("Successfully initialized Encounter Claim Data. Total Encounters: " + encounterClaimData.size());
        } catch (IOException e) {
            logger.error("Failed to load encounter claim data from JSON file.", e);
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
    public static void eRAClaim() throws Exception {
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
        String EraID = driver.findElement(By.xpath("//app-era-list/ed-col/section/div/table/tbody/tr[1]/td[3]/div/p")).getText();
        System.out.println("Retrieved EraID: " + EraID);
        
        // Pass the retrieved EraID to the sendEraIdToApi method
        sendEraIdToApi(EraID);
        String Check = driver.findElement(By.xpath("//td[4]/div")).getText(); 
        System.out.println(Check);
        WebElement PayementTab = driver.findElement(By.xpath("//sl-tab-group//sl-tab[3]"));
        PayementTab.click();
        Thread.sleep(2000);
        if (Check != null) {
            WebElement searchPayment = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//app-payments-list/ed-col/section/div[1]/form/div/div/input")));
            searchPayment.sendKeys(Check);  // Use the claim ID in the search box
            logger.info("Searching for Claim ID: " + Check);
        } else {
            logger.warn("No claim ID with status 'Awaiting to post' found in the JSON data.");
        }
        String createdBy =  wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//app-payments-list/ed-col/section/div[2]/table/tbody/tr[1]/td[9]/h6"))).getText();
        if ("System".equals(createdBy)) {
            System.out.println("The text matches 'System'.");
        } else {
            System.out.println("The text does not match 'System'.");
        }
        Thread.sleep(4000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr")));
        WebElement payment = driver.findElement(By.xpath("//table//tbody//tr"));
    	payment.click();
    	Thread.sleep(4000);
    	WebElement Transaction = driver.findElement(By.xpath("//sl-button[contains(text(), 'Transaction History')]"));
    	Transaction.click();
    	double amountReceived = Double.parseDouble(driver.findElement(By.xpath("//span[contains(text(), 'Amount Received:')]/following-sibling::strong")).getText().replaceAll("[^\\d.]", ""));
        double unappliedAmount = Double.parseDouble(driver.findElement(By.xpath("//span[contains(text(), 'Unapplied Amount:')]/following-sibling::strong")).getText().replaceAll("[^\\d.]", ""));
        double appliedAmount = Double.parseDouble(driver.findElement(By.xpath("//span[contains(text(), 'Applied Amount:')]/following-sibling::strong")).getText().replaceAll("[^\\d.]", ""));
        double refundAmount = Double.parseDouble(driver.findElement(By.xpath("//span[contains(text(), 'Refunded Amount:')]/following-sibling::strong")).getText().replaceAll("[^\\d.]", ""));
        logger.info("Amount Received (double)= " + amountReceived);
        logger.info("Unapplied Amount (double)= " + unappliedAmount);
        logger.info("Applied Amount (double)= " + appliedAmount);
        logger.info("Refund Amount (double)= " + refundAmount);
        if (amountReceived >= unappliedAmount) {
            logger.info("Amount Received is greater than or equal to Unapplied Amount.");
            double total = refundAmount + unappliedAmount + appliedAmount;
            
            if (amountReceived == total) {
                logger.info("Test Pass: The sum of Refund, Unapplied, and Applied equals Amount Received.");
            } else {
                logger.error("Test Fail: The sum of Refund, Unapplied, and Applied does NOT equal Amount Received.");
                logger.error("Calculated Total: " + total + ", but Amount Received is: " + amountReceived);
            }
        } else {
            logger.error("Test Fail: Amount Received is less than Unapplied Amount.");
        }       
    	   wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//app-transactions-payment/main/ed-drawer/ed-drawer-header/sl-icon-button")));
    	   Thread.sleep(3000);
        WebElement close = driver.findElement(By.xpath("//app-transactions-payment/main/ed-drawer/ed-drawer-header/sl-icon-button"));
        close.click();
        Thread.sleep(100);
        WebElement unapplyPaymentButton = driver.findElement(By.xpath("//sl-button[contains(text(), 'Unapply Payment')]"));
        String isDisabled = unapplyPaymentButton.getAttribute("disabled");
        if (appliedAmount == 0) {
            if (isDisabled != null) {
                logger.info("Unapply Payment button is disabled as the applied amount is 0.");
            } else {
                logger.error("Error: The button should be disabled, but it is not.");
            }
        } else {
            if (isDisabled == null) {
                logger.info("Unapply Payment button is enabled as the applied amount is greater than 0.");

                logger.info("Unapply Payment button clicked successfully.");
            } else {
                logger.error("Error: The button should be enabled, but it is disabled.");
            }
        }
        
        WebElement voidPaymentButton = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-payments/div/div[1]/div[4]/div[1]/div[2]/sl-tooltip[2]/sl-button"));
        String isDisabled1 = voidPaymentButton.getAttribute("disabled");
        if (refundAmount > 0) {
            if (isDisabled1 != null) {
                logger.info("Void Payment button is disabled as the refund amount is greater than 0.");
            } else {
                logger.error("Error: The button should be disabled, but it is not.");
            }
        } else {
            if (isDisabled1 == null) {
                logger.info("Void Payment button is enabled as the refund amount is 0.");

                logger.info("Void Payment button clicked successfully.");
            } else {
                logger.error("Error: The button should be enabled, but it is disabled.");
            }
        }
        Thread.sleep(2000);
        WebElement refundbutton = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-payments/div/div[1]/div[4]/div[2]/div[2]/sl-tooltip/sl-button"));
        String isDisabled2 = refundbutton.getAttribute("disabled");
        if (unappliedAmount == 0) {
            if (isDisabled2 != null) {
                logger.info("Button is disabled as the unapplied amount is 0.");
            } else {
                logger.error("Error: The button should be disabled, but it is not.");
            }
        } else {
            if (isDisabled2 == null) {
                logger.info("Button is enabled as the unapplied amount is greater than 0.");
            } else {
                logger.error("Error: The button should be enabled, but it is disabled.");
            }
        }
        WebElement transactionHistoryButton = driver.findElement(By.xpath("//sl-button[contains(text(), 'Transaction History')]"));
        if (transactionHistoryButton.isEnabled()) {
            logger.info("Transaction History button is enabled.");
            transactionHistoryButton.click();
            logger.info("Transaction History button clicked successfully.");
        } else {
            logger.warn("Transaction History button is disabled, cannot click.");
        }
        Thread.sleep(3000);
        boolean isTextFound = false;

     // Check for the "No transactions found" message
     List<WebElement> noTransactionsMessage = driver.findElements(By.xpath("//h6[contains(text(), 'No transactions found for this payment')]"));
     if (!noTransactionsMessage.isEmpty()) {
         System.out.println("No transactions found for this payment.");
     } else {
         // If "No transactions" message is not found, process the table
         List<WebElement> tables = driver.findElements(By.xpath("//ed-drawer/ed-drawer-body/div/table"));
         if (tables.isEmpty()) {
             System.out.println("Table is not present on the page. No transactions found.");
         } else {
             // Table exists, now check for rows and content
             try {
                 List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//ed-drawer/ed-drawer-body/div/table/tbody/tr")));
                 
                 for (WebElement row : rows) {
                     try {
                         WebElement column = row.findElement(By.xpath("./td[4]"));
                         String columnText = column.getText().trim();
                         
                         if ("System".equals(columnText)) {
                             System.out.println("Text 'System' found in one of the rows.");
                             isTextFound = true;
                             break;
                         }
                     } catch (NoSuchElementException e) {
                         // Column not found in this row, continue to the next row
                         continue;
                     }
                 }
                 
                 if (!isTextFound) {
                     System.out.println("Text 'System' not found in any row.");
                 }
             } catch (Exception e) {
                 System.out.println("Error occurred while processing the table: " + e.getMessage());
             }
         }
     }
//      String Check = driver.findElement(By.xpath("//td[4]/div")).getText(); 
//      String StatusEra = driver.findElement(By.xpath("//td[2]/div/sl-badge")).getText(); 
//      WebElement EraList = driver.findElement(By.xpath("//sl-tab-panel//tbody//tr[1]"));
//      EraList.click();
//      wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//img[contains(@src, 'loader.svg')]")));
//      wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ed-drawer/ed-drawer-body/div[2]/div/input")));
//      WebElement searchClaim = driver.findElement(By.xpath("//ed-drawer/ed-drawer-body/div[2]/div/input"));
//      searchClaim.sendKeys(claimId);
//      logger.info("Searched");
////      wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-era/ed-drawer/ed-drawer-body/div[3]/div/div[1]")));
//      Thread.sleep(2000);
//      String claimIdEra = driver.findElement(By.xpath("//ed-drawer-body/div[3]/div/div[2]/div[1]/div[1]/p[2]")).getText();
//      System.out.println(claimIdEra);
//      System.out.println(claimId);
//      if (claimIdEra.equals(claimId)) {
//    	    logger.info("ClaimId is present ");
//    	} else {
//    	    logger.info("Not found");
//    	}
//      if (StatusEra.equals("Awaiting to post")) {
//    	    WebElement postPayment = driver.findElement(By.xpath("//ed-drawer/ed-drawer-footer/sl-button[1]"));  
//    	    WebElement processPayment = driver.findElement(By.xpath("//ed-drawer/ed-drawer-footer/sl-button[2]"));
//    	    WebElement cancel = driver.findElement(By.xpath("//ed-drawer/ed-drawer-footer/sl-button[3]"));
//    	    
//    	    // Locate and extract the check number
//    	    WebElement checkNumberElement = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-era/ed-drawer/ed-drawer/ed-drawer-body/div[1]/h5/span"));
//    	    String fullText = checkNumberElement.getText().replace(" ", ""); 
//    	    String checkNumber = null;
//    	    Pattern pattern = Pattern.compile("\\(#:(\\S+?)\\)");
//    	    Matcher matcher = pattern.matcher(fullText);
//    	    if (matcher.find()) {
//    	        checkNumber = matcher.group(1);
//    	    }
//
//    	    if (checkNumber != null) {
//    	        logger.info("Extracted Check Number: " + checkNumber);
//    	    } else {
//    	        logger.warn("Check number not found in text: " + fullText);
//    	    }
//
//    	    if (checkNumber.equals(Check)) {
//    	        logger.info("Check Number is present.");
//    	    } else {
//    	        logger.info("Check number is not present.");
//    	    }
//
//    	    System.out.println(checkNumber);
//    	    System.out.println(Check);
//
//    	    // Validate that each button is present and displayed
//    	    Assert.assertTrue(postPayment.isDisplayed(), "Post Payment button should be displayed.");
//    	    Assert.assertTrue(processPayment.isDisplayed(), "Process Payment button should be displayed.");
//    	    Assert.assertTrue(cancel.isDisplayed(), "Cancel button should be displayed.");
//
//    	    // Click actions and further verifications for "Awaiting to post" status
//    	    postPayment.click();
//    	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ngb-modal-window/div")));
//    	    String Post = driver.findElement(By.xpath("//ngb-modal-window/div/div/ed-drawer/ed-drawer-header/h6")).getText();
//    	    logger.info(Post);
//    	    WebElement CancelPost = driver.findElement(By.xpath("//ngb-modal-window//ed-drawer//ed-drawer-footer//sl-button[contains(text(), 'Cancel')]"));
//    	    CancelPost.click();
//    	    logger.info("Cancel button is clicked");
//
//    	    processPayment.click();
//    	    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ngb-modal-window/div")));
//    	    String process = driver.findElement(By.xpath("//ngb-modal-window/div/div/ed-drawer/ed-drawer-header/h6")).getText();
//    	    logger.info(process);
//    	    
//    	    WebElement continuebutton = driver.findElement(By.xpath("//ngb-modal-window/div/div/ed-drawer/ed-drawer-footer/sl-button[1]"));
//    	    continuebutton.click();
//    	    wait.until(ExpectedConditions.not(ExpectedConditions.attributeContains(postPayment, "class", "button--loading")));
//    	    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//img[contains(@src, 'loader.svg')]")));
//    	    WebElement LinkButton = driver.findElement(By.xpath("//ed-drawer/ed-drawer-footer/sl-button[1]"));
//    	    Assert.assertTrue(LinkButton.isDisplayed(), "Post Payment button should be displayed.");
//    	    
////    	    WebElement CancelProcess = driver.findElement(By.xpath("//ngb-modal-window//ed-drawer//ed-drawer-footer//sl-button[contains(text(), 'Cancel')]"));
////    	    CancelProcess.click();
////    	    logger.info("Cancel button is clicked for process");
//    	    wait.until(ExpectedConditions.not(ExpectedConditions.attributeContains(processPayment, "class", "button--loading")));
//    	    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//img[contains(@src, 'loader.svg')]")));
//    	    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//sl-button[contains(text(), ' Linked Payment')]")));
//    	    Thread.sleep(3000);
//    	    Map<String, String> buttonXpaths = new HashMap<>();
//            buttonXpaths.put("Linked Payment", "//sl-button[contains(text(), ' Linked Payment')]");
//            buttonXpaths.put("Cancel", "//sl-button[contains(text(), 'Cancel')]");
//            buttonXpaths.put("Print", "//ed-drawer-header/div[2]/sl-tooltip/button");
//            buttonXpaths.put("Cross", "//ed-drawer-header/div[2]/sl-icon-button");          
//
//            // Iterate over each ent,sdf''l'sdf 'sl 'ls' l'ls  ;f'sd vd ry in the map and assert presence
//            for (Map.Entry<String, String> entry : buttonXpaths.entrySet()) {
//                String buttonName = entry.getKey();
//                String xpath = entry.getValue();
//                try {
//                    WebElement button = driver.findElement(By.xpath(xpath));
//                    Assert.assertTrue(button.isDisplayed(), buttonName + " button is not displayed.");
//                    System.out.println(buttonName + " button is present.");
//                } catch (NoSuchElementException e) {
//                    Assert.fail(buttonName + " button is not present.");
//                }
//            }
//            String paymentId = null;
//
//            String paymentIdText = driver.findElement(By.xpath("//sl-button[contains(text(), ' Linked Payment')]")).getText();
//
//            // Correct regex to match the text inside the parentheses starting with #
//            Pattern pattern1 = Pattern.compile("#(\\w+)");
//            Matcher matcher1 = pattern1.matcher(paymentIdText);
//
//            if (matcher1.find()) {
//                // Extract and assign the payment ID
//                paymentId = matcher1.group(1); // group(1) contains the part inside the parentheses
//                System.out.println(paymentId);
//            } else {
//                System.out.println("Payment ID not found!");
//            }
//
//            System.out.println("Extracted Payment ID: " + paymentId);
//            WebElement paymentLinked = driver.findElement(By.xpath("//sl-button[contains(text(), ' Linked Payment')]"));
//            paymentLinked.click();   
//            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//img[contains(@src, 'loader.svg')]")));
//            Thread.sleep(2000);
//            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//app-payments/div/header")));
//            Map<String, String> buttoninPayment = new HashMap<>();
//            buttoninPayment.put("Auto Apply Payment", "//sl-button[contains(text(), ' Auto Apply Payment ')]");
//            buttoninPayment.put("Transaction History", "//sl-button[contains(text(), 'Transaction History')]");
//            buttoninPayment.put("Unapply Payment", "//sl-button[contains(text(), 'Unapply Payment')]");
//            buttoninPayment.put("Void Payment", "//sl-button[contains(text(), 'Void Payment')]");          
//            buttoninPayment.put("Refund", "//sl-button[contains(text(), ' Refund ')]");
//            buttoninPayment.put("Linked ERA", "//sl-button[contains(text(), 'Linked ERA')]");
//            buttoninPayment.put("Create Task", "//sl-button[contains(text(), 'Create Task')]");
//            buttoninPayment.put("Select Claim", "//sl-button[contains(text(), 'Select Claim')]");
//            // Iterate over each entry in the map and assert presence
//            for (Map.Entry<String, String> entry : buttoninPayment.entrySet()) {
//                String buttonName = entry.getKey();
//                String xpath = entry.getValue();
//                try {
//                    WebElement button = driver.findElement(By.xpath(xpath));
//                    Assert.assertTrue(button.isDisplayed(), buttonName + " button is not displayed.");
//                    System.out.println(buttonName + " button is present.");
//                } catch (NoSuchElementException e) {
//                    Assert.fail(buttonName + " button is not present.");
//                }
//            }
//            String Payment_Id = driver.findElement(By.xpath("//h4[contains(text(), 'Payment #: ')]//span")).getText();
//            System.out.println(Payment_Id);
//            System.out.println(paymentId);
//            if (Payment_Id.trim().equals(paymentId.trim())) {
//                logger.info("\u001B[32mPayment id matched\u001B[0m");
//            } else {
//                logger.info("\u001B[31mPayment id not matched\u001B[0m");
//            }
//
//    	} else if (StatusEra.equals("Processed")) {
//    	    // Verify that Post Payment and Process Payment buttons are disabled
//    	    WebElement postPayment = driver.findElement(By.xpath("//ed-drawer/ed-drawer-footer/sl-button[1]"));  
//    	    WebElement processPayment = driver.findElement(By.xpath("//ed-drawer/ed-drawer-footer/sl-button[2]"));
//
//    	    if (!postPayment.isEnabled()) {
//    	        logger.info("Post Payment button is disabled as expected for 'Processed' status.");
//    	    } else {
//    	        logger.warn("Post Payment button should be disabled for 'Processed' status, but it is enabled.");
//    	    }
//
//    	    if (!processPayment.isEnabled()) {
//    	        logger.info("Process Payment button is disabled as expected for 'Processed' status.");
//    	    } else {
//    	        logger.warn("Process Payment button should be disabled for 'Processed' status, but it is enabled.");
//    	    }
//
//    	    // Assert that both buttons are disabled for the "Processed" status
//    	    Assert.assertFalse(postPayment.isEnabled(), "Post Payment button should be disabled for 'Processed' status.");
//    	    Assert.assertFalse(processPayment.isEnabled(), "Process Payment button should be disabled for 'Processed' status.");
//    	}
      
    }
    public static void sendEraIdToApi(String EraID) throws Exception {
        // Define the API endpoint
        String apiUrl = "https://darwinapi.edvak.com:3000/billing-payment-manage/auto-process-apply-ERA";

        // Create the API request payload with "ERAId" and "p_id"
        String requestBody = "{ \"p_id\": \"6630b50147b247c7c8ee8ea7\", \"ERAId\": \"" + EraID + "\" }";

        // Send the request to the API
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Configure the HTTP request
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("User-Agent", "PostmanRuntime/7.43.0");
        connection.setDoOutput(true);

        // Write the request body
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Get the response
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("API call successful. ERAId: " + EraID + " has been processed.");
        } else {
            System.out.println("API call failed with Response Code: " + responseCode);
        }

        // Close the connection
        connection.disconnect();
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


