package com.Edvak_EHR_Automation_V1.testCases;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Edvak_EHR_Automation_V1.pageObjects.BillingGenerateClaims;
import com.Edvak_EHR_Automation_V1.pageObjects.ManageClaimsPage;
import com.Edvak_EHR_Automation_V1.utilities.DataReader;
import com.Edvak_EHR_Automation_V1.utilities.GenerateRandomNumberBetweenLength;
import com.Edvak_EHR_Automation_V1.utilities.LoginUtils;
import com.Edvak_EHR_Automation_V1.utilities.TestData;


public class TC_BillingGenerateClaims extends BaseClass {
	DataReader dr = new DataReader();
    String encounterNumber ="";
    List<String> encounterNumbersList = new ArrayList<>();
    
   @Test(priority = 0)
public void testQuickRegistration() throws InterruptedException {
    logger.info("********Test Starts Here********");
    logger.info("'testQuickRegistrationWithValidData' test execution starts here:");

    // Use LoginUtils for login instead of repeating login steps
    LoginUtils.loginToApplication(driver, baseURL, "souravsusari311@gmail.com", "Edvak@3210");

    BillingGenerateClaims billingPage = new BillingGenerateClaims(driver);

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
    wait.until(ExpectedConditions.visibilityOf(billingPage.getBillingIconElement()));
    wait.until(ExpectedConditions.visibilityOf(billingPage.getDashboardElement()));

    Assert.assertTrue(billingPage.isDashboardDisplayed(), "Dashboard should be visible after login.");

    clickWithRetry(billingPage.getBillingIconElement(), 3);
    logger.info("Billing button is clicked");
}


@Test(priority = 1, dataProvider = "dataProviderTest", dependsOnMethods = {"testQuickRegistration"})
void testBillingGenerateClaims(HashMap<String, String> data) throws InterruptedException, IOException {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
    // Assertion to verify that Billing page is loaded
    BillingGenerateClaims billingPage = new BillingGenerateClaims(driver);

    // Page loading & Tab Selection
    billingPage.waitForBillingPageHeader(driver);
//       Assert.assertTrue(billingPageHeader.isDisplayed(), "Billing page should be displayed.");
    
   wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form//div//sl-button[@id='tour-guide-billing-Step4']")));
   wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr//td")));
    WebElement button = driver.findElement(By.xpath("//sl-tab-group//sl-tab[1]"));
   String ariaSelected = button.getAttribute("aria-selected");

   if (!"true".equals(ariaSelected)) {

       Assert.assertTrue(button.isDisplayed(), "Button is not visible!");
       button.click();
       System.out.println("Button clicked successfully!");

   } else {
       System.out.println("Button is already selected, no need to click.");
   }

   newCharge(billingPage);

    handleAlertIfPresent(driver);

    // Fill out patient details
  
    WebElement newChargeText = driver.findElement(By.xpath("//h6[contains(text(), 'New Charge')]"));
    String message = newChargeText.isDisplayed() ? "Visible" : "\u001B[31mNot Visible\u001B[0m"; 
    logger.info("The New charge Text button visibility: " + message);
    Assert.assertTrue(newChargeText.isDisplayed(), "The  New charge Text should be visible.");  
    WebElement printButton = driver.findElement(By.xpath("//ed-drawer-header/div[2]/sl-tooltip/sl-icon-button"));
    logger.info("The print button is " + (printButton.getAttribute("disabled") != null ? "disabled." : "enabled."));
    Assert.assertNotNull(printButton.getAttribute("disabled"), "The print button should be disabled.");

//        logger.info("The cross button visibility: " + (driver.findElement(By.xpath("//ed-drawer/ed-drawer-header/div[2]/sl-icon-button")).isDisplayed() ? "Visible" : "Not Visible")); Assert.assertTrue(driver.findElement(By.xpath("//ed-drawer/ed-drawer-header/div[2]/sl-icon-button")).isDisplayed(), "The cross button should be visible.");
    
    WebElement patientNameInput = driver.findElement(By.xpath("//input[@class='w-full form-input']"));
    patientNameInput.sendKeys(data.get("patientName"));
    Thread.sleep(100);
//         Assertion to verify patient name input
    Assert.assertEquals(patientNameInput.getAttribute("value"), data.get("patientName"),
            "Patient name input should match the provided data.");

    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[1]/div[1]/div[1]/type-ahead[1]/div[1]/div[1]/div[1]")));

    WebElement patientName1 = driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[1]/div[1]/div[1]/type-ahead[1]/div[1]/div[1]/div[1]"));
    patientName1.click();
    Thread.sleep(3000);
    // Handling encounter and other related inputs
    fillEncounterDetails(data, wait, billingPage);

    
    Thread.sleep(2000);
    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//img[contains(@src, 'loader.svg')]")));
    // ICD and CPT input
    fillIcdAndCptDetails(data, billingPage);


    // Generate Claim
    generateClaim(data);
    Thread.sleep(2000);

}

