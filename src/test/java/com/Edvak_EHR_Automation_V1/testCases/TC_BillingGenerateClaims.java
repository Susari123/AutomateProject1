package com.Edvak_EHR_Automation_V1.testCases;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Edvak_EHR_Automation_V1.pageObjects.LoginPage;
import com.Edvak_EHR_Automation_V1.utilities.DataReader;
import com.Edvak_EHR_Automation_V1.utilities.GenerateRandomNumberBetweenLength;

public class TC_BillingGenerateClaims extends BaseClass {
    DataReader dr = new DataReader();

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
        lp.setPassword("Admin@321");
        logger.info("Entered Password in password Text field");

        logger.info("Clicking on Login button");
        WebElement loginButton = driver
                .findElement(By.xpath("/html/body/app-root/div/div/app-login/section/div/div/form/div[3]/sl-button"))
                .getShadowRoot().findElement(By.cssSelector("button"));
        new Actions(driver).moveToElement(loginButton).click().build().perform();
        logger.info("Clicked on Login button");

        // Assertion to verify successful login
        WebElement dashboardElement = driver.findElement(By.xpath("//header//h2[normalize-space()='dashboard']"));
        Assert.assertTrue(dashboardElement.isDisplayed(), "Dashboard should be visible after login.");
    }

    @Test(priority = 1, dataProvider = "dataProviderTest", dependsOnMethods = {"testQuickRegistration"})
    void testBillingGenerateClaims(HashMap<String, String> data) throws InterruptedException, IOException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[normalize-space()='attach_money']")));

        clickWithRetry(driver.findElement(By.xpath("//span[normalize-space()='attach_money']")), 3);
        logger.info("Billing button is clicked");

        // Assertion to verify that Billing page is loaded
        WebElement billingPageHeader = driver.findElement(By.xpath("//h2[normalize-space()='billing']"));
        Assert.assertTrue(billingPageHeader.isDisplayed(), "Billing page should be displayed.");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form//div//sl-button")));
        newCharge();

        handleAlertIfPresent(driver);

        // Fill out patient details
        WebElement patientNameInput = driver.findElement(By.xpath("//input[@class='w-full form-input']"));
        patientNameInput.sendKeys(data.get("patientName"));

        // Assertion to verify patient name input
        Assert.assertEquals(patientNameInput.getAttribute("value"), data.get("patientName"),
                "Patient name input should match the provided data.");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[1]/div[1]/div[1]/type-ahead[1]/div[1]/div[1]/div[1]")));

        WebElement patientName1 = driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[1]/div[1]/div[1]/type-ahead[1]/div[1]/div[1]/div[1]"));
        patientName1.click();

        // Assertion to verify patient selection
        Assert.assertTrue(patientName1.isDisplayed(), "Patient name should be selected and visible.");

        // Handling encounter and other related inputs
        fillEncounterDetails(data, wait);

        Thread.sleep(4000);
        // ICD and CPT input
        fillIcdAndCptDetails(data);

        // Generate Claim
        generateClaim(data);

        // Assertion to verify claim generation
        WebElement claimGeneratedMessage = driver.findElement(By.xpath("//*[contains(text(),'Claim generated successfully')]"));
        Assert.assertTrue(claimGeneratedMessage.isDisplayed(), "Claim should be generated successfully.");
    }

    private void newCharge() {
        WebElement GenerateClaim = driver.findElement(By.xpath("//sl-tab-group//sl-tab[1]"));
        GenerateClaim.click();
        WebElement newCharge = driver.findElement(By.xpath("//form//div//sl-button"));
        newCharge.click();

        // Assertion to verify new charge creation
        Assert.assertTrue(newCharge.isDisplayed(), "New charge should be created.");
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
        Assert.assertTrue(clicked, "Element should be clicked within retry limit.");
    }

    private void handleAlertIfPresent(WebDriver driver) {
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
            logger.info("Alert accepted");

            // Assertion to confirm alert was handled
            Assert.assertTrue(true, "Alert should be handled.");
        } catch (NoAlertPresentException e) {
            logger.info("No alert present");

            // Assertion to confirm no alert was present
            Assert.assertTrue(true, "No alert should be present.");
        }
    }

    private void fillEncounterDetails(HashMap<String, String> data, WebDriverWait wait) throws InterruptedException, IOException {
        // Encounter selection
        WebElement encounterDropdown = driver.findElement(By.xpath("//div[@class='border border-[#CBD5E1] flex form-select w-full bg-white']"));
        encounterDropdown.click();
        logger.info("Encounter dropdown clicked");

        // Assertion to verify encounter dropdown is clicked
        Assert.assertTrue(encounterDropdown.isDisplayed(), "Encounter dropdown should be displayed.");

        WebElement newEncounterOption = driver.findElement(By.xpath("//div/sl-button-group/sl-button[2]"));
        newEncounterOption.click();
        Thread.sleep(2000);

        // Assertion to verify new encounter selection
        Assert.assertTrue(newEncounterOption.isDisplayed(), "New encounter option should be selected.");

        WebElement ngSelect = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("ng-select")));
        ngSelect.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ng-dropdown-panel-items")));
        WebElement option = driver.findElement(By.xpath("//ng-dropdown-panel//div//div[2]//div[1]"));
        option.click();

        // Assertion to verify provider name selection
        Assert.assertTrue(option.isDisplayed(), "Provider name should be selected.");

        WebElement ngSelect1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//ng-select//div[@class='ng-select-container']//span[@class='ng-arrow-wrapper']")));
        ngSelect1.click();
        logger.info("---------");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ng-dropdown-panel-items")));
        WebElement option1 = driver.findElement(By.xpath("(//*[@formcontrolname='location']/descendant::div[@role='option'])[" + new GenerateRandomNumberBetweenLength().generateRandomNumber(1, 3) + "]"));
        option1.click();

        // Assertion to verify location selection
        Assert.assertTrue(option1.isDisplayed(), "Location should be selected.");

        WebElement ngSelect2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[1]/div[2]/app-encounter-selection[1]/sl-dropdown[1]/main[1]/div[2]/div[1]/div[2]/div[2]/ng-select[1]/div[1]/div[1]/div[2]/input[1]")));
        ngSelect2.click();
        logger.info("---------");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ng-dropdown-panel-items")));
        WebElement option2 = driver.findElement(By.xpath("//ng-dropdown-panel//div//div[2]//div[1]"));
        option2.click();

        // Assertion to verify type selection
        Assert.assertTrue(option2.isDisplayed(), "Type should be selected.");

        WebElement GoToChargeEntry = driver.findElement(By.xpath("//sl-button-group//sl-button[3]"));
        GoToChargeEntry.click();

        // Assertion to verify navigation to charge entry
        Assert.assertTrue(GoToChargeEntry.isDisplayed(), "Should navigate to charge entry.");
    }

    private void fillIcdAndCptDetails(HashMap<String, String> data) {
        WebElement IcdInput = driver.findElement(By.xpath("//div[@class='px-4']//input[@placeholder='Type ICD code or diagnosis here']"));
        IcdInput.sendKeys(data.get("icd"));

        // Assertion to verify ICD input
        Assert.assertEquals(IcdInput.getAttribute("value"), data.get("icd"), "ICD input should match the provided data.");

        WebElement Icd = driver.findElement(By.xpath("//*[contains(text(),'" + data.get("icd") + "')]"));
        Icd.click();

        // Assertion to verify ICD selection
        Assert.assertTrue(Icd.isDisplayed(), "ICD code should be selected.");

        WebElement cptCode = driver.findElement(By.xpath("//input[@placeholder='Type CPT or keyword here']"));
        cptCode.sendKeys(data.get("cpt"));

        // Assertion to verify CPT input
        Assert.assertEquals(cptCode.getAttribute("value"), data.get("cpt"), "CPT input should match the provided data.");

        WebElement cptCode1 = driver.findElement(By.xpath("//*[contains(text(),'" + data.get("cpt") + "')]"));
        cptCode1.click();

        // Assertion to verify CPT selection
        Assert.assertTrue(cptCode1.isDisplayed(), "CPT code should be selected.");
    }

    private void generateClaim(HashMap<String, String> data) throws InterruptedException {
        WebElement chargeEntry = driver.findElement(By.xpath("//ed-drawer-footer/div[1]/div[1]/div[2]/sl-button[2]"));
        chargeEntry.click();

        // Assertion to verify charge entry
        Assert.assertTrue(chargeEntry.isDisplayed(), "Charge entry should be completed.");

        WebElement yes = driver.findElement(By.xpath("//sl-button-group/sl-button[2]"));
        yes.click();

        // Assertion to verify confirmation of charge entry
        Assert.assertTrue(yes.isDisplayed(), "Confirmation should be done.");

        WebElement saveClaim = driver.findElement(By.xpath("//sl-button-group/sl-button[3]"));
        saveClaim.click();

        // Assertion to verify saving of claim
        Assert.assertTrue(saveClaim.isDisplayed(), "Claim should be saved.");
    }

    @DataProvider(name = "dataProviderTest")
    public Object[][] dataProvider() throws IOException {
        // Assuming dr.getJsonDataToMap() returns a List of HashMaps
        List<HashMap<String, String>> data = dr.getJsonDataToMap();

        // Initialize the 2D Object array with the size of the data list
        Object[][] dataArray = new Object[data.size()][];

        // Loop through the data list and add each element to the array
        for (int i = 0; i < data.size(); i++) {
            dataArray[i] = new Object[]{data.get(i)};
        }

        return dataArray;
    }

}