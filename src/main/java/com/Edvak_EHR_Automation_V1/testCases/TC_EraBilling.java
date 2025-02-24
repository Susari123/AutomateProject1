package com.Edvak_EHR_Automation_V1.testCases;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import com.Edvak_EHR_Automation_V1.pageObjects.BillingGenerateClaims;
import com.Edvak_EHR_Automation_V1.utilities.ApiIntegrationTest;
import com.Edvak_EHR_Automation_V1.utilities.DataReader;
import com.Edvak_EHR_Automation_V1.utilities.LoginUtils;
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
        try {
            // Initialize encounterClaimData
            initializeEncounterClaimData();
    
            // Check if encounterClaimData is empty
            if (encounterClaimData == null || encounterClaimData.isEmpty()) {
                logger.error(" Encounter Claim Data is empty. Cannot process claims.");
                return;
            }
    
            // Log in to the application using reusable login method
            LoginUtils.loginToApplication(driver, baseURL, "souravsusari311@gmail.com", "Edvak@3210");
    
            // Navigate to the Billing Page
            navigateToBillingPage();
    
            // Extract claim IDs for ERA processing
            String claimIds = extractClaimIdsForEraProcessing();
    
            // If claim IDs were found, create a Sample ERA
            if (!claimIds.isEmpty()) {
                createSampleEra(claimIds);
            } else {
                logger.warn(" No claim IDs found with status 'Awaiting to post' or 'Awaiting to transmit'.");
            }
    
        } catch (Exception e) {
            logger.error(" Error in EraReceived: ", e);
        }
    }
    private void navigateToBillingPage() {
        try {
            WebElement billingButton = driver.findElement(By.xpath("//nav/a[5]/span[1]/sl-icon"));
            clickWithRetry(billingButton, 3);
            logger.info(" Navigated to the Billing Page.");
        } catch (Exception e) {
            logger.error(" Failed to navigate to the Billing Page: ", e);
        }
    }

    private String extractClaimIdsForEraProcessing() {
        StringBuilder claimIdsBuilder = new StringBuilder();
        try {
            for (List<Map<String, String>> claimsList : encounterClaimData.values()) {
                for (Map<String, String> claimData : claimsList) {
                    String status = claimData.get("status").trim();
    
                    if ("Awaiting to post".equalsIgnoreCase(status) || "Awaiting to transmit".equalsIgnoreCase(status)) {
                        if (claimIdsBuilder.length() > 0) {
                            claimIdsBuilder.append(",");
                        }
                        claimIdsBuilder.append(claimData.get("claim_id"));
                    }
                }
            }
        } catch (Exception e) {
            logger.error(" Error extracting claim IDs for ERA processing: ", e);
        }
    
        String claimIds = claimIdsBuilder.toString();
        logger.info(" Total Claims for ERA Processing: " + (claimIds.isEmpty() ? "None" : claimIds));
        return claimIds;
    }
    
    private void createSampleEra(String claimIds) {
        try {
            logger.info("📡 Initiating Sample ERA API Call...");
            ApiIntegrationTest api = new ApiIntegrationTest();
            api.createSampleERA(claimIds);
            logger.info(" Sample ERA successfully created for Claim IDs: " + claimIds);
        } catch (Exception e) {
            logger.error(" Error in creating Sample ERA: ", e);
        }
    }
        
    private void initializeEncounterClaimData() {
        try {
            logger.info("Initializing Encounter Claim Data from JSON file: " + JSON_FILE_PATH);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File(JSON_FILE_PATH));
    
            // Validate JSON structure
            if (!rootNode.has("claim_presence_status") || !rootNode.get("claim_presence_status").isArray()) {
                logger.error("Invalid JSON structure: Missing or incorrect 'claim_presence_status' key.");
                return;
            }
    
            encounterClaimData = new HashMap<>();
    
            // Iterate over each entry in the claim_presence_status array
            for (JsonNode encounterNode : rootNode.get("claim_presence_status")) {
                // Validate "claim_id" in each encounter node
                if (!encounterNode.has("claim_id")) {
                    logger.warn("Skipping entry due to missing 'claim_id'.");
                    continue;
                }
    
                String encounterNumber = encounterNode.get("claim_id").asText();
                List<Map<String, String>> claimsList = new ArrayList<>();
    
                // Extract the claim's status from the node
                if (encounterNode.has("status")) {
                    Map<String, String> claimData = new HashMap<>();
                    claimData.put("claim_id", encounterNode.get("claim_id").asText());
                    claimData.put("status", encounterNode.get("status").asText());
                    claimsList.add(claimData);
                } else {
                    logger.warn("Skipping entry due to missing 'status'.");
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

            // Validate JSON structure
            if (!json.has("claim_presence_status") || !json.get("claim_presence_status").getClass().equals(JSONArray.class)) {
                logger.error("Invalid JSON format: 'claim_presence_status' key is missing or not an array.");
                return encounterClaimData;
            }

            JSONArray encounterPresenceArray = json.getJSONArray("claim_presence_status");

            for (int i = 0; i < encounterPresenceArray.length(); i++) {
                JSONObject encounterObject = encounterPresenceArray.getJSONObject(i);

                // Validate 'claim_id' presence
                if (!encounterObject.has("claim_id")) {
                    logger.warn("Skipping entry due to missing 'claim_id'.");
                    continue;
                }

                String encounterNumber = encounterObject.getString("claim_id");

                // Check if encounter is present before processing claims
                if (!encounterObject.has("is_present") || !encounterObject.getBoolean("is_present")) {
                    logger.warn("Skipping claim_id: " + encounterNumber + " as 'is_present' is false or missing.");
                    continue;
                }

                // Validate 'claims' array
                if (!encounterObject.has("claims") || !encounterObject.get("claims").getClass().equals(JSONArray.class)) {
                    logger.warn("No valid 'claims' array found for encounter number: " + encounterNumber);
                    continue;
                }

                JSONArray claimsArray = encounterObject.getJSONArray("claims");
                List<Map<String, String>> claimsList = new ArrayList<>();

                for (int j = 0; j < claimsArray.length(); j++) {
                    JSONObject claimObject = claimsArray.getJSONObject(j);

                    // Validate 'claim_id' and 'status' presence
                    if (!claimObject.has("claim_id") || !claimObject.has("status")) {
                        logger.warn("Skipping claim due to missing 'claim_id' or 'status'.");
                        continue;
                    }

                    Map<String, String> claimData = new HashMap<>();
                    claimData.put("claim_id", claimObject.getString("claim_id"));
                    claimData.put("status", claimObject.getString("status"));
                    claimsList.add(claimData);
                }

                encounterClaimData.put(encounterNumber, claimsList);
            }

            logger.info("Successfully loaded Encounter Claim Data. Total Encounters: " + encounterClaimData.size());
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
        
        // // Pass the retrieved EraID to the sendEraIdToApi method
        // sendEraIdToApi(EraID);
        // String Check = driver.findElement(By.xpath("//td[4]/div")).getText(); 
        // System.out.println(Check);
        // WebElement PayementTab = driver.findElement(By.xpath("//sl-tab-group//sl-tab[3]"));
        // PayementTab.click();
    //     Thread.sleep(2000);
    //     if (Check != null) {
    //         WebElement searchPayment = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//app-payments-list/ed-col/section/div[1]/form/div/div/input")));
    //         searchPayment.sendKeys(Check);  // Use the claim ID in the search box
    //         logger.info("Searching for Claim ID: " + Check);
    //     } else {
    //         logger.warn("No claim ID with status 'Awaiting to post' found in the JSON data.");
    //     }
    //     String createdBy =  wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//app-payments-list/ed-col/section/div[2]/table/tbody/tr[1]/td[9]/h6"))).getText();
    //     if ("System".equals(createdBy)) {
    //         System.out.println("The text matches 'System'.");
    //     } else {
    //         System.out.println("The text does not match 'System'.");
    //     }
    //     Thread.sleep(4000);
    //     wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr")));
    //     WebElement payment = driver.findElement(By.xpath("//table//tbody//tr"));
    // 	payment.click();
    // 	Thread.sleep(4000);
    // 	WebElement Transaction = driver.findElement(By.xpath("//sl-button[contains(text(), 'Transaction History')]"));
    // 	Transaction.click();
    // 	double amountReceived = Double.parseDouble(driver.findElement(By.xpath("//span[contains(text(), 'Amount Received:')]/following-sibling::strong")).getText().replaceAll("[^\\d.]", ""));
    //     double unappliedAmount = Double.parseDouble(driver.findElement(By.xpath("//span[contains(text(), 'Unapplied Amount:')]/following-sibling::strong")).getText().replaceAll("[^\\d.]", ""));
    //     double appliedAmount = Double.parseDouble(driver.findElement(By.xpath("//span[contains(text(), 'Applied Amount:')]/following-sibling::strong")).getText().replaceAll("[^\\d.]", ""));
    //     double refundAmount = Double.parseDouble(driver.findElement(By.xpath("//span[contains(text(), 'Refunded Amount:')]/following-sibling::strong")).getText().replaceAll("[^\\d.]", ""));
    //     logger.info("Amount Received (double)= " + amountReceived);
    //     logger.info("Unapplied Amount (double)= " + unappliedAmount);
    //     logger.info("Applied Amount (double)= " + appliedAmount);
    //     logger.info("Refund Amount (double)= " + refundAmount);
    //     if (amountReceived >= unappliedAmount) {
    //         logger.info("Amount Received is greater than or equal to Unapplied Amount.");
    //         double total = refundAmount + unappliedAmount + appliedAmount;
            
    //         if (amountReceived == total) {
    //             logger.info("Test Pass: The sum of Refund, Unapplied, and Applied equals Amount Received.");
    //         } else {
    //             logger.error("Test Fail: The sum of Refund, Unapplied, and Applied does NOT equal Amount Received.");
    //             logger.error("Calculated Total: " + total + ", but Amount Received is: " + amountReceived);
    //         }
    //     } else {
    //         logger.error("Test Fail: Amount Received is less than Unapplied Amount.");
    //     }       
    // 	   wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//app-transactions-payment/main/ed-drawer/ed-drawer-header/sl-icon-button")));
    // 	   Thread.sleep(3000);
    //     WebElement close = driver.findElement(By.xpath("//app-transactions-payment/main/ed-drawer/ed-drawer-header/sl-icon-button"));
    //     close.click();
    //     Thread.sleep(100);
    //     WebElement unapplyPaymentButton = driver.findElement(By.xpath("//sl-button[contains(text(), 'Unapply Payment')]"));
    //     String isDisabled = unapplyPaymentButton.getAttribute("disabled");
    //     if (appliedAmount == 0) {
    //         if (isDisabled != null) {
    //             logger.info("Unapply Payment button is disabled as the applied amount is 0.");
    //         } else {
    //             logger.error("Error: The button should be disabled, but it is not.");
    //         }
    //     } else {
    //         if (isDisabled == null) {
    //             logger.info("Unapply Payment button is enabled as the applied amount is greater than 0.");

    //             logger.info("Unapply Payment button clicked successfully.");
    //         } else {
    //             logger.error("Error: The button should be enabled, but it is disabled.");
    //         }
    //     }
        
    //     WebElement voidPaymentButton = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-payments/div/div[1]/div[4]/div[1]/div[2]/sl-tooltip[2]/sl-button"));
    //     String isDisabled1 = voidPaymentButton.getAttribute("disabled");
    //     if (refundAmount > 0) {
    //         if (isDisabled1 != null) {
    //             logger.info("Void Payment button is disabled as the refund amount is greater than 0.");
    //         } else {
    //             logger.error("Error: The button should be disabled, but it is not.");
    //         }
    //     } else {
    //         if (isDisabled1 == null) {
    //             logger.info("Void Payment button is enabled as the refund amount is 0.");

    //             logger.info("Void Payment button clicked successfully.");
    //         } else {
    //             logger.error("Error: The button should be enabled, but it is disabled.");
    //         }
    //     }
    //     Thread.sleep(2000);
    //     WebElement refundbutton = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-payments/div/div[1]/div[4]/div[2]/div[2]/sl-tooltip/sl-button"));
    //     String isDisabled2 = refundbutton.getAttribute("disabled");
    //     if (unappliedAmount == 0) {
    //         if (isDisabled2 != null) {
    //             logger.info("Button is disabled as the unapplied amount is 0.");
    //         } else {
    //             logger.error("Error: The button should be disabled, but it is not.");
    //         }
    //     } else {
    //         if (isDisabled2 == null) {
    //             logger.info("Button is enabled as the unapplied amount is greater than 0.");
    //         } else {
    //             logger.error("Error: The button should be enabled, but it is disabled.");
    //         }
    //     }
    //     WebElement transactionHistoryButton = driver.findElement(By.xpath("//sl-button[contains(text(), 'Transaction History')]"));
    //     if (transactionHistoryButton.isEnabled()) {
    //         logger.info("Transaction History button is enabled.");
    //         transactionHistoryButton.click();
    //         logger.info("Transaction History button clicked successfully.");
    //     } else {
    //         logger.warn("Transaction History button is disabled, cannot click.");
    //     }
    //     Thread.sleep(3000);
    //     boolean isTextFound = false;

    //  // Check for the "No transactions found" message
    //  List<WebElement> noTransactionsMessage = driver.findElements(By.xpath("//h6[contains(text(), 'No transactions found for this payment')]"));
    //  if (!noTransactionsMessage.isEmpty()) {
    //      System.out.println("No transactions found for this payment.");
    //  } else {
    //      // If "No transactions" message is not found, process the table
    //      List<WebElement> tables = driver.findElements(By.xpath("//ed-drawer/ed-drawer-body/div/table"));
    //      if (tables.isEmpty()) {
    //          System.out.println("Table is not present on the page. No transactions found.");
    //      } else {
    //          // Table exists, now check for rows and content
    //          try {
    //              List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//ed-drawer/ed-drawer-body/div/table/tbody/tr")));
                 
    //              for (WebElement row : rows) {
    //                  try {
    //                      WebElement column = row.findElement(By.xpath("./td[4]"));
    //                      String columnText = column.getText().trim();
                         
    //                      if ("System".equals(columnText)) {
    //                          System.out.println("Text 'System' found in one of the rows.");
    //                          isTextFound = true;
    //                          break;
    //                      }
    //                  } catch (NoSuchElementException e) {
    //                      // Column not found in this row, continue to the next row
    //                      continue;
    //                  }
    //              }
                 
    //              if (!isTextFound) {
    //                  System.out.println("Text 'System' not found in any row.");
    //              }
    //          } catch (Exception e) {
    //              System.out.println("Error occurred while processing the table: " + e.getMessage());
    //          }
    //      }
    //  }
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