    public WebElement retryingFindElement(By by) {
        WebElement element = null;
        int attempts = 0;
        while (attempts < 3) {
            try {
                element = driver.findElement(by);
                break;
            } catch (StaleElementReferenceException e) {
                attempts++;
                logger.info("Attempt " + attempts + ": Element went stale. Retrying...");
            }
        }
        return element;
    }

    private void newCharge(BillingGenerateClaims billingPage) {
        // Click Charge Tab (Optional, depends on your test logic)
        billingPage.clickChargeTabIfNotSelected(driver);
    
        // Verify Filter Button Visibility
        WebElement filterButton = billingPage.getFilterButton();
        String filterMessage = filterButton.isDisplayed() ? "Visible" : "\u001B[31mNot Visible\u001B[0m";
        logger.info("The filter button visibility: " + filterMessage);
        Assert.assertTrue(filterButton.isDisplayed(), "The filter button should be visible.");
    
        // Verify Input Field Visibility
        WebElement inputField = billingPage.getFilterInputField();
        String inputMessage = inputField.isDisplayed() ? "Visible" : "\u001B[31mNot Visible\u001B[0m";
        logger.info("The input field visibility: " + inputMessage);
        Assert.assertTrue(inputField.isDisplayed(), "The input field should be visible.");
    
        // Click New Charge Button
        billingPage.clickNewChargeButton();
        logger.info("New Charge button is clicked.");
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

    private void handleAlertIfPresent(WebDriver driver) {
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
            logger.info("Alert accepted");

            // Assertion to confirm alert was handled
//            Assert.assertTrue(true, "Alert should be handled.");
        } catch (NoAlertPresentException e) {
            logger.info("No alert present");

            // Assertion to confirm no alert was present
//            Assert.assertTrue(true, "No alert should be present.");
        }
    }

