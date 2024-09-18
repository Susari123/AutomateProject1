package com.Edvak_EHR_Automation_V1.testCases;
 
import static org.testng.Assert.assertFalse;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;
 
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Edvak_EHR_Automation_V1.pageObjects.LoginPage;
import com.Edvak_EHR_Automation_V1.utilities.DataReaderFilter;
 
public class TC_StatementReady extends TC_ManageClaims {
    WebDriverWait waitShort;
//    TC_StatementReady st = new TC_StatementReady();
    @BeforeClass
    public void setup() {
        waitShort = new WebDriverWait(driver, Duration.ofSeconds(10));  // Initialize WebDriverWait in a proper place
    }
 
    @Override
    @Test(priority = 0)
    public void testQuickRegistration() throws InterruptedException {
        // Reuse login logic but avoid page reload when clicking the billing button
        LoginPage lp = new LoginPage(driver);
        logger.info("Starting login process...");
        driver.get(baseURL);
        driver.manage().window().maximize();
 
        // Enter username and password
        lp.setUserName("souravsusari311@gmail.com");
        lp.setPassword("Admin@12345");
 
        // Click on the login button
        WebElement loginButton = driver
                .findElement(By.xpath("/html/body/app-root/div/div/app-login/section/div/div/form/div[3]/sl-button"))
                .getShadowRoot().findElement(By.cssSelector("button"));
        new Actions(driver).moveToElement(loginButton).click().build().perform();
 
        // Wait for login to complete and dashboard to load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[normalize-space()='attach_money']")));
 
        // Do not reload the page again by skipping the billing button click if already clicked
        if (!driver.getCurrentUrl().contains("billing")) {
            tcb.clickWithRetry(driver.findElement(By.xpath("//span[normalize-space()='attach_money']")), 3);
        }
        logger.info("Billing button is clicked, login completed.");
    }
 
    boolean filterApplied = false;
 
