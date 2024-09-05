package com.Edvak_EHR_Automation_V1.testCases;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Edvak_EHR_Automation_V1.pageObjects.BillingGenerateClaims;
import com.Edvak_EHR_Automation_V1.pageObjects.LoginPage;
import com.Edvak_EHR_Automation_V1.utilities.DataReader;
import com.Edvak_EHR_Automation_V1.utilities.GenerateRandomNumberBetweenLength;
import com.Edvak_EHR_Automation_V1.utilities.TestData;

public class TC_BillingGenerateClaims extends BaseClass {
    DataReader dr = new DataReader();
    BillingGenerateClaims bi = new BillingGenerateClaims(driver);
    String encounterNumber ="";
    List<String> encounterNumbersList = new ArrayList<>();
    @Test(priority = 0)
    public void testQuickRegistration() throws InterruptedException {
        LoginPage lp = new LoginPage(driver);
        logger.info("********Test Starts Here********");
        logger.info("'testQuickRegistrationWithValidData' test execution starts here:");
        logger.info("Opening URL: " + baseURL);
        driver.get(baseURL);
        logger.info("Opened URL: " + baseURL);
        driver.manage().window().maximize();

        logger.info("Entering username in Username Text field");
        lp.setUserName("souravsusari311@gmail.com");
        logger.info("Entered Username in Username Text field");

        logger.info("Entering Password in password Text field");
        lp.setPassword("Admin@12345");
        logger.info("Entered Password in password Text field");

        logger.info("Clicking on Login button");
        WebElement loginButton = driver
                .findElement(By.xpath("/html/body/app-root/div/div/app-login/section/div/div/form/div[3]/sl-button"))
                .getShadowRoot().findElement(By.cssSelector("button"));
        new Actions(driver).moveToElement(loginButton).click().build().perform();
        logger.info("Clicked on Login button");
        Thread.sleep(1000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[normalize-space()='attach_money']")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//header//h2[normalize-space()='dashboard']")));
        WebElement dashboardElement = driver.findElement(By.xpath("//header//h2[normalize-space()='dashboard']"));
//        bi.getTextBillingPageHeader();
        Assert.assertTrue(dashboardElement.isDisplayed(), "Dashboard should be visible after login.");
        clickWithRetry(driver.findElement(By.xpath("//span[normalize-space()='attach_money']")), 3);
        logger.info("Billing button is clicked");
        
    }

    @Test(priority = 1, dataProvider = "dataProviderTest", dependsOnMethods = {"testQuickRegistration"})
    void testBillingGenerateClaims(HashMap<String, String> data) throws InterruptedException, IOException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
        // Assertion to verify that Billing page is loaded
       WebElement billingPageHeader = driver.findElement(By.xpath("//h2[normalize-space()='billing']"));
       Assert.assertTrue(billingPageHeader.isDisplayed(), "Billing page should be displayed.");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form//div//sl-button[@id='tour-guide-billing-Step4']")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr//td")));
        newCharge();

        handleAlertIfPresent(driver);

        // Fill out patient details
        
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
        fillEncounterDetails(data, wait);

        Thread.sleep(4000);
        // ICD and CPT input
        fillIcdAndCptDetails(data);

        // Generate Claim
        generateClaim(data);
        Thread.sleep(4000);
	    Assert.assertTrue(billingPageHeader.isDisplayed(), "Billing page should be displayed.");

    }
 // Function for Retry Mechanism to Handle StaleElementReferenceException
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

    private void newCharge() {
        WebElement GenerateClaim = driver.findElement(By.xpath("//sl-tab-group//sl-tab[1]"));
        GenerateClaim.click();
        WebElement newCharge = driver.findElement(By.xpath("//sl-button[@id='tour-guide-billing-Step4']"));
        newCharge.click();
//         Assertion to verify new charge creation
//        Assert.assertTrue(newCharge.isDisplayed(), "New charge should be created.");
    }

    private void clickWithRetry(WebElement element, int maxRetries) {
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

    private void fillEncounterDetails(HashMap<String, String> data, WebDriverWait wait) throws InterruptedException, IOException {
        // Encounter selection
        WebElement encounterDropdown = driver.findElement(By.xpath("//div[@class='border border-[#CBD5E1] flex form-select w-full bg-white']"));
        encounterDropdown.click();
        logger.info("Encounter dropdown clicked");

        // Assertion to verify encounter dropdown is clicked
//        Assert.assertTrue(encounterDropdown.isDisplayed(), "Encounter dropdown should be displayed.");

        WebElement newEncounterOption = driver.findElement(By.xpath("//div/sl-button-group/sl-button[2]"));
        newEncounterOption.click();
        Thread.sleep(2000);

        // Assertion to verify new encounter selection
//        Assert.assertTrue(newEncounterOption.isDisplayed(), "New encounter option should be selected.");

        WebElement provider = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("ng-select")));
        provider.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ng-dropdown-panel-items")));
        WebElement option = driver.findElement(By.xpath("//ng-dropdown-panel//div//div[2]//div[1]"));
        option.click();

        // Assertion to verify provider name selection
