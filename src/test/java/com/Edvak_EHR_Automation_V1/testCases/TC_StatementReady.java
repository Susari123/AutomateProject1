package com.Edvak_EHR_Automation_V1.testCases;
 
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeoutException;
 
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
    private void handleAwaitingToTransmit() throws InterruptedException {
        System.out.println("Handling Awaiting to Transmit status...");
        WebElement claim = waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr")));
        claim.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[.='Claim Details']")));
        try {
            // Locate the badge with "STATEMENT READY"
            WebElement awaitingToTransmitReadyBadge = waitShort.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//sl-badge[contains(text(), 'Awaiting to transmit')]")));
 
            // Verify if the text is "STATEMENT READY"
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
 
        // Click the dropdown once to open it
        try {
            WebElement actionsDropdown = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step4\"]/sl-dropdown[2]"));
            actionsDropdown.click();
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; // Stop the test if the dropdown is not found
        }
 
        // Loop through each button name and check if it is enabled or disabled
        for (String buttonName : buttons) {
            checkButtonState(buttonName);
            Thread.sleep(2000); // Pause for 2 seconds between checks
        }
        Thread.sleep(1000);
        WebElement Note = driver.findElement(By.xpath("//sl-dropdown[@id='myDropdown']"));
        Note.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), 'Add Note')]")));
        WebElement textArea = driver.findElement(By.xpath("//*[@id=\"myDropdown\"]/main/form/textarea"));
        textArea.sendKeys("Test case");
        WebElement save = driver.findElement(By.xpath("//*[@id=\"myDropdown\"]/main/ed-row/sl-button[2]"));
        save.click();
        try {
            WebElement paragraph = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step5\"]/div/div/p"));
            Assert.assertTrue(paragraph.isDisplayed(), "Paragraph <p> tag is displayed.");
        } catch (Exception e) {
        	Assert.fail("Paragraph <p> tag does not exist on the page.");
        }
        try {
            // Locate the element containing the payer text
            WebElement payerTextElement = driver.findElement(By.id("//*[@id=\"tour-guide-managing-claims-step1\"]/section[3]/ed-row[1]/label")); // Replace with the correct locator
 
            // Get the text of the element
            String payerText = payerTextElement.getText();
 
            // Check if the text contains "Primary" and does not contain "Patient"
            if (payerText.contains("Primary") && !payerText.contains("Patient")) {
            	Assert.assertTrue(true, "Text contains 'Primary' and does not contain 'Patient'. Test passed.");
            } else {
            	Assert.fail("Text does not contain 'Primary' or contains 'Patient'. Test failed.");
            }
        } catch (Exception e) {
            // Fail the test if there's an issue finding the element or retrieving the text
        	Assert.fail("An error occurred while checking the payer text: " + e.getMessage());
        }
    }
 
    private void handleAwaitingToPost() throws InterruptedException {
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
 
        // Click the dropdown once to open it
        try {
            WebElement actionsDropdown = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step4\"]/sl-dropdown[2]"));
            actionsDropdown.click();
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; // Stop the test if the dropdown is not found
        }
 
        // Loop through each button name and check if it is enabled or disabled
        for (String buttonName : buttons) {
            checkButtonState(buttonName);
            Thread.sleep(2000); // Pause for 2 seconds between checks
        }
        Thread.sleep(1000);
        WebElement Note = driver.findElement(By.xpath("//sl-dropdown[@id='myDropdown']"));
        Note.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), 'Add Note')]")));
        WebElement textArea = driver.findElement(By.xpath("//*[@id=\"myDropdown\"]/main/form/textarea"));
        textArea.sendKeys("Test case");
        WebElement save = driver.findElement(By.xpath("//*[@id=\"myDropdown\"]/main/ed-row/sl-button[2]"));
        save.click();
        try {
            WebElement paragraph = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step5\"]/div/div/p"));
            Assert.assertTrue(paragraph.isDisplayed(), "Paragraph <p> tag is displayed.");
        } catch (Exception e) {
        	Assert.fail("Paragraph <p> tag does not exist on the page.");
        }
        try {
            // Locate the element containing the payer text
            WebElement payerTextElement = driver.findElement(By.id("//*[@id=\"tour-guide-managing-claims-step1\"]/section[3]/ed-row[1]/label")); // Replace with the correct locator
 
            // Get the text of the element
            String payerText = payerTextElement.getText();
 
            // Check if the text contains "Primary" and does not contain "Patient"
            if (payerText.contains("Primary") && !payerText.contains("Patient")) {
            	Assert.assertTrue(true, "Text contains 'Primary' and does not contain 'Patient'. Test passed.");
            } else {
            	Assert.fail("Text does not contain 'Primary' or contains 'Patient'. Test failed.");
            }
        } catch (Exception e) {
            // Fail the test if there's an issue finding the element or retrieving the text
        	Assert.fail("An error occurred while checking the payer text: " + e.getMessage());
        }
    }
 
    private void handleTransmissionInProgress() throws InterruptedException {
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
 
        // Click the dropdown once to open it
        try {
            WebElement actionsDropdown = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step4\"]/sl-dropdown[2]"));
            actionsDropdown.click();
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; // Stop the test if the dropdown is not found
        }
 
        // Loop through each button name and check if it is enabled or disabled
        for (String buttonName : buttons) {
            checkButtonState(buttonName);
            Thread.sleep(2000); // Pause for 2 seconds between checks
        }
        Thread.sleep(1000);
        WebElement Note = driver.findElement(By.xpath("//sl-dropdown[@id='myDropdown']"));
        Note.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), 'Add Note')]")));
        WebElement textArea = driver.findElement(By.xpath("//*[@id=\"myDropdown\"]/main/form/textarea"));
        textArea.sendKeys("Test case");
        WebElement save = driver.findElement(By.xpath("//*[@id=\"myDropdown\"]/main/ed-row/sl-button[2]"));
        save.click();
        try {
            WebElement paragraph = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step5\"]/div/div/p"));
            Assert.assertTrue(paragraph.isDisplayed(), "Paragraph <p> tag is displayed.");
        } catch (Exception e) {
        	Assert.fail("Paragraph <p> tag does not exist on the page.");
        }
        try {
            // Locate the element containing the payer text
            WebElement payerTextElement = driver.findElement(By.id("//*[@id=\"tour-guide-managing-claims-step1\"]/section[3]/ed-row[1]/label")); // Replace with the correct locator
 
            // Get the text of the element
            String payerText = payerTextElement.getText();
 
            // Check if the text contains "Primary" and does not contain "Patient"
            if (payerText.contains("Primary") && !payerText.contains("Patient")) {
            	Assert.assertTrue(true, "Text contains 'Primary' and does not contain 'Patient'. Test passed.");
            } else {
            	Assert.fail("Text does not contain 'Primary' or contains 'Patient'. Test failed.");
            }
        } catch (Exception e) {
            // Fail the test if there's an issue finding the element or retrieving the text
        	Assert.fail("An error occurred while checking the payer text: " + e.getMessage());
        }
       
    }
 
    private void handleStatementReady() throws TimeoutException, InterruptedException {
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
        String[] buttons = { "Reopen", "Edit Claim", "Settle", "Void Claim", "CMS 1500", "Patient Statement" };
 
        // Click the dropdown once to open it
        try {
            WebElement actionsDropdown = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step4\"]/sl-dropdown[2]"));
            actionsDropdown.click();
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; // Stop the test if the dropdown is not found
        }
 
        // Loop through each button name and check if it is enabled or disabled
        for (String buttonName : buttons) {
            checkButtonState(buttonName);
            Thread.sleep(2000); // Pause for 2 seconds between checks
        }
        Thread.sleep(1000);
        WebElement Note = driver.findElement(By.xpath("//sl-dropdown[@id='myDropdown']"));
        Note.click();

        // Wait for the 'Add Note' option to be visible
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), 'Add Note')]")));

        // Check if the textarea and save button are present
        List<WebElement> textAreaList = driver.findElements(By.xpath("//*[@id=\"myDropdown\"]/main/form/textarea"));
        List<WebElement> saveButtonList = driver.findElements(By.xpath("//*[@id=\"myDropdown\"]/main/ed-row/sl-button[2]"));

        // Run the test only if both the text area and the save button are present
        if (!textAreaList.isEmpty() && !saveButtonList.isEmpty()) {
            WebElement textArea = textAreaList.get(0);
            textArea.sendKeys("Test case");

            WebElement save = saveButtonList.get(0);
            save.click();
            System.out.println("Test case executed successfully.");
        } else {
            System.out.println("Required elements not found. Test skipped.");
        }
        try {
            WebElement paragraph = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step5\"]/div/div/p"));
            Assert.assertTrue(paragraph.isDisplayed(), "Paragraph <p> tag is displayed.");
        } catch (Exception e) {
        	Assert.fail("Paragraph <p> tag does not exist on the page.");
        }
        try {
            // Locate the element containing the payer text
            WebElement payerTextElement = driver.findElement(By.id("//*[@id=\"tour-guide-managing-claims-step1\"]/section[3]/ed-row[1]/label"));
 
            // Get the text of the element
            String payerText = payerTextElement.getText();
 
            // Check if the text contains "Patient" and does not contain "Primary"
            if (payerText.contains("Patient") && !payerText.contains("Primary")) {
            	Assert.assertTrue(true, "Text contains 'Patient' and does not contain 'Primary'. Test passed.");
            } else {
            	Assert.fail("Text does not contain 'Patient' or contains 'Primary'. Test failed.");
            }
        } catch (Exception e) {
            // Fail the test if there's an issue finding the element or retrieving the text
        	Assert.fail("An error occurred while checking the payer text: " + e.getMessage());
        }
    }
   
 
    private void handleClearingHouse() throws InterruptedException {
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
 
        // Click the dropdown once to open it
        try {
            WebElement actionsDropdown = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step4\"]/sl-dropdown[2]"));
            actionsDropdown.click();
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; // Stop the test if the dropdown is not found
        }
 
        // Loop through each button name and check if it is enabled or disabled
        for (String buttonName : buttons) {
            checkButtonState(buttonName);
            Thread.sleep(2000); // Pause for 2 seconds between checks
        }
        Thread.sleep(1000);
        WebElement Note = driver.findElement(By.xpath("//sl-dropdown[@id='myDropdown']"));
        Note.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), 'Add Note')]")));
        WebElement textArea = driver.findElement(By.xpath("//*[@id=\"myDropdown\"]/main/form/textarea"));
        textArea.sendKeys("Test case");
        WebElement save = driver.findElement(By.xpath("//*[@id=\"myDropdown\"]/main/ed-row/sl-button[2]"));
        save.click();
        try {
            WebElement paragraph = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step5\"]/div/div/p"));
            Assert.assertTrue(paragraph.isDisplayed(), "Paragraph <p> tag is displayed.");
        } catch (Exception e) {
        	Assert.fail("Paragraph <p> tag does not exist on the page.");
        }
        try {
            // Locate the element containing the payer text
            WebElement payerTextElement = driver.findElement(By.id("//*[@id=\"tour-guide-managing-claims-step1\"]/section[3]/ed-row[1]/label")); // Replace with the correct locator
 
            // Get the text of the element
            String payerText = payerTextElement.getText();
 
            // Check if the text contains "Primary" and does not contain "Patient"
            if (payerText.contains("Primary") && !payerText.contains("Patient")) {
            	Assert.assertTrue(true, "Text contains 'Primary' and does not contain 'Patient'. Test passed.");
            } else {
            	Assert.fail("Text does not contain 'Primary' or contains 'Patient'. Test failed.");
            }
        } catch (Exception e) {
            // Fail the test if there's an issue finding the element or retrieving the text
        	Assert.fail("An error occurred while checking the payer text: " + e.getMessage());
        }
    }
    private void handleERAReceived() throws InterruptedException {
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
 
        // Click the dropdown once to open it
        try {
            WebElement actionsDropdown = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step4\"]/sl-dropdown[2]"));
            actionsDropdown.click();
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; // Stop the test if the dropdown is not found
        }
 
        // Loop through each button name and check if it is enabled or disabled
        for (String buttonName : buttons) {
            checkButtonState(buttonName);
            Thread.sleep(2000); // Pause for 2 seconds between checks
        }
        Thread.sleep(1000);
        WebElement Note = driver.findElement(By.xpath("//sl-dropdown[@id='myDropdown']"));
        Note.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), 'Add Note')]")));
        WebElement textArea = driver.findElement(By.xpath("//*[@id=\"myDropdown\"]/main/form/textarea"));
        textArea.sendKeys("Test case");
        WebElement save = driver.findElement(By.xpath("//*[@id=\"myDropdown\"]/main/ed-row/sl-button[2]"));
        save.click();
        try {
            WebElement paragraph = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step5\"]/div/div/p"));
            Assert.assertTrue(paragraph.isDisplayed(), "Paragraph <p> tag is displayed.");
        } catch (Exception e) {
        	Assert.fail("Paragraph <p> tag does not exist on the page.");
        }
        try {
            // Locate the element containing the payer text
            WebElement payerTextElement = driver.findElement(By.id("//*[@id=\"tour-guide-managing-claims-step1\"]/section[3]/ed-row[1]/label")); // Replace with the correct locator
 
            // Get the text of the element
            String payerText = payerTextElement.getText();
 
            // Check if the text contains "Primary" and does not contain "Patient"
            if (payerText.contains("Primary") && !payerText.contains("Patient")) {
            	Assert.assertTrue(true, "Text contains 'Primary' and does not contain 'Patient'. Test passed.");
            } else {
            	Assert.fail("Text does not contain 'Primary' or contains 'Patient'. Test failed.");
            }
        } catch (Exception e) {
            // Fail the test if there's an issue finding the element or retrieving the text
        	Assert.fail("An error occurred while checking the payer text: " + e.getMessage());
        }
    }
    private void handlePaymentReceived() throws InterruptedException {
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
 
        // Click the dropdown once to open it
        try {
            WebElement actionsDropdown = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step4\"]/sl-dropdown[2]"));
            actionsDropdown.click();
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; // Stop the test if the dropdown is not found
        }
 
        // Loop through each button name and check if it is enabled or disabled
        for (String buttonName : buttons) {
            checkButtonState(buttonName);
            Thread.sleep(2000); // Pause for 2 seconds between checks
        }
        Thread.sleep(1000);
        WebElement Note = driver.findElement(By.xpath("//sl-dropdown[@id='myDropdown']"));
        Note.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), 'Add Note')]")));
        WebElement textArea = driver.findElement(By.xpath("//*[@id=\"myDropdown\"]/main/form/textarea"));
        textArea.sendKeys("Test case");
        WebElement save = driver.findElement(By.xpath("//*[@id=\"myDropdown\"]/main/ed-row/sl-button[2]"));
        save.click();
        try {
            WebElement paragraph = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step5\"]/div/div/p"));
            Assert.assertTrue(paragraph.isDisplayed(), "Paragraph <p> tag is displayed.");
        } catch (Exception e) {
        	Assert.fail("Paragraph <p> tag does not exist on the page.");
        }
        try {
            // Locate the element containing the payer text
            WebElement payerTextElement = driver.findElement(By.id("//*[@id=\"tour-guide-managing-claims-step1\"]/section[3]/ed-row[1]/label")); // Replace with the correct locator
 
            // Get the text of the element
            String payerText = payerTextElement.getText();
 
            // Check if the text contains "Primary" and does not contain "Patient"
            if (payerText.contains("Primary") && !payerText.contains("Patient")) {
            	Assert.assertTrue(true, "Text contains 'Primary' and does not contain 'Patient'. Test passed.");
            } else {
            	Assert.fail("Text does not contain 'Primary' or contains 'Patient'. Test failed.");
            }
        } catch (Exception e) {
            // Fail the test if there's an issue finding the element or retrieving the text
        	Assert.fail("An error occurred while checking the payer text: " + e.getMessage());
        }
    }
 
    private void handleSettled() throws InterruptedException {
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
 
        // Click the dropdown once to open it
        try {
            WebElement actionsDropdown = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step4\"]/sl-dropdown[2]"));
            actionsDropdown.click();
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; // Stop the test if the dropdown is not found
        }
 
        // Loop through each button name and check if it is enabled or disabled
        for (String buttonName : buttons) {
            checkButtonState(buttonName);
            Thread.sleep(2000); // Pause for 2 seconds between checks
        }
        Thread.sleep(1000);
        WebElement Note = driver.findElement(By.xpath("//sl-dropdown[@id='myDropdown']"));
        Note.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), 'Add Note')]")));
        WebElement textArea = driver.findElement(By.xpath("//*[@id=\"myDropdown\"]/main/form/textarea"));
        textArea.sendKeys("Test case");
        WebElement save = driver.findElement(By.xpath("//*[@id=\"myDropdown\"]/main/ed-row/sl-button[2]"));
        save.click();
        try {
            WebElement paragraph = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step5\"]/div/div/p"));
            Assert.assertTrue(paragraph.isDisplayed(), "Paragraph <p> tag is displayed.");
        } catch (Exception e) {
        	Assert.fail("Paragraph <p> tag does not exist on the page.");
        }
        try {
            // Locate the element containing the payer text
            WebElement payerTextElement = driver.findElement(By.id("//*[@id=\"tour-guide-managing-claims-step1\"]/section[3]/ed-row[1]/label")); // Replace with the correct locator
 
            // Get the text of the element
            String payerText = payerTextElement.getText();
 
            // Check if the text contains "Primary" and does not contain "Patient"
            if (payerText.contains("Primary") && !payerText.contains("Patient")) {
                Assert.assertTrue(true, "Text contains 'Primary' and does not contain 'Patient'. Test passed.");
            } else {
                Assert.fail("Text does not contain 'Primary' or contains 'Patient'. Test failed.");
            }
        } catch (Exception e) {
            // Fail the test if there's an issue finding the element or retrieving the text
            Assert.fail("An error occurred while checking the payer text: " + e.getMessage());
        }
    }
    private void handlePending() throws InterruptedException {
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
 
        // Click the dropdown once to open it
        try {
            WebElement actionsDropdown = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step4\"]/sl-dropdown[2]"));
            actionsDropdown.click();
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; // Stop the test if the dropdown is not found
        }
 
        // Loop through each button name and check if it is enabled or disabled
        for (String buttonName : buttons) {
            checkButtonState(buttonName);
            Thread.sleep(2000); // Pause for 2 seconds between checks
        }
        Thread.sleep(1000);
        WebElement Note = driver.findElement(By.xpath("//sl-dropdown[@id='myDropdown']"));
        Note.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), 'Add Note')]")));
        WebElement textArea = driver.findElement(By.xpath("//*[@id=\"myDropdown\"]/main/form/textarea"));
        textArea.sendKeys("Test case");
        WebElement save = driver.findElement(By.xpath("//*[@id=\"myDropdown\"]/main/ed-row/sl-button[2]"));
        save.click();
        try {
            WebElement paragraph = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step5\"]/div/div/p"));
            Assert.assertTrue(paragraph.isDisplayed(), "Paragraph <p> tag is displayed.");
        } catch (Exception e) {
        	Assert.fail("Paragraph <p> tag does not exist on the page.");
        }
        try {
            // Locate the element containing the payer text
            WebElement payerTextElement = driver.findElement(By.id("//*[@id=\"tour-guide-managing-claims-step1\"]/section[3]/ed-row[1]/label")); // Replace with the correct locator
 
            // Get the text of the element
            String payerText = payerTextElement.getText();
 
            // Check if the text contains "Primary" and does not contain "Patient"
            if (payerText.contains("Primary") && !payerText.contains("Patient")) {
            	Assert.assertTrue(true, "Text contains 'Primary' and does not contain 'Patient'. Test passed.");
            } else {
            	Assert.fail("Text does not contain 'Primary' or contains 'Patient'. Test failed.");
            }
        } catch (Exception e) {
            // Fail the test if there's an issue finding the element or retrieving the text
        	Assert.fail("An error occurred while checking the payer text: " + e.getMessage());
        }
    }
 
    private void handleVoided() throws InterruptedException {
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
 
        // Click the dropdown once to open it
        try {
            WebElement actionsDropdown = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step4\"]/sl-dropdown[2]"));
            actionsDropdown.click();
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; // Stop the test if the dropdown is not found
        }
 
        // Loop through each button name and check if it is enabled or disabled
        for (String buttonName : buttons) {
            checkButtonState(buttonName);
            Thread.sleep(2000); // Pause for 2 seconds between checks
        }
        Thread.sleep(1000);
        WebElement Note = driver.findElement(By.xpath("//sl-dropdown[@id='myDropdown']"));
        Note.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), 'Add Note')]")));
        WebElement textArea = driver.findElement(By.xpath("//*[@id=\"myDropdown\"]/main/form/textarea"));
        textArea.sendKeys("Test case");
        WebElement save = driver.findElement(By.xpath("//*[@id=\"myDropdown\"]/main/ed-row/sl-button[2]"));
        save.click();
        try {
            WebElement paragraph = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step5\"]/div/div/p"));
            Assert.assertTrue(paragraph.isDisplayed(), "Paragraph <p> tag is displayed.");
        } catch (Exception e) {
        	Assert.fail("Paragraph <p> tag does not exist on the page.");
        }
        try {
            // Locate the element containing the payer text
            WebElement payerTextElement = driver.findElement(By.id("//*[@id=\"tour-guide-managing-claims-step1\"]/section[3]/ed-row[1]/label")); // Replace with the correct locator
 
            // Get the text of the element
            String payerText = payerTextElement.getText();
 
            // Check if the text contains "Primary" and does not contain "Patient"
            if (payerText.contains("Primary") && !payerText.contains("Patient")) {
            	Assert.assertTrue(true, "Text contains 'Primary' and does not contain 'Patient'. Test passed.");
            } else {
            	Assert.fail("Text does not contain 'Primary' or contains 'Patient'. Test failed.");
            }
        } catch (Exception e) {
            // Fail the test if there's an issue finding the element or retrieving the text
        	Assert.fail("An error occurred while checking the payer text: " + e.getMessage());
        }
    }
 
    private void handleRejected() throws InterruptedException {
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
 
        // Click the dropdown once to open it
        try {
            WebElement actionsDropdown = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step4\"]/sl-dropdown[2]"));
            actionsDropdown.click();
        } catch (NoSuchElementException e) {
            System.out.println("Dropdown not found.");
            return; // Stop the test if the dropdown is not found
        }
 
        // Loop through each button name and check if it is enabled or disabled
        for (String buttonName : buttons) {
            checkButtonState(buttonName);
            Thread.sleep(2000); // Pause for 2 seconds between checks
        }
        Thread.sleep(1000);
        WebElement Note = driver.findElement(By.xpath("//sl-dropdown[@id='myDropdown']"));
        Note.click();
        waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(), 'Add Note')]")));
        WebElement textArea = driver.findElement(By.xpath("//*[@id=\"myDropdown\"]/main/form/textarea"));
        textArea.sendKeys("Test case");
        WebElement save = driver.findElement(By.xpath("//*[@id=\"myDropdown\"]/main/ed-row/sl-button[2]"));
        save.click();
        try {
            WebElement paragraph = driver.findElement(By.xpath("//*[@id=\"tour-guide-managing-claims-step5\"]/div/div/p"));
            Assert.assertTrue(paragraph.isDisplayed(), "Paragraph <p> tag is displayed.");
        } catch (Exception e) {
        	Assert.fail("Paragraph <p> tag does not exist on the page.");
        }
        try {
            // Locate the element containing the payer text
            WebElement payerTextElement = driver.findElement(By.id("//*[@id=\"tour-guide-managing-claims-step1\"]/section[3]/ed-row[1]/label")); // Replace with the correct locator
 
            // Get the text of the element
            String payerText = payerTextElement.getText();
 
            // Check if the text contains "Primary" and does not contain "Patient"
            if (payerText.contains("Primary") && !payerText.contains("Patient")) {
            	Assert.assertTrue(true, "Text contains 'Primary' and does not contain 'Patient'. Test passed.");
            } else {
            	Assert.fail("Text does not contain 'Primary' or contains 'Patient'. Test failed.");
            }
        } catch (Exception e) {
            // Fail the test if there's an issue finding the element or retrieving the text
        	Assert.fail("An error occurred while checking the payer text: " + e.getMessage());
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