    private void fillEncounterDetails(@SuppressWarnings("unused") HashMap<String, String> data, WebDriverWait wait, BillingGenerateClaims billingPage) throws InterruptedException, IOException {
        // Encounter selection
        billingPage.getEncounterSelectionDropdown().click();
        logger.info("Encounter dropdown clicked");
    
        billingPage.getNewEncounterSelectionOption().click();
        Thread.sleep(2000); // Optional: consider replacing with explicit wait
    
        // Provider Selection
        billingPage.getProviderDropdown().click();
        logger.info("Provider dropdown clicked");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ng-dropdown-panel-items")));
        WebElement providerOption = driver.findElement(By.xpath("//ng-dropdown-panel//div//div[2]//div[1]"));
        providerOption.click();
    
        // Location Selection
        billingPage.getLocationDropdown().click();
        logger.info("Location dropdown clicked");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ng-dropdown-panel-items")));
        long randomNumber = GenerateRandomNumberBetweenLength.generateRandomNumber(1, 3);
        WebElement randomLocationOption = driver.findElement(By.xpath("(//*[@formcontrolname='location']/descendant::div[@role='option'])[" + randomNumber + "]"));
        randomLocationOption.click();
    
        // Service Dropdown Selection
        billingPage.getServiceDropdown().click();
        logger.info("Service dropdown clicked");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ng-dropdown-panel-items")));
        WebElement serviceOption = driver.findElement(By.xpath("//ng-dropdown-panel//div//div[2]//div[1]"));
        serviceOption.click();
    
        // Date Input
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter expectedFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateToEnter = TestData.randomizePolicyDates().getFrom();
    
        billingPage.getEncounterDateInput().sendKeys(dateToEnter);
        logger.info("Date entered: " + dateToEnter);
        LocalDate parsedDate = LocalDate.parse(dateToEnter, inputFormatter);
        String expectedDate = parsedDate.format(expectedFormatter);
        String enteredDate = billingPage.getEncounterDateInput().getAttribute("value");
    
        logger.info("Confirmed entered date: " + enteredDate);
        Assert.assertEquals(enteredDate, expectedDate, "The entered date should match the date provided.");
    
        // Create Encounter
        billingPage.getCreateEncounterButton().click();
        logger.info("Create Encounter button clicked");
    
        // Wait for Encounter Number
        By encounterNumberLocator = By.xpath("//div[contains(text(), 'Encounter#:')]");
        wait.until(ExpectedConditions.visibilityOfElementLocated(encounterNumberLocator));
    
        String fullText = billingPage.getEncounterNumberDiv().getText().trim();
        String[] parts = fullText.split("Encounter#: ");
        if (parts.length > 1) {
            encounterNumber = parts[1].trim();
            logger.info("Encounter Number: " + encounterNumber);
            encounterNumbersList.add(encounterNumber);
        } else {
            logger.info("Encounter# not found");
        }
    }
    

    private void fillIcdAndCptDetails(HashMap<String, String> data, BillingGenerateClaims billingPage) throws InterruptedException {
        // Enter ICD Code
        WebElement icdInput = billingPage.getIcdInput(); // this is actually `icdInput` in your Page Object
        icdInput.sendKeys(data.get("icd"));
        Thread.sleep(2000); // This can be replaced with a better wait if needed
    
        // Select ICD Suggestion
        billingPage.getIcdSuggestionOption().click();
        logger.info("ICD Added");
        Assert.assertTrue(icdInput.isDisplayed(), "ICD code should be selected.");
    
        // Enter CPT Code
        WebElement cptInput = billingPage.getCptInput();
        cptInput.sendKeys(data.get("cpt"));
        Thread.sleep(3000);
    
        // Select CPT Suggestion
        billingPage.getCptSuggestionOption().click();
        logger.info("CPT Added");
        Assert.assertTrue(cptInput.isDisplayed(), "CPT code should be selected.");
    }
    