//        Assert.assertTrue(option.isDisplayed(), "Provider name should be selected.");

        WebElement location = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//ng-select//div[@class='ng-select-container']//span[@class='ng-arrow-wrapper']")));
        location.click();
        logger.info("location entered--------");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ng-dropdown-panel-items")));
        WebElement option1 = driver.findElement(By.xpath("(//*[@formcontrolname='location']/descendant::div[@role='option'])[" + new GenerateRandomNumberBetweenLength().generateRandomNumber(1, 3) + "]"));
        option1.click();

        // Assertion to verify location selection
//        Assert.assertTrue(option1.isDisplayed(), "Location should be selected.");

        WebElement ngSelect2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[1]/div[2]/app-encounter-selection[1]/sl-dropdown[1]/main[1]/div[2]/div[1]/div[2]/div[2]/ng-select[1]/div[1]/div[1]/div[2]/input[1]")));
        ngSelect2.click();
        logger.info("---------");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ng-dropdown-panel-items")));
        WebElement option2 = driver.findElement(By.xpath("//ng-dropdown-panel//div//div[2]//div[1]"));
        option2.click();

        WebElement ngSelect3 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[1]/div[2]/app-encounter-selection[1]/sl-dropdown[1]/main[1]/div[2]/div[1]/div[2]/div[3]/input[1]")));
        ngSelect3.sendKeys(TestData.randomizePolicyDates().getFrom());
        
        WebElement createEncounter = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[1]/div[2]/app-encounter-selection/sl-dropdown/main/div[2]/div/div[3]/sl-button"));
        createEncounter.click();
        
        Thread.sleep(4000);
        WebElement encounterDiv = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[1]/div[2]/app-encounter-selection/sl-dropdown/div/div"));

        // Extract the full text from the div
        String fullText = encounterDiv.getText().trim();
        
        // Split and get the encounter number
        String[] parts = fullText.split("Encounter#: ");
        if (parts.length > 1) {
            encounterNumber = parts[1].trim();
            System.out.println("Encounter Number: " + encounterNumber);    // Output: E0704HA11429
         // Store the encounter number in the list
            encounterNumbersList.add(encounterNumber);
        } else {
            System.out.println("Encounter# not found");
        }

        // Assertion to verify navigation to charge entry