    @Test(priority = 1, dataProvider = "dataProviderTest", dependsOnMethods = {"testQuickRegistration"})
    void ManageClaims(HashMap<String, Object> data) throws InterruptedException, IOException, TimeoutException {
        try {
            // Wait for the first row to be visible
            WebElement firstRow = waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr")));
            if (firstRow == null) {
                logger.info("First row not found in the table.");
                return;
            }
 
            // Find the status element within the first row
            WebElement statusElement = firstRow.findElement(By.xpath("//td/descendant::sl-badge"));
            if (statusElement == null) {
                logger.info("Status element (sl-badge) not found within the first row.");
                return;
            }
 
            // Use JavaScript to get the text from the shadow DOM of sl-badge
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String statusText = (String) js.executeScript(
                "return arguments[0].shadowRoot.querySelector('slot').assignedNodes()[0].textContent.trim();",
                statusElement);
 
            logger.info("Status Text Retrieved: " + statusText);  // Debugging log
            System.out.println("Status Text Retrieved: " + statusText);
 
            // Run the corresponding method based on the status of the first visible row
            switch (statusText) {
                case "Awaiting to Transmit":
                    handleAwaitingToTransmit();
                    break;
 
                case "Awaiting to Post":
                    handleAwaitingToPost();
                    break;
 
                case "Transmission In Progress":
                    handleTransmissionInProgress();
                    break;
 
                case "Statement Ready":
                    handleStatementReady();
                    break;
 
                case "Clearing House":
                    handleClearingHouse();
                    break;
 
                case "ERA Received":
                    handleERAReceived();
                    break;
 
                case "Payment Received":
                    handlePaymentReceived();
                    break;
 
                case "Settled":
                    handleSettled();
                    break;
 
                case "Pending":
                    handlePending();
                    break;
 
                case "Voided":
                    handleVoided();
                    break;
 
                case "Rejected":
                    handleRejected();
                    break;
 
                default:
                    System.out.println("Unknown status: " + statusText);
                    break;
            }
        } catch (NoSuchElementException e) {
            logger.error("Unable to find the expected element in the claims table.", e);
        } catch (Exception e) {
            logger.error("An error occurred while processing the ManageClaims method.", e);
        }
    }
 // Function to check if all buttons are enabled in the dropdown without closing it
    public boolean isButtonEnabledInDropdown(String buttonName) {
        try {
            // Wait for the sl-menu-item button to be visible
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement button = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//sl-menu-item[.='" + buttonName + "']")));
 
            // Check if the button has the "aria-disabled" attribute or contains a "disabled" class
            String ariaDisabled = button.getAttribute("aria-disabled");
            boolean isDisabledByAria = ariaDisabled != null && ariaDisabled.equals("true");
 
            String classAttribute = button.getAttribute("class");
            boolean isDisabledByClass = classAttribute != null && classAttribute.contains("disabled");
 
            // Return false if either aria-disabled or class indicates the button is disabled
            return !(isDisabledByAria || isDisabledByClass);
 
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException("Button '" + buttonName + "' not found.");
        }
    }
    public void checkButtonState(String buttonName) {
        try {
            // Call the isButtonEnabledInDropdown method for the specific button
            boolean isEnabled = isButtonEnabledInDropdown(buttonName);
 
            if (isEnabled) {
                System.out.println("Button '" + buttonName + "' is enabled.");
            } else {
                System.out.println("Button '" + buttonName + "' is disabled.");
            }
 
        } catch (NoSuchElementException e) {
            System.out.println("Button '" + buttonName + "' not found.");
        } catch (Exception e) {
            System.out.println("An error occurred while checking the button '" + buttonName + "': " + e.getMessage());
        }
    }
    // Handle various statuses (e.g., Awaiting to Transmit)
    private void handleAwaitingToTransmit() throws InterruptedException, IOException {
        System.out.println("Handling Awaiting to Transmit status...");
        WebElement claim = waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr")));
        claim.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[.='Claim Details']")));
        try {
            WebElement awaitingToTransmitReadyBadge = waitShort.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//sl-badge[contains(text(), 'Awaiting to transmit')]")));
            String badgeText = awaitingToTransmitReadyBadge.getText().trim();
            if (badgeText.equals("Awaiting to transmit")) {
                System.out.println("The page contains the 'Awaiting to transmit' text.");
                logger.info("The page contains the 'Awaiting to transmit' text.");
            } else {
                System.out.println("The 'Awaiting to transmit' text is not found.");
                logger.info("The 'Awaiting to transmit' text is not found.");
            }
 
        } catch (NoSuchElementException e) {
            System.out.println("The 'Awaiting to transmit' badge is not present.");
            logger.error("The 'Awaiting to transmit' badge is not present.", e);
        }
        Thread.sleep(2000);
        String[] buttons = { "Reopen", "Edit Claim", "Settle", "Void Claim", "CMS 1500", "Patient Statement" };
 
  try {
        	
            List<WebElement> secondButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[2]"));
            List<WebElement> firstButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[1]"));

         if (!secondButtonList.isEmpty()) {
             WebElement secondButton = secondButtonList.get(0);
             secondButton.click();
             System.out.println("Second button clicked.");
         } else if (!firstButtonList.isEmpty()) {
             WebElement firstButton = firstButtonList.get(0);
             firstButton.click();
             System.out.println("First button clicked.");
         } else {
             System.out.println("Neither button was found.");
         }
 
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; 
        }
 
        for (String buttonName : buttons) {
            checkButtonState(buttonName);
            Thread.sleep(2000); 
        }
        try {
        	
            List<WebElement> secondButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[2]"));
            List<WebElement> firstButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[1]"));

         if (!secondButtonList.isEmpty()) {
  
             WebElement secondButton = secondButtonList.get(0);
             secondButton.click();
             System.out.println("Second button clicked.");
         } else if (!firstButtonList.isEmpty()) {
         
             WebElement firstButton = firstButtonList.get(0);
             firstButton.click();
             System.out.println("First button clicked.");
         } else {
             System.out.println("Neither button was found.");
         }
 
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; 
        }
//        Thread.sleep(2000);
        checkClaim();
        
    }
    private void handleAwaitingToPost() throws InterruptedException, IOException, AWTException {
        System.out.println("Handling Awaiting to Post status...");
        WebElement claim = waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr")));
        claim.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[.='Claim Details']")));
        try {
            // Locate the badge with "Awaiting to post"
            WebElement awaitingToPostReadyBadge = waitShort.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//sl-badge[contains(text(), 'Awaiting to post')]")));
 
            // Verify if the text is "STATEMENT READY"
            String badgeText = awaitingToPostReadyBadge.getText().trim();
            if (badgeText.equals("Awaiting to post")) {
                System.out.println("The page contains the 'Awaiting to post' text.");
                logger.info("The page contains the 'Awaiting to post.");
            } else {
                System.out.println("The 'Awaiting to post' text is not found.");
                logger.info("The 'Awaiting to post' text is not found.");
            }
 
        } catch (NoSuchElementException e) {
            System.out.println("The 'Awaiting to post' badge is not present.");
            logger.error("The 'Awaiting to post' badge is not present.", e);
        }
        Thread.sleep(2000);
        String[] buttons = { "Reopen", "Edit Claim", "Settle", "Void Claim", "CMS 1500", "Patient Statement" };
 
  try {
        	
            List<WebElement> secondButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[2]"));
            List<WebElement> firstButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[1]"));

         if (!secondButtonList.isEmpty()) {
             WebElement secondButton = secondButtonList.get(0);
             secondButton.click();
             System.out.println("Second button clicked.");
         } else if (!firstButtonList.isEmpty()) {
             WebElement firstButton = firstButtonList.get(0);
             firstButton.click();
             System.out.println("First button clicked.");
         } else {
             System.out.println("Neither button was found.");
         }
 
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; 
        }
 
        for (String buttonName : buttons) {
            checkButtonState(buttonName);
            Thread.sleep(2000); 
        }
        try {
        	
            List<WebElement> secondButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[2]"));
            List<WebElement> firstButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[1]"));

         if (!secondButtonList.isEmpty()) {
  
             WebElement secondButton = secondButtonList.get(0);
             secondButton.click();
             System.out.println("Second button clicked.");
         } else if (!firstButtonList.isEmpty()) {
         
             WebElement firstButton = firstButtonList.get(0);
             firstButton.click();
             System.out.println("First button clicked.");
         } else {
             System.out.println("Neither button was found.");
         }
 
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; 
        }
        logger.info("Attachment Button");
        WebElement Attachment = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step4\"]/sl-button[contains(text(), 'Attachments')]"));
        Attachment.click();
        logger.info("Attachment button clicked ");
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//app-attach-document/div/ed-drawer/ed-drawer-header/h2")));
        WebElement attachmentButton = driver.findElement(By.xpath("//ed-form-wrapper/ed-form-row[1]/div/sl-button"));
        attachmentButton.click();
        
        String filePath = "C:\\Users\\sksusari\\Documents\\Test\\Modifiers_POS_Coding.pdf";
        uploadFile(filePath);
        logger.info("File Uploaded");
        WebElement fileName = driver.findElement(By.xpath("//input[@formcontrolname='fileName']"));
        fileName.sendKeys("TestFile");
        logger.info("File name entered");
        WebElement documentLabel = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-attach-document/div/ed-drawer/ed-drawer-body/ed-form-wrapper/ed-form-row[3]/div/ng-select/div/span"));
        documentLabel.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='ng-dropdown-panel-items scroll-host']//span[contains(text(),  'Appeals Package')]")));
        WebElement TypeOption = driver.findElement(By.xpath("//div[@class='ng-dropdown-panel-items scroll-host']//span[contains(text(),  'Appeals Package')]"));
        TypeOption.click();
        WebElement documentType = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-attach-document/div/ed-drawer/ed-drawer-body/ed-form-wrapper/ed-form-row[4]/div/ng-select/div/span"));
        documentType.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='ng-dropdown-panel-items scroll-host']//span[contains(text(),  ' 03 - Report Justifying Treatment Beyond Utilization Guidelines')]")));
        WebElement documentOption = driver.findElement(By.xpath("//div[@class='ng-dropdown-panel-items scroll-host']//span[contains(text(),  ' 03 - Report Justifying Treatment Beyond Utilization Guidelines')]"));
        documentOption.click();
        WebElement include = driver.findElement(By.xpath("//sl-button[contains(text(), 'Include File')]"));
        include.click();
        logger.info("document Included");
        Thread.sleep(2000);
        WebElement documentSave = driver.findElement(By.xpath("//ed-drawer-footer//sl-button[contains(text(), 'Save')]"));
        documentSave.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div//h2[contains(text(), 'Attached Documents ')]")));
        WebElement cancle = driver.findElement(By.xpath("//ed-drawer-footer//sl-button[contains(text(), 'Cancel')]"));
        cancle.click();
        logger.info("Document Saved");