    private void generateClaim(HashMap<String, String> data) throws InterruptedException {
    	WebElement printButton = driver.findElement(By.xpath("//ed-drawer-header/div[2]/sl-tooltip/sl-icon-button"));
        logger.info("The print button is " + (printButton.isEnabled() ? "enabled." : "disabled."));
        Assert.assertTrue(printButton.isDisplayed(), "The icon button should be visible.");

        WebElement submitButton = driver.findElement(By.xpath("//ed-drawer//ed-drawer-footer//sl-button[1]"));
        submitButton.click();

        // Replace deprecated implicit wait with Duration
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Explicit wait example
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//img[contains(@src, 'loader.svg')]")));
        String dynamicXPath = "//table//tbody//tr//p[text()=' " + encounterNumber + " ']";
        System.out.println(dynamicXPath);
        WebElement claimRow = driver.findElement(By.xpath(dynamicXPath));
        claimRow.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//img[contains(@src, 'loader.svg')]")));
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr")));
        WebElement amountInput = driver.findElement(By.xpath("//tbody//tr//td[8]//input"));
        amountInput.sendKeys(data.get("amount"));
        

        String claimType = data.get("claimType").toLowerCase();  

        switch (claimType) {
        case "paper":
            // Perform paper claim actions
            logger.info("Processing as Paper Claim...");
            WebElement paperClaimOption = driver.findElement(By.xpath("//input[@id='paperClaim']"));
            paperClaimOption.click();
            logger.info("paper claim option is selected ");
            WebElement modifiersDropdown = driver.findElement(By.xpath("//app-ed-dropdown//div[1]"));
            modifiersDropdown.click();
            Thread.sleep(4000);
            WebElement modifierOption = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//*[@id='mod1']/descendant::button)[1]")));
            modifierOption.click();
            logger.info("modifiers entered ..");
            WebElement cptCodeIcon = driver.findElement(By.xpath("//*[@id=\"tour-guide-billing-encounter-step5\"]/sl-icon-button"));
            cptCodeIcon.click();
            WebElement cptCodeOption = driver.findElement(By.xpath("//ed-drawer-body//ul//li[25]"));
            cptCodeOption.click();
            logger.info("cpt code entered..");
            WebElement closeDrawer = driver.findElement(By.xpath("//ed-drawer-header//sl-icon-button"));
            closeDrawer.click();
            logger.info("tab closed..");
            Thread.sleep(200);
            WebElement modifiersDropdown1 = driver.findElement(By.xpath("//tr[2]/td[5]/div/div[1]/app-ed-dropdown/div"));
            modifiersDropdown1.click();
            Thread.sleep(4000);
            WebElement modifierOption1 = driver.findElement(By.xpath("(//*[@id='mod9']/descendant::button)[1]"));
            modifierOption1.click();
            logger.info("modifiers entered ..");
            WebElement serviceLineAmount = driver.findElement(By.xpath("//tr[2]/td[8]/input"));
            serviceLineAmount.sendKeys("300");
            break;

        case "electronic":
            // Perform electronic claim actions
            logger.info("Processing as Electronic Claim...");
            WebElement electronicClaimOption = driver.findElement(By.xpath("//input[@id='electronicClaim']"));
            electronicClaimOption.click();
            logger.info("electronic claim option is selected..");
            WebElement modifiersDropdown2 = driver.findElement(By.xpath("//app-ed-dropdown//div[1]"));
            modifiersDropdown2.click();
            logger.info("modifiers entered..");
            Thread.sleep(4000);
            // wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//*[@id='mod1']/descendant::button)[1]")));
            WebElement modifierOption2 = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//*[@id='mod1']/descendant::button)[1]")));
            modifierOption2.click();
            logger.info("modifiers option2 is selected..");
            WebElement cptCodeIcon1 = driver.findElement(By.xpath("//*[@id=\"tour-guide-billing-encounter-step5\"]/sl-icon-button"));
            cptCodeIcon1.click();
            WebElement cptCodeOption1 = driver.findElement(By.xpath("//ed-drawer-body//ul//li[25]"));
            cptCodeOption1.click();
            logger.info("cpt code entered..");
            WebElement closeDrawer1 = driver.findElement(By.xpath("//ed-drawer-header//sl-icon-button"));
            closeDrawer1.click();
            logger.info("tab closed..");
            Thread.sleep(200);
            WebElement modifiersDropdown12 = driver.findElement(By.xpath("//tr[2]/td[5]/div/div[1]/app-ed-dropdown/div"));
            modifiersDropdown12.click();
            Thread.sleep(4000);
            WebElement modifierOption12 = driver.findElement(By.xpath("(//*[@id='mod9']/descendant::button)[1]"));
            modifierOption12.click();
            logger.info("modifiers entered ..");
            WebElement serviceLineAmount2 = driver.findElement(By.xpath("//tr[2]/td[8]/input"));
            serviceLineAmount2.sendKeys("300");
            break;

        case "self":
        default:
            // Perform self-pay claim actions if claim type is "self" or invalid/missing
            logger.info("Processing as Self Pay Claim...");
            WebElement selfpay = driver.findElement(By.xpath("//div//descendant::ed-select"));
            selfpay.click();
            logger.info("self pay option selected..");
            WebElement self = driver.findElement(By.xpath("//div//descendant::ed-option-wrapper//ed-option[@value='self']"));
            self.click();
            logger.info("self pay clicked");
            break;
    }
        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebElement FromDate = driver.findElement(By.xpath("//input[@formcontrolname='dateFrom']"));
        String fromDateValue = (String) js.executeScript("return arguments[0].value;", FromDate);
        logger.info("The date value is: " + fromDateValue);
        WebElement ToDate = driver.findElement(By.xpath("//input[@formcontrolname='dateTo']"));
        String toDateValue = (String) js.executeScript("return arguments[0].value;", ToDate);
        logger.info("The date value is: " + toDateValue);
        
        String nameInClaim = driver.findElement(By.xpath("//div[@class='flex items-center']//h6")).getText();
        String patientName1 = data.get("patientName");
        System.out.println(patientName1);
        if(nameInClaim.equals(patientName1)) {
        	logger.info("Patient name is matching .");
        }

        Map<String, String> buttonXpaths = new HashMap<>();
        buttonXpaths.put("Notes & Facesheet", "//sl-button[contains(text(), ' Notes & Facesheet ')]");
        buttonXpaths.put("Attach Documents", "//sl-button[contains(text(), 'Documents')]");
        buttonXpaths.put("Super Bill", "//sl-button[contains(text(), ' Super Bill ')]");
        buttonXpaths.put("Create Task", "//sl-button[contains(text(), ' Create Task ')]");
        buttonXpaths.put("Tools", "//sl-button[contains(text(), 'Tools')]");
        buttonXpaths.put("DarwinApi", "//*[@id=\"codingComponent\"]/header/div[1]/section");

        // Iterate over each entry in the map and assert presence
        for (Map.Entry<String, String> entry : buttonXpaths.entrySet()) {
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
        WebElement orderingProvider = driver.findElement(By.xpath("//section[@id='tour-guide-billing-encounter-step1']//descendant::ng-select[@placeholder='Select Ordering Provider']"));
        orderingProvider.click();
        logger.info("clicked ordering provider");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//ng-dropdown-panel//div//div//div[1]")));
        WebElement orderingProvideroption = driver.findElement(By.xpath("//ng-dropdown-panel//div//div//div[1]"));
        orderingProvideroption.click();
        logger.info("orderingProvideroption entered");
//        Select Referring Provider
        WebElement ReferringProvider = driver.findElement(By.xpath("//section[@id='tour-guide-billing-encounter-step1']//descendant::ng-select[@placeholder='Select Referring Provider']"));
        ReferringProvider.click();
        logger.info("ReferringProvider");
        WebElement ReferringProvideroption = driver.findElement(By.xpath("//ng-dropdown-panel//div//div//div[4]"));
        ReferringProvideroption.click();
        logger.info("ReferringProvideroption");
        
        WebElement supervisingProvider = driver.findElement(By.xpath("//section[@id='tour-guide-billing-encounter-step1']//descendant::ng-select[@placeholder='Select Supervising Provider']"));
        supervisingProvider.click();
        WebElement supervisingProviderOption = driver.findElement(By.xpath("//ng-dropdown-panel//div//div//div[3]"));
        supervisingProviderOption.click();
        
        WebElement serviceToDate = driver.findElement(By.xpath("//input[@formcontrolname='DOS_start']"));
        String serviceToDateValue = (String) js.executeScript("return arguments[0].value;", serviceToDate);
        logger.info("The date value is: " + serviceToDateValue);
        
        WebElement serviceFromDate = driver.findElement(By.xpath("//input[@formcontrolname='DOS_start']"));
        String serviceFromDateValue = (String) js.executeScript("return arguments[0].value;", serviceFromDate);
        logger.info("The date value is: " + serviceFromDateValue);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            // Parse dates using the formatter
            LocalDate serviceTo = LocalDate.parse(serviceToDateValue.trim(), formatter);
            LocalDate serviceFrom = LocalDate.parse(serviceFromDateValue.trim(), formatter);
            LocalDate from = LocalDate.parse(fromDateValue.trim(), formatter);
            LocalDate to = LocalDate.parse(toDateValue.trim(), formatter);

            // Check if ServiceToDate is less than or equal to ServiceFromDate
            boolean isServiceToDateLessThanOrEqualToServiceFromDate = serviceTo.isBefore(serviceFrom) || serviceTo.isEqual(serviceFrom);
            logger.info("ServiceToDate <= ServiceFromDate: " + isServiceToDateLessThanOrEqualToServiceFromDate);
            Assert.assertTrue(isServiceToDateLessThanOrEqualToServiceFromDate, "ServiceToDate should be less than or equal to ServiceFromDate.");

            // Check if ServiceToDate and ServiceFromDate are within the range FromDate to ToDate
            boolean isServiceToDateInRange = (serviceTo.isAfter(from) || serviceTo.isEqual(from)) && (serviceTo.isBefore(to) || serviceTo.isEqual(to));
            boolean isServiceFromDateInRange = (serviceFrom.isAfter(from) || serviceFrom.isEqual(from)) && (serviceFrom.isBefore(to) || serviceFrom.isEqual(to));
            
            logger.info("ServiceToDate is within range FromDate to ToDate: " + isServiceToDateInRange);
            logger.info("ServiceFromDate is within range FromDate to ToDate: " + isServiceFromDateInRange);

            // Assert the range conditions
            Assert.assertTrue(isServiceToDateInRange, "ServiceToDate should be within the range FromDate to ToDate.");
            Assert.assertTrue(isServiceFromDateInRange, "ServiceFromDate should be within the range FromDate to ToDate.");

        } catch (DateTimeParseException e) {
            logger.error("Failed to parse date. Ensure the date format is yyyy-MM-dd. Error: " + e.getMessage());
        }
        Object mode = data.get("Mode");
        System.out.println(mode);
		if("electronic".equals(claimType) && "GenerateandTransmit".equals(mode)) {
        WebElement transmit = driver.findElement(By.xpath("//footer//sl-button[4]"));
        logger.info("tansmitted");
        transmit.click();
        logger.info("claim transmitted");
        }
        else {
        WebElement generateClaimButton = driver.findElement(By.xpath("//sl-button[contains(text(), 'Generate Claim')]"));
        generateClaimButton.click();
        logger.info("Claim generated successfully.");     
        System.out.println("-------------------------------------------------------------------------------------------------------------------");
        }
    }
    @Test(priority = 2, dependsOnMethods = {"testBillingGenerateClaims"})
public void verifyEncountersInManageClaims() throws InterruptedException {
    // Path to the file
    String filePath = "C:\\Users\\sksusari\\Documents\\Test\\encounter_presence.json";

    // Clear the file by writing empty content
    try (FileWriter file = new FileWriter(filePath)) {
        file.write("");  // Clears the file content
        logger.info("Previous encounter presence data file cleared.");
    } catch (IOException e) {
        logger.error("Error clearing the previous encounter presence data file.", e);
    }

    WebElement manageClaim = retryingFindElement(ManageClaimsPage.MANAGE_CLAIMS_TAB);
    manageClaim.click();
    logger.info("Navigated to manage claim page.");

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    wait.until(ExpectedConditions.invisibilityOfElementLocated(ManageClaimsPage.LOADER_ICON));

    WebElement transmitButton = driver.findElement(ManageClaimsPage.TRANSMIT_BUTTON);
    boolean isTransmitDisplayed = transmitButton.isDisplayed();
    logger.info("Checking if Transmit button is displayed. Result: " + isTransmitDisplayed);
    Assert.assertTrue(isTransmitDisplayed, "Transmit button should be displayed.");

    List<WebElement> claimIdElements = fetchClaimIdElements(driver);
    List<WebElement> statusElements = fetchStatusElements(driver);

    // Ensure required number of elements are fetched
    int requiredCount = encounterNumbersList.size();
    while (claimIdElements.size() < requiredCount || statusElements.size() < requiredCount) {
        scrollPage(driver);
        claimIdElements = fetchClaimIdElements(driver);
        statusElements = fetchStatusElements(driver);
    }

    // Extract claim IDs and status
    List<String> claimIds = claimIdElements.stream()
            .limit(requiredCount)
            .map(WebElement::getText)
            .collect(Collectors.toList());

    List<String> claimStatuses = statusElements.stream()
            .limit(requiredCount)
            .map(WebElement::getText)
            .collect(Collectors.toList());

    // Create JSON structure for claim data
    JSONArray encounterPresenceArray = new JSONArray();
    for (int i = 0; i < requiredCount; i++) {
        JSONObject claimObject = new JSONObject();
        claimObject.put("claim_id", claimIds.get(i));
        claimObject.put("status", claimStatuses.get(i));
        System.out.println("Claim ID extracted: " + claimIds.get(i) + " | Status: " + claimStatuses.get(i));
        encounterPresenceArray.put(claimObject);
    }

    // Root JSON object to store claim presence data
    JSONObject json = new JSONObject();
    json.put("claim_presence_status", encounterPresenceArray);

    // Write JSON data to file
    try (FileWriter file = new FileWriter(filePath)) {
        file.write(json.toString(4));
        System.out.println("Claim presence data saved to " + filePath);
    } catch (IOException e) {
        System.err.println("Error writing claim presence data to JSON file: " + filePath + " - " + e.getMessage());
         // Keep this only if you still need stack trace visibility
    }

    // Assert the correct number of claims were fetched
    Assert.assertEquals(claimIds.size(), requiredCount, "The number of extracted claims should match the required count.");
    Assert.assertEquals(claimStatuses.size(), requiredCount, "The number of extracted statuses should match the required count.");
}

// Scrolls down the page using JavaScript Executor
    private static void scrollPage(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        try {
        js.executeScript("window.scrollBy(0, 500);"); // Adjust scroll value as needed
        Thread.sleep(500); // Allow time for loading
         } catch (InterruptedException e) {
        Thread.currentThread().interrupt(); // Restore the interrupted status
        // Optional: Use a logging framework like Log4j or SLF4J instead of e.printStackTrace()
        System.err.println("Thread was interrupted during scroll wait: " + e.getMessage());
    }
}

// Fetch Claim ID Elements
private static List<WebElement> fetchClaimIdElements(WebDriver driver) {
    return driver.findElements(By.xpath("//*[@id='tour-guide-billing-claims-step4']/td[1]/div/div/div/p"));
}

// Fetch Status Elements
private static List<WebElement> fetchStatusElements(WebDriver driver) {
    return driver.findElements(By.xpath("//*[@id='tour-guide-billing-claims-step4']/td[1]/div/div/div/div/span/sl-badge"));
}


@DataProvider(name = "dataProviderTest")
public Object[][] dataProvider() throws IOException {
    // Default value if not set
    int count = 3;
    String taskCount = System.getProperty("taskCount");
    if (taskCount != null) {
        try {
            count = Integer.parseInt(taskCount);
        } catch (NumberFormatException e) {
            // Log the error and fallback to default
            System.err.println("Invalid task count provided, using default: " + e.getMessage());
        }
    }

    List<HashMap<String, String>> limitedData = dr.getLimitedJsonData(count);
    Object[][] dataArray = new Object[limitedData.size()][1];

    for (int i = 0; i < limitedData.size(); i++) {
        dataArray[i][0] = limitedData.get(i);
    }
    return dataArray;
}


}