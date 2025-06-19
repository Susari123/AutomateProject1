package com.Edvak_EHR_Automation_V1.testCases;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.Edvak_EHR_Automation_V1.pageObjects.BillingGenerateClaims;
import com.Edvak_EHR_Automation_V1.pageObjects.PaymentPage;
import com.Edvak_EHR_Automation_V1.service.BuildCombinedJson;
import com.Edvak_EHR_Automation_V1.service.ClaimIddata;
import com.Edvak_EHR_Automation_V1.service.Patientfirstname;
import com.Edvak_EHR_Automation_V1.service.SessionData;
import com.Edvak_EHR_Automation_V1.utilities.DateUtils;
import com.Edvak_EHR_Automation_V1.utilities.EncounterDataProvider;
import com.Edvak_EHR_Automation_V1.utilities.LoginUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;

import com.Edvak_EHR_Automation_V1.pageObjects.PaymentPage;
import com.Edvak_EHR_Automation_V1.service.SessionData;
import com.Edvak_EHR_Automation_V1.utilities.LoginUtils;

public class TC_Payment extends BaseClass {

    @Test(priority = 0)
    @SuppressWarnings("UseSpecificCatch")
public void testQuickRegistration() throws InterruptedException {
        logger.info("********Test Starts Here********");

        String userEmail = SessionData.getUserEmail();
        String userPassword = SessionData.getUserPassword();

        if (userEmail == null || userPassword == null) {
            logger.error("❌ Email/password null");
            throw new IllegalStateException("Missing credentials");
        }

        LoginUtils.loginToApplication(driver, baseURL, userEmail, userPassword);
        Thread.sleep(3000);  // <- if needed    
}


@Test(priority = 1, dataProvider = "combinedDataProvider", dependsOnMethods = {"testQuickRegistration"})
    public void payment(String encounterNumber, String status, String currentDate, String futureDate) throws InterruptedException {
        logger.info("💳 Starting payment processing...");

        WebElement billingIcon = getBillingIconElement();
        logger.info("Billing icon is displayed: " + billingIcon.isDisplayed());
        logger.info("Billing icon is enabled: " + billingIcon.isEnabled());
        clickWithRetry(billingIcon, 3);

        logger.info("✅ Billing button clicked");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr//td")));

        PaymentPage paymentPage = new PaymentPage(driver);
        paymentPage.clickPaymentTab();
        paymentPage.clickPlusButton();
        paymentPage.waitForNewPaymentHeader();

        paymentPage.enterSubmissionDate(currentDate);
        paymentPage.openPaymentTypeDropdown();

        if (status.equalsIgnoreCase("Statement Ready")) {
            if (paymentPage.isPatientPaymentOptionEnabled()) {
                paymentPage.selectPatientPaymentOption();
                paymentPage.searchAndSelectPatient("Radiousone Smith");
            }
        } else {
            if (paymentPage.isInsurancePaymentOptionEnabled()) {
                paymentPage.selectInsurancePaymentOption();
                Thread.sleep(3000);
                paymentPage.searchAndSelectInsurancePlan("CareCore National, Inc.");
                paymentPage.selectInsurancePlanResult();
            }
        }

        paymentPage.selectModeOfPayment("Cash");
         String randomAmount = TC_Payment.getRandomAmount();
        paymentPage.enterPaymentAmount(randomAmount);
        paymentPage.enterPaymentNotes("Test payment note");
        paymentPage.submitPayment();
        Thread.sleep(4000);
        paymentPage.waitForPaymentToAppear();
        paymentPage.openCreatedPayment();
        Thread.sleep(3000);

        String paymentType = paymentPage.getPaymentType();
    if (paymentType.equalsIgnoreCase("Insurance")) {
        TC_Payment.insurancePayment(encounterNumber);
    } else {
        TC_Payment.patientPayment(encounterNumber);
    }
    }