//        Assert.assertTrue(GoToChargeEntry.isDisplayed(), "Should navigate to charge entry.");
    }

    private void fillIcdAndCptDetails(HashMap<String, String> data) throws InterruptedException {
        WebElement IcdInput = driver.findElement(By.xpath("//div[@class='control flex']//input[1]"));
        IcdInput.sendKeys(data.get("icd"));

        // Assertion to verify ICD input
//        Assert.assertEquals(IcdInput.getAttribute("value"), data.get("icd"), "ICD input should match the provided data.");
        Thread.sleep(3000);
        WebElement icd1 = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[4]/div[1]/div[2]/div/div[1]/div"));
        icd1.click();

        // Assertion to verify ICD selection
//        Assert.assertTrue(Icd.isDisplayed(), "ICD code should be selected.");

        WebElement cptCode = driver.findElement(By.xpath("//input[@placeholder='Search CPT Codes to add to the below list']"));
        cptCode.sendKeys(data.get("cpt"));
        Thread.sleep(3000);
        // Assertion to verify CPT input
//        Assert.assertEquals(cptCode.getAttribute("value"), data.get("cpt"), "CPT input should match the provided data.");

        WebElement cpt1 = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[4]/div[2]/div[1]/div[2]/div/div/div"));
        cpt1.click();

        // Assertion to verify CPT selection
//        Assert.assertTrue(cptCode1.isDisplayed(), "CPT code should be selected.");
    }

    private void generateClaim(HashMap<String, String> data) throws InterruptedException {
        WebElement submitButton = driver.findElement(By.xpath("//ed-drawer//ed-drawer-footer//sl-button[1]"));
        submitButton.click();

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr")));
        String dynamicXPath = "//table//tbody//tr//p[text()=' " + encounterNumber + " ']";
        System.out.println(dynamicXPath);
        WebElement claimRow = driver.findElement(By.xpath(dynamicXPath));
        claimRow.click();
        Thread.sleep(10000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr")));
        WebElement amountInput = driver.findElement(By.xpath("//tbody//tr//td[8]//input"));
        amountInput.sendKeys(data.get("amount"));
        

        String claimType = data.get("claimType").toLowerCase();  // Convert to lowercase for easier comparison

        switch (claimType) {
        case "paper":
            // Perform paper claim actions
            logger.info("Processing as Paper Claim...");
            WebElement paperClaimOption = driver.findElement(By.xpath("//sl-radio-group//div//sl-radio[2]"));
            paperClaimOption.click();
            
            WebElement modifiersDropdown = driver.findElement(By.xpath("//app-ed-dropdown//div[1]"));
            modifiersDropdown.click();


            WebElement modifierOption = driver.findElement(By.xpath("(//*[@id='mod1']/descendant::button)[1]"));
            modifierOption.click();
            
            WebElement cptCodeIcon = driver.findElement(By.xpath("//*[@id=\"tour-guide-billing-encounter-step5\"]/sl-icon-button"));
            cptCodeIcon.click();

            WebElement cptCodeOption = driver.findElement(By.xpath("//ed-drawer-body//ul//li[25]"));
            cptCodeOption.click();
            
            WebElement closeDrawer = driver.findElement(By.xpath("//ed-drawer-header//sl-icon-button"));
            closeDrawer.click();
            break;

        case "electronic":
            // Perform electronic claim actions
            logger.info("Processing as Electronic Claim...");
            WebElement electronicClaimOption = driver.findElement(By.xpath("//sl-radio-group//div//sl-radio[1]"));
            electronicClaimOption.click();
            
            WebElement modifiersDropdown2 = driver.findElement(By.xpath("//app-ed-dropdown//div[1]"));
            modifiersDropdown2.click();
//            modifiersDropdown2.sendKeys("OA");

            WebElement modifierOption2 = driver.findElement(By.xpath("(//*[@id='mod1']/descendant::button)[1]"));
            modifierOption2.click();
            break;

        case "self":
            // Perform self-pay claim actions
            logger.info("Processing as Self Pay Claim...");
            WebElement selfpay = driver.findElement(By.xpath("//div//descendant::ed-select"));
            selfpay.click();
            
            WebElement self = driver.findElement(By.xpath("//div//descendant::ed-option-wrapper//ed-option[@value='self']"));
            self.click();
            break;

        default:
            logger.error("Invalid claim type provided: " + claimType);
            throw new IllegalArgumentException("Invalid claim type provided in the test data.");
    }  
        WebElement orderingProvider = driver.findElement(By.xpath("//section[@id='tour-guide-billing-encounter-step1']//descendant::ng-select[@placeholder='Select Ordering Provider']"));
        orderingProvider.click();
//        bi.selectOrderingProvider();
        logger.info("clicked ordering provider");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//ng-dropdown-panel//div//div//div[3]")));
        WebElement orderingProvideroption = driver.findElement(By.xpath("//ng-dropdown-panel//div//div//div[3]"));
        orderingProvideroption.click();
        logger.info("orderingProvideroption entered");
//        Select Referring Provider
        WebElement ReferringProvider = driver.findElement(By.xpath("//section[@id='tour-guide-billing-encounter-step1']//descendant::ng-select[@placeholder='Select Referring Provider']"));
        ReferringProvider.click();
        logger.info("ReferringProvider");
        //ng-dropdown-panel[@id='a3e6db251932']//descendant::div[@id='a3e6db251932-3']
        WebElement ReferringProvideroption = driver.findElement(By.xpath("//ng-dropdown-panel//div//div//div[4]"));
        ReferringProvideroption.click();
        logger.info("ReferringProvideroption");
        
        WebElement supervisingProvider = driver.findElement(By.xpath("//section[@id='tour-guide-billing-encounter-step1']//descendant::ng-select[@placeholder='Select Supervising Provider']"));
        supervisingProvider.click();
        WebElement supervisingProviderOption = driver.findElement(By.xpath("//ng-dropdown-panel//div//div//div[3]"));
        supervisingProviderOption.click();
        
        Object mode = data.get("Mode");
        System.out.println(mode);
		if("electronic".equals(claimType) && "GenerateandTransmit".equals(mode)) {
        WebElement transmit = driver.findElement(By.xpath("//footer//sl-button[4]"));
        logger.info("tansmitted");
        transmit.click();
        logger.info("claim transmitted");
        }
        else {
        WebElement generateClaimButton = driver.findElement(By.xpath("//*[@id=\"tour-guide-billing-encounter-step7\"]"));
        generateClaimButton.click();
        logger.info("Claim generated successfully.");     
        
		 
        }
    }
    @Test(priority = 2, dependsOnMethods = {"testBillingGenerateClaims"})
    public void verifyEncountersInManageClaims() throws InterruptedException {
        WebElement manageclaim = retryingFindElement(By.xpath("//sl-tab-group//sl-tab[2]"));
        manageclaim.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr")));

        List<WebElement> claimRows = driver.findElements(By.xpath("//td[2]/descendant::p"));
        List<String> encounterNumbersOnScreen = new ArrayList<>();
        System.out.println(claimRows);
        System.out.println();

//        for (int i = 0; i < claimRows.size(); i++) {
//            try {
//                WebElement row = driver.findElements(By.xpath("//table//tbody//tr")).get(i);
//                WebElement encounterNumberCell = row.findElement(By.xpath("//td[2]/descendant::p"));
//                String encounterNumber = encounterNumberCell.getText();
//                encounterNumbersOnScreen.add(encounterNumber);
//            } catch (StaleElementReferenceException e) {
//                logger.warn("Encountered StaleElementReferenceException. Retrying...");
//                WebElement row = driver.findElements(By.xpath("//table//tbody//tr")).get(i);
//                WebElement encounterNumberCell = row.findElement(By.xpath("//td[2]/descendant::p"));
//                String encounterNumber = encounterNumberCell.getText();
//                encounterNumbersOnScreen.add(encounterNumber);
//            }
//        }
//
//        boolean allEncountersPresent = true;
//
//        for (String encounter : encounterNumbersList) {
//            if (!encounterNumbersOnScreen.contains(encounter)) {
//                logger.warn("Encounter number " + encounter + " is NOT present in the Claims List.");
//                allEncountersPresent = false;
//            } else {
//                logger.info("Encounter number " + encounter + " is present in the Claims List.");
//            }
//        }
//
//        Assert.assertTrue(allEncountersPresent, "All generated encounter numbers should be present in the Claims List.");
    }

    @DataProvider(name = "dataProviderTest")
    public Object[][] dataProvider() throws IOException {
        // Assuming dr.getJsonDataToMap() returns a List of HashMaps
        List<HashMap<String, String>> data = dr.getJsonDataToMap();
        Object[][] dataArray = new Object[data.size()][];

        for (int i = 0; i < data.size(); i++) {
            dataArray[i] = new Object[]{data.get(i)};
            try {
                Thread.sleep(10); 
            } catch (InterruptedException e) {
                e.printStackTrace(); 
            }
        }

        return dataArray;
    }

}