//        Thread.sleep(2000);
        checkClaim();
        String cpt1 = driver.findElement(By.xpath("//tr//td//ed-row[@class='gap-sm items-center']")).getText();
        System.out.println(cpt1);
        String billed = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step3\"]/div/table/tbody/tr/td[4]")).getText().replace("$", "").replace(",", "").trim();
        System.out.println(billed);
        logger.info("billed amount");
        List<WebElement> tableRows = driver.findElements(By.xpath("//*[@id=\"tour-guide-managing-claims-step3\"]/div/table/tbody/tr"));
        int rowCount = tableRows.size();
        logger.info(rowCount);
        System.out.println("Number of <tr> elements: " + rowCount);
        WebElement serviceLine = driver.findElement(By.xpath("//*[@id='tour-guide-managing-claims-step3']//div//table//tbody//tr")); 
        serviceLine.click();
        String Balance = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step3\"]/div/table/tbody/tr/td[7]")).getText().replace("$", "").replace(",", "").trim();
        System.out.println(Balance);
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(), ' Log Tracker ')]")));
        List<WebElement> lineItemManagerElements = driver.findElements(By.xpath("//h2[contains(text(), 'Line Item Manager')]"));
        Assert.assertFalse(lineItemManagerElements.isEmpty(), "The text 'Line Item Manager' is NOT present on the page.");
        System.out.println("The text 'Line Item Manager' is present on the page.");
        WebElement element = driver.findElement(By.xpath("//sl-tooltip//p"));
        String fullText = element.getText();
        String code = extractCode(fullText);
        System.out.println("Extracted Code: " + code);
        Assert.assertEquals(cpt1, code, "The code does not match the expected value.");
        logger.info("cpt matched");
        String serviceBilled = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-adjust-apply-service-line/div/ed-drawer[1]/ed-drawer-body/section[1]/div[2]/div/div[1]/strong")).getText().replace("$", "").replace(",", "").trim();
        Assert.assertEquals(billed, serviceBilled, "The Billed amount does not match the expected value."); 
        logger.info("BILLING AMOUNT IS CORRECT");
        String balance = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-adjust-apply-service-line/div/ed-drawer[1]/ed-drawer-body/section[1]/div[2]/div/div[4]/strong")).getText().replace("$", "").replace(",", "").trim();
        Assert.assertEquals(Balance, balance, "Balance amount does not match the expected value."); 
        logger.info("Balance amout is matching");
        WebElement element1 = driver.findElement(By.xpath("//ed-drawer-body/section[1]/div[1]/div/ed-row[1]/span"));
        String text = element1.getText().trim();  
        if (text.contains("Primary")) {
            System.out.println("The text contains 'Primary'.");
        } else if (text.contains("Patient")) {
            System.out.println("The text contains 'Patient'.");
        } else {
            System.out.println("Neither 'Primary' nor 'Patient' found.");
        }
        Assert.assertTrue(text.contains("Primary") || text.contains("Patient"), "The text does not contain 'Primary' or 'Patient'.");
        
        Thread.sleep(2000);
        if (text.contains("Primary")) {

            List<WebElement> insuranceSection = driver.findElements(By.xpath("//h6[contains(text(),'Insurance')]"));
            List<WebElement> patientSection = driver.findElements(By.xpath("//h6[contains(text(),'Patient')]"));
            
          
            Assert.assertFalse(insuranceSection.isEmpty(), "Insurance section is not displayed for Primary Payer.");
            Assert.assertFalse(patientSection.isEmpty(), "Patient section is not displayed for Primary Payer.");
            
            System.out.println("Both Insurance and Patient sections are displayed for Primary Payer.");
        } else if (text.contains("Patient")) {
          
            List<WebElement> patientSection = driver.findElements(By.xpath("//h6[contains(text(),'Patient')]"));
            List<WebElement> insuranceSection = driver.findElements(By.xpath("//h6[contains(text(),'Insurance')]"));

          
            Assert.assertFalse(patientSection.isEmpty(), "Patient section is not displayed for Patient Payer.");
            Assert.assertTrue(insuranceSection.isEmpty(), "Insurance section should not be displayed for Patient Payer.");
            
            System.out.println("Only the Patient section is displayed for Patient Payer, Test Pass.");
        } else {
            System.out.println("Payer type is not recognized.");
        }
    }
    private void handleTransmissionInProgress() throws InterruptedException, IOException {
        System.out.println("Handling Transmission In Progress status...");
        WebElement claim = waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr")));
        claim.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[.='Claim Details']")));
        try {
            // Locate the badge with "STATEMENT READY"
            WebElement transmissionReadyBadge = waitShort.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//sl-badge[contains(text(), 'Transmission In Progress')]")));
 
            // Verify if the text is "STATEMENT READY"
            String badgeText = transmissionReadyBadge.getText().trim();
            if (badgeText.equals("Transmission In Progress")) {
                System.out.println("The page contains the 'Transmission In Progress' text.");
                logger.info("The page contains the 'Transmission In Progress' text.");
            } else {
                System.out.println("The 'Transmission In Progress' text is not found.");
                logger.info("The 'Transmission In Progress' text is not found.");
            }
 
        } catch (NoSuchElementException e) {
            System.out.println("The 'Transmission In Progress' badge is not present.");
            logger.error("The 'Transmission In Progress' badge is not present.", e);
        }
        Thread.sleep(2000);
        String[] buttons = { "Reopen", "Edit Claim", "Settle", "Void Claim", "CMS 1500", "Patient Statement" };
 
  try {
        	
            List<WebElement> secondButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[2]"));
            List<WebElement> firstButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[1]"));

         if (!secondButtonList.isEmpty()) {
             WebElement secondButton = secondButtonList.get(0);
             secondButton.click();
             System.out.println("Second button clicked.");
         } else if (!firstButtonList.isEmpty()) {
             WebElement firstButton = firstButtonList.get(0);
             firstButton.click();
             System.out.println("First button clicked.");
         } else {
             System.out.println("Neither button was found.");
         }
 
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; 
        }
 
        for (String buttonName : buttons) {
            checkButtonState(buttonName);
            Thread.sleep(2000); 
        }
        try {
        	
            List<WebElement> secondButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[2]"));
            List<WebElement> firstButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[1]"));

         if (!secondButtonList.isEmpty()) {
  
             WebElement secondButton = secondButtonList.get(0);
             secondButton.click();
             System.out.println("Second button clicked.");
         } else if (!firstButtonList.isEmpty()) {
         
             WebElement firstButton = firstButtonList.get(0);
             firstButton.click();
             System.out.println("First button clicked.");
         } else {
             System.out.println("Neither button was found.");
         }
 
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; 
        }
//        Thread.sleep(2000);
        checkClaim();
        
    }
 
    private void handleStatementReady() throws TimeoutException, InterruptedException, IOException, AWTException {
        System.out.println("Handling Statement Ready status...");
        WebElement claim = waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr")));
        claim.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[.='Claim Details']")));
        try {
            // Locate the badge with "STATEMENT READY"
            WebElement statementReadyBadge = waitShort.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//sl-badge[contains(text(), 'Statement Ready')]")));
 
            // Verify if the text is "STATEMENT READY"
            String badgeText = statementReadyBadge.getText().trim();
            if (badgeText.equals("Statement Ready")) {
                System.out.println("The page contains the 'STATEMENT READY' text.");
                logger.info("The page contains the 'STATEMENT READY' text.");
            } else {
                System.out.println("The 'STATEMENT READY' text is not found.");
                logger.info("The 'STATEMENT READY' text is not found.");
            }
 
        } catch (NoSuchElementException e) {
            System.out.println("The 'STATEMENT READY' badge is not present.");
            logger.error("The 'STATEMENT READY' badge is not present.", e);
        }
        Thread.sleep(2000);
        String[] buttons = { "Reopen", " Edit Claim ", "Settle", "Void Claim", "CMS 1500", "Patient Statement" };
   	 
        try {
        	
            List<WebElement> secondButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[2]"));
            List<WebElement> firstButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[1]"));

         if (!secondButtonList.isEmpty()) {
             WebElement secondButton = secondButtonList.get(0);
             secondButton.click();
             System.out.println("Second button clicked.");
         } else if (!firstButtonList.isEmpty()) {
             WebElement firstButton = firstButtonList.get(0);
             firstButton.click();
             System.out.println("First button clicked.");
         } else {
             System.out.println("Neither button was found.");
         }
 
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; 
        }
 
        for (String buttonName : buttons) {
            checkButtonState(buttonName);
            Thread.sleep(2000); 
        }
        try {
        	
            List<WebElement> secondButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[2]"));
            List<WebElement> firstButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[1]"));

         if (!secondButtonList.isEmpty()) {
  
             WebElement secondButton = secondButtonList.get(0);
             secondButton.click();
             System.out.println("Second button clicked.");
         } else if (!firstButtonList.isEmpty()) {
         
             WebElement firstButton = firstButtonList.get(0);
             firstButton.click();
             System.out.println("First button clicked.");
         } else {
             System.out.println("Neither button was found.");
         }
 
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; 
        }
        logger.info("Attachment Button");
        WebElement Attachment = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step4\"]/sl-button[contains(text(), 'Attachments')]"));
        Attachment.click();
        logger.info("Attachment button clicked ");
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//app-attach-document/div/ed-drawer/ed-drawer-header/h2")));
        WebElement attachmentButton = driver.findElement(By.xpath("//ed-form-wrapper/ed-form-row[1]/div/sl-button"));
        attachmentButton.click();
        
        String filePath = "C:\\Users\\sksusari\\Documents\\Test\\Modifiers_POS_Coding.pdf";
        uploadFile(filePath);
        logger.info("File Uploaded");
        WebElement fileName = driver.findElement(By.xpath("//input[@formcontrolname='fileName']"));
        fileName.sendKeys("TestFile");
        logger.info("File name entered");
        WebElement documentLabel = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-attach-document/div/ed-drawer/ed-drawer-body/ed-form-wrapper/ed-form-row[3]/div/ng-select/div/span"));
        documentLabel.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='ng-dropdown-panel-items scroll-host']//span[contains(text(),  'Appeals Package')]")));
        WebElement TypeOption = driver.findElement(By.xpath("//div[@class='ng-dropdown-panel-items scroll-host']//span[contains(text(),  'Appeals Package')]"));
        TypeOption.click();
        WebElement documentType = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-attach-document/div/ed-drawer/ed-drawer-body/ed-form-wrapper/ed-form-row[4]/div/ng-select/div/span"));
        documentType.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='ng-dropdown-panel-items scroll-host']//span[contains(text(),  ' 03 - Report Justifying Treatment Beyond Utilization Guidelines')]")));
        WebElement documentOption = driver.findElement(By.xpath("//div[@class='ng-dropdown-panel-items scroll-host']//span[contains(text(),  ' 03 - Report Justifying Treatment Beyond Utilization Guidelines')]"));
        documentOption.click();
        WebElement include = driver.findElement(By.xpath("//sl-button[contains(text(), 'Include File')]"));
        include.click();
        logger.info("document Included");
        Thread.sleep(2000);
        WebElement documentSave = driver.findElement(By.xpath("//ed-drawer-footer//sl-button[contains(text(), 'Save')]"));
        documentSave.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div//h2[contains(text(), 'Attached Documents ')]")));
        WebElement cancle = driver.findElement(By.xpath("//ed-drawer-footer//sl-button[contains(text(), 'Cancel')]"));
        cancle.click();
        logger.info("Document Saved");
         
        checkClaim();
        
       String cpt1 = driver.findElement(By.xpath("//tr//td//ed-row[@class='gap-sm items-center']")).getText();
       System.out.println(cpt1);
       String billed = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step3\"]/div/table/tbody/tr/td[4]")).getText().replace("$", "").replace(",", "").trim();
       System.out.println(billed);
       logger.info("billed amount");
       List<WebElement> tableRows = driver.findElements(By.xpath("//*[@id=\"tour-guide-managing-claims-step3\"]/div/table/tbody/tr"));
       int rowCount = tableRows.size();
       logger.info(rowCount);
       System.out.println("Number of <tr> elements: " + rowCount);
       WebElement serviceLine = driver.findElement(By.xpath("//*[@id='tour-guide-managing-claims-step3']//div//table//tbody//tr")); 
       serviceLine.click();
       String Balance = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step3\"]/div/table/tbody/tr/td[7]")).getText().replace("$", "").replace(",", "").trim();
       System.out.println(Balance);
       waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(), ' Log Tracker ')]")));
       List<WebElement> lineItemManagerElements = driver.findElements(By.xpath("//h2[contains(text(), 'Line Item Manager')]"));
       Assert.assertFalse(lineItemManagerElements.isEmpty(), "The text 'Line Item Manager' is NOT present on the page.");
       System.out.println("The text 'Line Item Manager' is present on the page.");
       
       WebElement element = driver.findElement(By.xpath("//sl-tooltip//p"));
       String fullText = element.getText();
       String code = extractCode(fullText);
       System.out.println("Extracted Code: " + code);
       Assert.assertEquals(cpt1, code, "The code does not match the expected value.");
       logger.info("cpt matched");
       String serviceBilled = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-adjust-apply-service-line/div/ed-drawer[1]/ed-drawer-body/section[1]/div[2]/div/div[1]/strong")).getText().replace("$", "").replace(",", "").trim();
       Assert.assertEquals(billed, serviceBilled, "The Billed amount does not match the expected value."); 
       logger.info("BILLING AMOUNT IS CORRECT");
       String balance = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-adjust-apply-service-line/div/ed-drawer[1]/ed-drawer-body/section[1]/div[2]/div/div[4]/strong")).getText().replace("$", "").replace(",", "").trim();
       Assert.assertEquals(Balance, balance, "Balance amount does not match the expected value."); 
       logger.info("Balance amout is matching");
       WebElement element1 = driver.findElement(By.xpath("//ed-drawer-body/section[1]/div[1]/div/ed-row[1]/span"));
       String text = element1.getText().trim();  
       if (text.contains("Primary")) {
           System.out.println("The text contains 'Primary'.");
       } else if (text.contains("Patient")) {
           System.out.println("The text contains 'Patient'.");
       } else {
           System.out.println("Neither 'Primary' nor 'Patient' found.");
       }
       Assert.assertTrue(text.contains("Primary") || text.contains("Patient"), "The text does not contain 'Primary' or 'Patient'.");
       
       Thread.sleep(2000);
       if (text.contains("Primary")) {

           List<WebElement> insuranceSection = driver.findElements(By.xpath("//h6[contains(text(),'Insurance')]"));
           List<WebElement> patientSection = driver.findElements(By.xpath("//h6[contains(text(),'Patient')]"));
           
         
           Assert.assertFalse(insuranceSection.isEmpty(), "Insurance section is not displayed for Primary Payer.");
           Assert.assertFalse(patientSection.isEmpty(), "Patient section is not displayed for Primary Payer.");
           
           System.out.println("Both Insurance and Patient sections are displayed for Primary Payer.");
       } else if (text.contains("Patient")) {
         
           List<WebElement> patientSection = driver.findElements(By.xpath("//h6[contains(text(),'Patient')]"));
           List<WebElement> insuranceSection = driver.findElements(By.xpath("//h6[contains(text(),'Insurance')]"));

         
           Assert.assertFalse(patientSection.isEmpty(), "Patient section is not displayed for Patient Payer.");
           Assert.assertTrue(insuranceSection.isEmpty(), "Insurance section should not be displayed for Patient Payer.");
           
           System.out.println("Only the Patient section is displayed for Patient Payer, Test Pass.");
       } else {
           System.out.println("Payer type is not recognized.");
       }
       WebElement billedAmountElement = driver.findElement(By.xpath("//span[contains(text(),'Patient Balance')]/following-sibling::strong"));
       String billedAmountText = billedAmountElement.getText().replace("$", "").trim();
       double BalanceAmount = Double.parseDouble(billedAmountText);
 
       if (BalanceAmount > 0) {
    	   logger.info("Billed amount is greater than 0: " + BalanceAmount);
           WebElement selectPatientPaymentButton = driver.findElement(By.xpath("//sl-tooltip//sl-button[contains(text(),'Select Patient Payment')]"));
           boolean isButtonEnabled = selectPatientPaymentButton.isEnabled();
           Assert.assertTrue(isButtonEnabled, "'Select Patient Payment' button should be enabled when billed amount is greater than 0.");        
           logger.info("'Select Patient Payment' button is enabled.");
           WebElement NotifyPatientButton = driver.findElement(By.xpath("//sl-tooltip[2]//sl-button[contains(text(),'Notify Patient')]"));
           boolean isSecondButtonEnabled = NotifyPatientButton.isEnabled();
           Assert.assertTrue(isSecondButtonEnabled, "Second button should be enabled when billed amount equals balance amount.");
           
       } else if(BalanceAmount == 0)
       {
    	   Thread.sleep(200);
    	   WebElement AdjustButton = driver.findElement(By.xpath("//sl-tooltip[1]/sl-button[contains(text(),'Adjust')]"));
    	   boolean isAriaDisabled = "true".equals(AdjustButton.getAttribute("aria-disabled"));

           Assert.assertFalse(isAriaDisabled, "'Adjust' button should be disabled when balance amount is 0.");
           logger.info("The 'Adjust' button is correctly disabled when balance is 0.");
           WebElement NotifyPatientButton = driver.findElement(By.xpath("//sl-tooltip[2]//sl-button[contains(text(),'Notify Patient')]"));
           boolean isSecondButtonEnabled = NotifyPatientButton.isEnabled();
           Assert.assertTrue(isSecondButtonEnabled, "Patient statement button is Enable");
           
       }else  {          
    	   logger.info("Test failed ");
       } 
       
       WebElement shadowHost = driver.findElement(By.xpath("//ed-drawer-footer/ed-row/sl-button[2]"));
       SearchContext shadowRoot = shadowHost.getShadowRoot();
       logger.info("nextButton");
       WebElement button = shadowRoot.findElement(By.cssSelector("button")); 	
       boolean isButtonDisabled = "true".equals(button.getAttribute("aria-disabled")) || button.getAttribute("disabled") != null;
       System.out.println(rowCount);
       
       if (rowCount == 1) {
    	   logger.info("Row count is 1. Checking if the button is disabled.");
           if (isButtonDisabled) {
               logger.info("Button is correctly disabled when row count is 1.");
           } else {
        	   logger.info("Error: Button should be disabled when row count is 1.");
           }
       } 
       // If the row count is greater than 1, assert the button is enabled
       else if (rowCount > 1) {
    	   logger.info("Row count is greater than 1. Checking if the button is enabled.");
           if (!isButtonDisabled) {
        	   logger.info("Button is correctly enabled when row count is more than 1.");
           } else {
        	   logger.info("Error: Button should be enabled when row count is more than 1.");
           }
       }
       else {
    	   System.out.println("Skipped");
       }
       logger.info("Test completed");
    }

    private void handleClearingHouse() throws InterruptedException, IOException {
        System.out.println("Handling Clearing House status...");
        WebElement claim = waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr")));
        claim.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[.='Claim Details']")));
        try {
            // Locate the badge with "STATEMENT READY"
            WebElement ClearingHouseReadyBadge = waitShort.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//sl-badge[contains(text(), 'Clearing House')]")));
 
            // Verify if the text is "STATEMENT READY"
            String badgeText = ClearingHouseReadyBadge.getText().trim();
            if (badgeText.equals("Clearing House")) {
                System.out.println("The page contains the 'Clearing House' text.");
                logger.info("The page contains the 'Clearing House' text.");
            } else {
                System.out.println("The 'Clearing House' text is not found.");
                logger.info("The 'Clearing House' text is not found.");
            }
 
        } catch (NoSuchElementException e) {
            System.out.println("The 'Clearing House' badge is not present.");
            logger.error("The 'Clearing House' badge is not present.", e);
        }
        Thread.sleep(2000);
        String[] buttons = { "Reopen", "Edit Claim", "Settle", "Void Claim", "CMS 1500", "Patient Statement" };
 
  try {
        	
            List<WebElement> secondButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[2]"));
            List<WebElement> firstButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[1]"));

         if (!secondButtonList.isEmpty()) {
             WebElement secondButton = secondButtonList.get(0);
             secondButton.click();
             System.out.println("Second button clicked.");
         } else if (!firstButtonList.isEmpty()) {
             WebElement firstButton = firstButtonList.get(0);
             firstButton.click();
             System.out.println("First button clicked.");
         } else {
             System.out.println("Neither button was found.");
         }
 
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; 
        }
 
        for (String buttonName : buttons) {
            checkButtonState(buttonName);
            Thread.sleep(2000); 
        }
        try {
        	
            List<WebElement> secondButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[2]"));
            List<WebElement> firstButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[1]"));

         if (!secondButtonList.isEmpty()) {
  
             WebElement secondButton = secondButtonList.get(0);
             secondButton.click();
             System.out.println("Second button clicked.");
         } else if (!firstButtonList.isEmpty()) {
         
             WebElement firstButton = firstButtonList.get(0);
             firstButton.click();
             System.out.println("First button clicked.");
         } else {
             System.out.println("Neither button was found.");
         }
 
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; 
        }
//        Thread.sleep(2000);
        checkClaim();
        
    }
    private void handleERAReceived() throws InterruptedException, IOException {
        System.out.println("Handling ERA Received status...");
        WebElement claim = waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr")));
        claim.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[.='Claim Details']")));
        try {
            // Locate the badge with "STATEMENT READY"
            WebElement EraReadyBadge = waitShort.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//sl-badge[contains(text(), 'ERA Received')]")));
 
            // Verify if the text is "STATEMENT READY"
            String badgeText = EraReadyBadge.getText().trim();
            if (badgeText.equals("ERA Received")) {
                System.out.println("The page contains the 'ERA Received' text.");
                logger.info("The page contains the 'ERA Received' text.");
            } else {
                System.out.println("The 'ERA Received' text is not found.");
                logger.info("The 'ERA Received' text is not found.");
            }
 
        } catch (NoSuchElementException e) {
            System.out.println("The 'ERA Received' badge is not present.");
            logger.error("The 'ERA Received' badge is not present.", e);
        }
        Thread.sleep(2000);
        String[] buttons = { "Reopen", "Edit Claim", "Settle", "Void Claim", "CMS 1500", "Patient Statement" };
 
  try {
        	
            List<WebElement> secondButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[2]"));
            List<WebElement> firstButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[1]"));

         if (!secondButtonList.isEmpty()) {
             WebElement secondButton = secondButtonList.get(0);
             secondButton.click();
             System.out.println("Second button clicked.");
         } else if (!firstButtonList.isEmpty()) {
             WebElement firstButton = firstButtonList.get(0);
             firstButton.click();
             System.out.println("First button clicked.");
         } else {
             System.out.println("Neither button was found.");
         }
 
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; 
        }
 
        for (String buttonName : buttons) {
            checkButtonState(buttonName);
            Thread.sleep(2000); 
        }
        try {
        	
            List<WebElement> secondButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[2]"));
            List<WebElement> firstButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[1]"));

         if (!secondButtonList.isEmpty()) {
  
             WebElement secondButton = secondButtonList.get(0);
             secondButton.click();
             System.out.println("Second button clicked.");
         } else if (!firstButtonList.isEmpty()) {
         
             WebElement firstButton = firstButtonList.get(0);
             firstButton.click();
             System.out.println("First button clicked.");
         } else {
             System.out.println("Neither button was found.");
         }
 
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; 
        }
//        Thread.sleep(2000);
        checkClaim();
        
    }
    private void handlePaymentReceived() throws InterruptedException, IOException {
        System.out.println("Handling Payment Received status...");
        WebElement claim = waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr")));
        claim.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[.='Claim Details']")));
        try {
            // Locate the badge with "STATEMENT READY"
            WebElement patmentReceivedReadyBadge = waitShort.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//sl-badge[contains(text(), 'Payment Received')]")));
 
            // Verify if the text is "STATEMENT READY"
            String badgeText = patmentReceivedReadyBadge.getText().trim();
            if (badgeText.equals("Payment Received")) {
                System.out.println("The page contains the 'Payment Received' text.");
                logger.info("The page contains the 'Payment Received' text.");
            } else {
                System.out.println("The 'Payment Received' text is not found.");
                logger.info("The 'Payment Received' text is not found.");
            }
 
        } catch (NoSuchElementException e) {
            System.out.println("The 'Payment Received' badge is not present.");
            logger.error("The 'Payment Received' badge is not present.", e);
        }
        Thread.sleep(2000);
        String[] buttons = { "Reopen", "Edit Claim", "Settle", "Void Claim", "CMS 1500", "Patient Statement" };
 
  try {
        	
            List<WebElement> secondButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[2]"));
            List<WebElement> firstButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[1]"));

         if (!secondButtonList.isEmpty()) {
             WebElement secondButton = secondButtonList.get(0);
             secondButton.click();
             System.out.println("Second button clicked.");
         } else if (!firstButtonList.isEmpty()) {
             WebElement firstButton = firstButtonList.get(0);
             firstButton.click();
             System.out.println("First button clicked.");
         } else {
             System.out.println("Neither button was found.");
         }
 
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; 
        }
 
        for (String buttonName : buttons) {
            checkButtonState(buttonName);
            Thread.sleep(2000); 
        }
        try {
        	
            List<WebElement> secondButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[2]"));
            List<WebElement> firstButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[1]"));

         if (!secondButtonList.isEmpty()) {
  
             WebElement secondButton = secondButtonList.get(0);
             secondButton.click();
             System.out.println("Second button clicked.");
         } else if (!firstButtonList.isEmpty()) {
         
             WebElement firstButton = firstButtonList.get(0);
             firstButton.click();
             System.out.println("First button clicked.");
         } else {
             System.out.println("Neither button was found.");
         }
 
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; 
        }
//        Thread.sleep(2000);
        checkClaim();
        
    }
    private void handleSettled() throws InterruptedException, IOException {
        System.out.println("Handling Settled status...");
        WebElement claim = waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr")));
        claim.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[.='Claim Details']")));
        try {
            // Locate the badge with "STATEMENT READY"
            WebElement settleReadyBadge = waitShort.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//sl-badge[contains(text(), 'Settled')]")));
 
            // Verify if the text is "STATEMENT READY"
            String badgeText = settleReadyBadge.getText().trim();
            if (badgeText.equals("Settled")) {
                System.out.println("The page contains the 'Settled' text.");
                logger.info("The page contains the 'Settled' text.");
            } else {
                System.out.println("The 'Settled' text is not found.");
                logger.info("The 'Settled' text is not found.");
            }
 
        } catch (NoSuchElementException e) {
            System.out.println("The 'Settled' badge is not present.");
            logger.error("The 'Settled' badge is not present.", e);
        }
        Thread.sleep(2000);
        String[] buttons = { "Reopen", "Edit Claim", "Settle", "Void Claim", "CMS 1500", "Patient Statement" };
 
  try {
        	
            List<WebElement> secondButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[2]"));
            List<WebElement> firstButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[1]"));

         if (!secondButtonList.isEmpty()) {
             WebElement secondButton = secondButtonList.get(0);
             secondButton.click();
             System.out.println("Second button clicked.");
         } else if (!firstButtonList.isEmpty()) {
             WebElement firstButton = firstButtonList.get(0);
             firstButton.click();
             System.out.println("First button clicked.");
         } else {
             System.out.println("Neither button was found.");
         }
 
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; 
        }
 
        for (String buttonName : buttons) {
            checkButtonState(buttonName);
            Thread.sleep(2000); 
        }
        try {
        	
            List<WebElement> secondButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[2]"));
            List<WebElement> firstButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[1]"));

         if (!secondButtonList.isEmpty()) {
  
             WebElement secondButton = secondButtonList.get(0);
             secondButton.click();
             System.out.println("Second button clicked.");
         } else if (!firstButtonList.isEmpty()) {
         
             WebElement firstButton = firstButtonList.get(0);
             firstButton.click();
             System.out.println("First button clicked.");
         } else {
             System.out.println("Neither button was found.");
         }
 
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; 
        }
//        Thread.sleep(2000);
        checkClaim();
        
    }
    private void handlePending() throws InterruptedException, IOException {
        System.out.println("Handling Pending status...");
        WebElement claim = waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr")));
        claim.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[.='Claim Details']")));
        try {
            // Locate the badge with "STATEMENT READY"
            WebElement PendingReadyBadge = waitShort.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//sl-badge[contains(text(), 'Pending')]")));
 
            // Verify if the text is "STATEMENT READY"
            String badgeText = PendingReadyBadge.getText().trim();
            if (badgeText.equals("Statement Ready")) {
                System.out.println("The page contains the 'Pending' text.");
                logger.info("The page contains the 'Pending' text.");
            } else {
                System.out.println("The 'Pending' text is not found.");
                logger.info("The 'Pending' text is not found.");
            }
 
        } catch (NoSuchElementException e) {
            System.out.println("The 'Pending' badge is not present.");
            logger.error("The 'Pending' badge is not present.", e);
        }
        Thread.sleep(2000);
        String[] buttons = { "Reopen", "Edit Claim", "Settle", "Void Claim", "CMS 1500", "Patient Statement" };
 
  try {
        	
            List<WebElement> secondButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[2]"));
            List<WebElement> firstButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[1]"));

         if (!secondButtonList.isEmpty()) {
             WebElement secondButton = secondButtonList.get(0);
             secondButton.click();
             System.out.println("Second button clicked.");
         } else if (!firstButtonList.isEmpty()) {
             WebElement firstButton = firstButtonList.get(0);
             firstButton.click();
             System.out.println("First button clicked.");
         } else {
             System.out.println("Neither button was found.");
         }
 
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; 
        }
 
        for (String buttonName : buttons) {
            checkButtonState(buttonName);
            Thread.sleep(2000); 
        }
        try {
        	
            List<WebElement> secondButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[2]"));
            List<WebElement> firstButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[1]"));

         if (!secondButtonList.isEmpty()) {
  
             WebElement secondButton = secondButtonList.get(0);
             secondButton.click();
             System.out.println("Second button clicked.");
         } else if (!firstButtonList.isEmpty()) {
         
             WebElement firstButton = firstButtonList.get(0);
             firstButton.click();
             System.out.println("First button clicked.");
         } else {
             System.out.println("Neither button was found.");
         }
 
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; 
        }
//        Thread.sleep(2000);
        checkClaim();
        
    }
    private void handleVoided() throws InterruptedException, IOException {
        System.out.println("Handling Voided status...");
        WebElement claim = waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr")));
        claim.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[.='Claim Details']")));
        try {
            // Locate the badge with "STATEMENT READY"
            WebElement VoidedReadyBadge = waitShort.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//sl-badge[contains(text(), 'Voided')]")));
 
            // Verify if the text is "STATEMENT READY"
            String badgeText = VoidedReadyBadge.getText().trim();
            if (badgeText.equals("Voided")) {
                System.out.println("The page contains the 'Voided' text.");
                logger.info("The page contains the 'Voided' text.");
            } else {
                System.out.println("The 'Voided' text is not found.");
                logger.info("The 'Voided' text is not found.");
            }
 
        } catch (NoSuchElementException e) {
            System.out.println("The 'Voided' badge is not present.");
            logger.error("The 'Voided' badge is not present.", e);
        }
        Thread.sleep(2000);
        String[] buttons = { "Reopen", "Edit Claim", "Settle", "Void Claim", "CMS 1500", "Patient Statement" };
 
  try {
        	
            List<WebElement> secondButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[2]"));
            List<WebElement> firstButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[1]"));

         if (!secondButtonList.isEmpty()) {
             WebElement secondButton = secondButtonList.get(0);
             secondButton.click();
             System.out.println("Second button clicked.");
         } else if (!firstButtonList.isEmpty()) {
             WebElement firstButton = firstButtonList.get(0);
             firstButton.click();
             System.out.println("First button clicked.");
         } else {
             System.out.println("Neither button was found.");
         }
 
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; 
        }
 
        for (String buttonName : buttons) {
            checkButtonState(buttonName);
            Thread.sleep(2000); 
        }
        try {
        	
            List<WebElement> secondButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[2]"));
            List<WebElement> firstButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[1]"));

         if (!secondButtonList.isEmpty()) {
  
             WebElement secondButton = secondButtonList.get(0);
             secondButton.click();
             System.out.println("Second button clicked.");
         } else if (!firstButtonList.isEmpty()) {
         
             WebElement firstButton = firstButtonList.get(0);
             firstButton.click();
             System.out.println("First button clicked.");
         } else {
             System.out.println("Neither button was found.");
         }
 
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; 
        }
//        Thread.sleep(2000);
        checkClaim();
        
    }
    private void handleRejected() throws InterruptedException, IOException {
        System.out.println("Handling Rejected status...");
        WebElement claim = waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr")));
        claim.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[.='Claim Details']")));
        try {
            // Locate the badge with "STATEMENT READY"
            WebElement statementReadyBadge = waitShort.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//sl-badge[contains(text(), 'Statement Ready')]")));
 
            // Verify if the text is "STATEMENT READY"
            String badgeText = statementReadyBadge.getText().trim();
            if (badgeText.equals("Statement Ready")) {
                System.out.println("The page contains the 'STATEMENT READY' text.");
                logger.info("The page contains the 'STATEMENT READY' text.");
            } else {
                System.out.println("The 'STATEMENT READY' text is not found.");
                logger.info("The 'STATEMENT READY' text is not found.");
            }
 
        } catch (NoSuchElementException e) {
            System.out.println("The 'STATEMENT READY' badge is not present.");
            logger.error("The 'STATEMENT READY' badge is not present.", e);
        }
        Thread.sleep(2000);
        String[] buttons = { "Reopen", "Edit Claim", "Settle", "Void Claim", "CMS 1500", "Patient Statement" };
   	 
  try {
        	
            List<WebElement> secondButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[2]"));
            List<WebElement> firstButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[1]"));

         if (!secondButtonList.isEmpty()) {
             WebElement secondButton = secondButtonList.get(0);
             secondButton.click();
             System.out.println("Second button clicked.");
         } else if (!firstButtonList.isEmpty()) {
             WebElement firstButton = firstButtonList.get(0);
             firstButton.click();
             System.out.println("First button clicked.");
         } else {
             System.out.println("Neither button was found.");
         }
 
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; 
        }
 
        for (String buttonName : buttons) {
            checkButtonState(buttonName);
            Thread.sleep(2000); 
        }
        try {
        	
            List<WebElement> secondButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[2]"));
            List<WebElement> firstButtonList = driver.findElements(By.xpath("//*[@id='tour-guide-managing-claims-step4']//sl-dropdown[1]"));

         if (!secondButtonList.isEmpty()) {
  
             WebElement secondButton = secondButtonList.get(0);
             secondButton.click();
             System.out.println("Second button clicked.");
         } else if (!firstButtonList.isEmpty()) {
         
             WebElement firstButton = firstButtonList.get(0);
             firstButton.click();
             System.out.println("First button clicked.");
         } else {
             System.out.println("Neither button was found.");
         }
 
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; 
        }
//        Thread.sleep(2000);
        checkClaim();
        
    }
    
    public void checkClaim() throws InterruptedException, IOException {
        
        Thread.sleep(3000);

        List<WebElement> myDropdownElements = driver.findElements(By.xpath("//*[@id='myDropdown']"));
        boolean isMyDropdownVisible = !myDropdownElements.isEmpty() && myDropdownElements.get(0).isDisplayed();

        List<WebElement> claimDetailSection = driver.findElements(By.xpath("/html/body/app-root/div/div[2]/app-claim-detail/div/div/section[1]/div/sl-icon-button"));
        boolean isClaimDetailVisible = !claimDetailSection.isEmpty() && claimDetailSection.get(0).isDisplayed();

        if (isMyDropdownVisible && isClaimDetailVisible) {
            System.out.println("Note is already added, skipping to the next test.");
        } else {
            System.out.println("Either 'myDropdown' or 'app-claim-detail' is not visible. Proceeding with adding a note.");
            WebElement addNote = driver.findElement(By.xpath("//*[@id='myDropdown']"));
            addNote.click();
            waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), 'Add Note')]")));
            WebElement textArea = driver.findElement(By.xpath("//textarea[@formcontrolname='note']"));
            textArea.sendKeys("Sourav");
            WebElement saveButton = driver.findElement(By.xpath("//*[@id='myDropdown']/main/ed-row/sl-button[2]"));
            saveButton.click();
            System.out.println("Test case executed successfully.");
        }

        Thread.sleep(200);

        try {
            WebElement paragraph = driver.findElement(By.xpath("//*[@id='tour-guide-managing-claims-step5']/descendant::p"));
            Assert.assertTrue(paragraph.isDisplayed(), "GenerateClaim Claim tracker is displayed.");
            logger.info("Claim Tracker is present");
        } catch (Exception e) {
            Assert.fail("GenerateClaim Claim tracker does not exist on the page.");
        }

        List<WebElement> serviceLineElements = driver.findElements(By.xpath("//section[@id='tour-guide-managing-claims-step3']//div"));
        if (!serviceLineElements.isEmpty()) {
            logger.info("ServiceLine present");
            Assert.assertTrue(true, "Test Passed: Element found.");
        } else {
            Assert.fail("Test Failed: Element not found.");
        }
    }
    public static String extractCode(String fullText) {
        // Check if there is a hyphen delimiter in the text
        if (fullText.contains("-")) {            
            String[] parts = fullText.split("-", 2);  
            String codePart = parts[0].trim();         
            codePart = codePart.replaceAll("\\s+", "");  
            return codePart;
        } else {
            return fullText.trim().replaceAll("\\s+", ""); 
        }
    }
    public static void uploadFile(String filePath) throws AWTException, InterruptedException {
    	Robot robot = new Robot();
        Thread.sleep(2000); // Give time for the file dialog to open

        for (char c : filePath.toCharArray()) {
            typeCharacter(robot, c);
        }

        // Press 'Enter' to confirm the file path
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }
    private static void typeCharacter(Robot robot, char character) {
        switch (character) {
            case '\\':
                // Simulate backslash (\)
                robot.keyPress(KeyEvent.VK_BACK_SLASH);
                robot.keyRelease(KeyEvent.VK_BACK_SLASH);
                break;
            case ':':
                // Simulate colon (:)
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_SEMICOLON);
                robot.keyRelease(KeyEvent.VK_SEMICOLON);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            case '_':
                // Simulate underscore (_), which is Shift + Hyphen
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_MINUS);  // Hyphen key is used for underscore
                robot.keyRelease(KeyEvent.VK_MINUS);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                break;
            default:
                // Handle uppercase letters
                if (Character.isUpperCase(character)) {
                    robot.keyPress(KeyEvent.VK_SHIFT);
                    robot.keyPress(KeyEvent.getExtendedKeyCodeForChar(Character.toLowerCase(character)));
                    robot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(Character.toLowerCase(character)));
                    robot.keyRelease(KeyEvent.VK_SHIFT);
                } else {
                    // Handle lowercase letters and numbers
                    robot.keyPress(KeyEvent.getExtendedKeyCodeForChar(character));
                    robot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(character));
                }
                break;
        }
    }


    @DataProvider(name = "dataProviderTest")
    public Object[][] dataProvider() throws IOException {
        // Reuse the DataProvider for consistent data feeding
        DataReaderFilter dataReader = new DataReaderFilter();
        HashMap<String, Object> jsonData = dataReader.getJsonDataToMapFilter();
 
        return new Object[][] {
            { jsonData }
        };
    }
}