    // Retry click method with logging
    public void clickWithRetry(WebElement element, int retries) {
        int attempts = 0;
        while (attempts < retries) {
            try {
                element.click();
                logger.info("✅ Clicked successfully on attempt " + (attempts + 1));
                return;
            } catch (Exception e) {
                logger.warn("❌ Click failed on attempt " + (attempts + 1) + ": " + e.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }
            attempts++;
        }
        throw new RuntimeException("❌ All click attempts failed after " + retries + " tries.");
    }

    // Billing icon locator
    public WebElement getBillingIconElement() {
        return driver.findElement(By.xpath("//a[.//span[@data-title='Billing']]"));
    }

    @AfterMethod
    public void afterTestResult(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            logger.error("❌ Test failed: " + result.getName(), result.getThrowable());
            result.getThrowable().printStackTrace();
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            logger.info("✅ Test passed: " + result.getName());
        } else if (result.getStatus() == ITestResult.SKIP) {
            logger.warn("⚠️ Test skipped: " + result.getName());
        }
    }
    

    public static void insurancePayment(String encounterNumber) throws InterruptedException {
    	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
    	try {
            WebElement firstButton = driver.findElement(By.xpath("//sl-button[contains(text(),'Select Claim')]"));
            if (firstButton != null && firstButton.isDisplayed()) {
                firstButton.click();
                logger.info("First button clicked successfully.");
            }

        } catch (NoSuchElementException e) {
            try {
                WebElement secondButton = driver.findElement(By.xpath("//app-patient-payments/div/div[2]/div[1]/div[2]/sl-tooltip/sl-button"));
                secondButton.click();
                System.out.println("Second button clicked successfully.");
            } catch (NoSuchElementException secondException) {
            	logger.info("Both buttons are not found.");
            }
        }
    	logger.info("search claim");
    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='searchClaim']")));
        WebElement searchClaim = driver.findElement(By.xpath("//input[@formcontrolname='searchClaim']"));

    	logger.info("search claim");
        searchClaim.sendKeys(encounterNumber);
        logger.info("encounter number added");
    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ed-drawer/ed-drawer-body/div/div[2]/div/table/tbody/tr")));
    	Thread.sleep(5000);
    	WebElement claim = driver.findElement(By.xpath("//ed-drawer-body/div/div[2]/div/table/tbody/tr/td[1]/div/sl-checkbox"));
    	claim.click();
    	logger.info("claim is selected");
    	WebElement includeClaim = driver.findElement(By.xpath("//sl-button[contains(text(), 'Include Claims')]"));
    	includeClaim.click();
    	logger.info("claim selected");
    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h5[contains(text(),'Claim Info ')]")));
    	wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//h5[contains(text(),'Claim Info ')]/sl-tooltip/span")));
    	WebElement claimElement = driver.findElement(By.xpath("//h5[contains(text(),'Claim Info ')]/sl-tooltip/span"));
    	String claimID = claimElement.getText();
    	String trimmedClaimID = claimID.replace("#", "").trim();
    	logger.info("Encounter Number: " + encounterNumber);
    	logger.info("Trimmed Claim ID: " + trimmedClaimID);
    	if (trimmedClaimID.equals(encounterNumber)) {
    	    logger.info("Test pass, claim ID is the same as the selected claim ID");
    	} else {
    	    logger.info("Test fail, claim ID is not the same as the selected claim ID");
    	}
    	logger.info("Trimmed Claim ID: " + trimmedClaimID);
    	Thread.sleep(2000);
    	   double amountReceived = Double.parseDouble(driver.findElement(By.xpath("//span[contains(text(), 'Amount Received: ')]/following-sibling::strong")).getText().replaceAll("[^\\d.]", ""));
           double unappliedAmount = Double.parseDouble(driver.findElement(By.xpath("//span[contains(text(), 'Unapplied Amount: ')]/following-sibling::strong")).getText().replaceAll("[^\\d.]", ""));
           double appliedAmount = Double.parseDouble(driver.findElement(By.xpath("//span[contains(text(), 'Applied Amount: ')]/following-sibling::strong")).getText().replaceAll("[^\\d.]", ""));
           double refundAmount = Double.parseDouble(driver.findElement(By.xpath("//span[contains(text(), 'Refunded Amount: ')]/following-sibling::strong")).getText().replaceAll("[^\\d.]", ""));
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
           WebElement transactionHistoryButton = driver.findElement(By.xpath("//sl-button[contains(text(), 'Transaction History')]"));
           if (transactionHistoryButton.isEnabled()) {
               logger.info("Transaction History button is enabled.");
               transactionHistoryButton.click();
               logger.info("Transaction History button clicked successfully.");
           } else {
               logger.warn("Transaction History button is disabled, cannot click.");
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
           
           WebElement voidPaymentButton = driver.findElement(By.xpath("//sl-button[contains(text(),'Void Payment')]"));
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
           WebElement refundbutton = driver.findElement(By.xpath("//sl-button[contains(text(),'Refund')]"));
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
          
           double totalBilledAmount = Double.parseDouble(driver.findElement(By.xpath("//span[contains(text(), 'Total Billed Amount:')]/following-sibling::strong")).getText().replaceAll("[^\\d.]", ""));
           double totalAdjustAmount = Double.parseDouble(driver.findElement(By.xpath("//span[contains(text(), 'Total Adjusted Amount:')]/following-sibling::strong")).getText().replaceAll("[^\\d.]", ""));
           double insuranceBalance = Double.parseDouble(driver.findElement(By.xpath("//span[contains(text(), 'Insurance Balance:')]/following-sibling::strong")).getText().replaceAll("[^\\d.]", ""));
           double patientBalance = Double.parseDouble(driver.findElement(By.xpath("//span[contains(text(), 'Patient Balance:')]/following-sibling::strong")).getText().replaceAll("[^\\d.]", ""));
           wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//app-patient-payments/div/div[2]/div[2]/div[2]/table/tbody/tr")));
        List<WebElement> rows = driver.findElements(By.xpath("//app-patient-payments/div/div[2]/div[2]/div[2]/table/tbody/tr"));
        double totalSum = 0.0;
        for (WebElement row : rows) {
            String cellValue = row.findElement(By.xpath("./td[6]")).getText();
            String cleanedValue = cellValue.replaceAll("[^\\d.]", ""); 
            if (!cleanedValue.isEmpty()) {
                double value = Double.parseDouble(cleanedValue);
                totalSum += value;
            }
        }
        System.out.println("Total Sum of Claim Balances in td[6]: " + totalSum);
        double totalbill = 0.0;
        for (WebElement row : rows) {
            String cellValue = row.findElement(By.xpath("./td[3]")).getText();
            String cleanedValue = cellValue.replaceAll("[^\\d.]", ""); 
            if (!cleanedValue.isEmpty()) {
                double value = Double.parseDouble(cleanedValue);
                totalbill += value;
            }
        }
        System.out.println("Total billed of Claim Balances in td[6]: " + totalbill);
           @SuppressWarnings("unused")
        double ClaimAdjust = Double.parseDouble(driver.findElement(By.xpath("//app-patient-payments/div/div[2]/div[2]/div[2]/table/tbody/tr/td[5]")).getText().replaceAll("[^\\d.]", ""));
           double totaladjusted = 0.0;
           for (WebElement row : rows) {
               String cellValue = row.findElement(By.xpath("./td[5]")).getText();
               String cleanedValue = cellValue.replaceAll("[^\\d.]", ""); 
               if (!cleanedValue.isEmpty()) {
                   double value = Double.parseDouble(cleanedValue);
                   totaladjusted += value;
               }
           }
           System.out.println("Total totaladjusted of Claim Balances in td[5]: " + totaladjusted);
         
           double expectedClaimBalance = insuranceBalance + patientBalance;
           Assert.assertEquals(totalSum, expectedClaimBalance, "Test Fail: The claim balance does not match the sum of insurance balance and patient balance.");
           logger.info("Test Pass: The claim balance matches the sum of insurance balance and patient balance.");
           logger.info("Payment Test Completed as all the Test is pass");
           Assert.assertEquals(totalBilledAmount, totalbill, "Test Fail: Total Billed Amount does not match the Claim Billed amount.");
           logger.info("Test Pass: Total Billed Amount matches the Claim Billed amount.");
           Assert.assertEquals(totalAdjustAmount, totaladjusted, "Test Fail: Total Adjust Amount does not match Claim Adjust.");
           Map<String, String> buttoninPayment = new HashMap<>();
           buttoninPayment.put("Refund", "//sl-button[contains(text(), 'Refund')]");
           buttoninPayment.put("Transaction History", "//sl-button[contains(text(), 'Transaction History')]");
           buttoninPayment.put("Unapply Payment", "//sl-button[contains(text(), 'Unapply Payment')]");
           buttoninPayment.put("Void Payment", "//sl-button[contains(text(), 'Void Payment')]");          
           buttoninPayment.put("Create Task", "//sl-button[contains(text(), 'Create Task')]");
           buttoninPayment.put("Select Claim", "//sl-button[contains(text(), 'Select Claim')]");
           // Iterate over each entry in the map and assert presence
           for (Map.Entry<String, String> entry : buttoninPayment.entrySet()) {
               String buttonName = entry.getKey();
               String xpath = entry.getValue();
               try {
                   WebElement button = driver.findElement(By.xpath(xpath));
                   Assert.assertTrue(button.isDisplayed(), buttonName + " button is not displayed.");
                   System.out.println(buttonName + " button is present.");
               } catch (NoSuchElementException e) {
                   Assert.fail(buttonName + " button is not present.");
               }
           }
           logger.info("Test Pass: Total Adjust Amount matches Claim Adjust.");
           logger.info("Test pass as all the senerio is completed.");
           WebElement serviceLine = driver.findElement(By.xpath("//app-patient-payments/div/div[2]/div[2]/div[2]/table/tbody/tr[1]")); 
           serviceLine.click();
           wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h5[contains(text(), 'Payment Allocation')]"))); 
           double insuranceServiceBalance = Double.parseDouble(driver.findElement(By.xpath("//app-adjust-apply-service-line/div/ed-drawer[1]/ed-drawer-body/section[2]/ed-row[3]/strong")).getText().replaceAll("[^\\d.]", ""));
           PaymentPage paymentPage = new PaymentPage(driver);
           if (insuranceServiceBalance == 0) {
               System.out.println("Insurance Balance is 0. Nothing to split.");
               return;
           }
       
           // Split the balance into 35%, 35%, and 30%
           double part1 = Math.round(insuranceServiceBalance * 0.15 * 100.0) / 100.0;
           double part2 = Math.round(insuranceServiceBalance * 0.55 * 100.0) / 100.0;
           double part3 = Math.round(insuranceServiceBalance * 0.30 * 100.0) / 100.0;
       
           System.out.println("Insurance Balance: " + insuranceServiceBalance);
           System.out.println("35% Part 1: " + part1);
           System.out.println("35% Part 2: " + part2);
           System.out.println("30% Part 3: " + part3);
        System.out.println("Insurance Balance: " + insuranceServiceBalance);
        System.out.println("Unapplied Amount: " + unappliedAmount);
           paymentPage.clickadjustTransfterinsu();
           paymentPage.enterAmountToAajust(part1);
           paymentPage.selectAdjustmentType();
           paymentPage.enterTransferAmount(part2);
           paymentPage.clickSaveButton();
           Thread.sleep(4000);
           if (part3 > unappliedAmount && unappliedAmount > 0) {
            paymentPage.enterAmountToApply(unappliedAmount);
            System.out.println("Applied Unapplied Amount: " + unappliedAmount);
            paymentPage.clickApplyButton();
        } else if (part3 < unappliedAmount && part3 > 0) {
            paymentPage.enterAmountToApply(part3);
            System.out.println("Applied Insurance Balance Amount (30% Part3): " + part3);
            paymentPage.clickApplyButton();
        } else if (part3 == 0 || unappliedAmount == 0) {
            System.out.println("No amount to apply. Skipping...");
        } else {
            System.out.println("Unexpected Condition – Check Balances.");
        }
        Thread.sleep(10000);
        double patientBalanceAmount = paymentPage.getPatientBalanceAmount();

        double patietnAdjust = Math.round((patientBalanceAmount / 2) * 100.0) / 100.0;
        double PatientApplied = patientBalanceAmount - part1;

        System.out.println("Patient Balance Amount: " + patientBalanceAmount);
        System.out.println("Part 1: " + patietnAdjust);
        System.out.println("Parrt 2: " + PatientApplied);
        paymentPage.clickpatientAdjust();
        paymentPage.enterAmountToAajust(patietnAdjust);

        paymentPage.selectAdjustmentType();
        WebElement SaveButton = driver.findElement(By.xpath("//app-adjust-patient-balance/ed-drawer/ed-drawer-footer/sl-button[1]"));
        SaveButton.click();
        // paymentPage.clickSaveButton();
        Thread.sleep(4000);
        paymentPage.clickPatientPayment();
        paymentPage.applyLesserAmount(PatientApplied);

    }
    public static void patientPayment(String encounterNumber) throws InterruptedException {
            @SuppressWarnings("unused")
    	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
    	try {
            WebElement firstButton = driver.findElement(By.xpath("//html/body/app-root/div/div[2]/app-patient-payments/div/div[2]/div[1]/div/table/tbody/tr/td/div/sl-tooltip/sl-button"));            
            if (firstButton != null && firstButton.isDisplayed()) {
                firstButton.click();
                System.out.println("First button clicked successfully.");
            }

        } catch (NoSuchElementException e) {
            try {
                WebElement secondButton = driver.findElement(By.xpath("//app-patient-payments/div/div[2]/div[1]/div[2]/sl-tooltip/sl-button"));
                secondButton.click();
                System.out.println("Second button clicked successfully.");
            } catch (NoSuchElementException secondException) {
                System.out.println("Both buttons are not found.");
            }
        }
        WebElement searchClaim = driver.findElement(By.xpath("//input[@formcontrolname='searchClaim']"));
        searchClaim.sendKeys(encounterNumber);

    }
    @DataProvider(name = "encounterDataProvider")
public Object[][] encounterDataProvider() throws Exception {
    EncounterDataProvider reader = new EncounterDataProvider();
    List<EncounterDataProvider.EncounterData> encounterDataList = reader.readEncounterDataFromJson();
    Object[][] data = new Object[encounterDataList.size()][2];

    for (int i = 0; i < encounterDataList.size(); i++) {
        data[i][0] = encounterDataList.get(i).getEncounterNumber();
        data[i][1] = encounterDataList.get(i).getStatus();
    }

    return data;
}
    @DataProvider(name = "dateDataProvider")
    public Object[][] dateDataProvider() {
    	 String[] dates = DateUtils.getCurrentAndPreviousDate();
        return new Object[][] {
            { dates[0], dates[1] }  
        };
    }
    @DataProvider(name = "combinedDataProvider")
public Object[][] combinedDataProvider() throws Exception {
    Object[][] encounterData = encounterDataProvider();
    Object[][] dateData = dateDataProvider(); // You must ensure this is valid
    Object[][] combinedData = new Object[encounterData.length][4];

    for (int i = 0; i < encounterData.length; i++) {
        combinedData[i][0] = encounterData[i][0];
        combinedData[i][1] = encounterData[i][1];
        combinedData[i][2] = dateData[0][0];
        combinedData[i][3] = dateData[0][1];
    }

    return combinedData;
}

    public static String getRandomAmount() {
        Random rand = new Random();
        double randomAmount = 100 + (500 - 100) * rand.nextDouble();
        boolean addDecimals = rand.nextBoolean();  
        if (addDecimals) {        
            DecimalFormat df = new DecimalFormat("#.00");
            return df.format(randomAmount);
        } else {       
            return String.valueOf((int) randomAmount);
        }
    }
       @SuppressWarnings("unused")
    private void deleteJsonFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("JSON file deleted successfully: " + file.getName());
            } else {
                System.out.println("Failed to delete the JSON file.");
            }
        } else {
            System.out.println("JSON file not found.");
        }
    }

}



