package com.Edvak_EHR_Automation_V1.testCases;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Edvak_EHR_Automation_V1.pageObjects.LoginPage;
import com.Edvak_EHR_Automation_V1.pageObjects.QuickRegisterPage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.Edvak_EHR_Automation_V1.pageObjects.FacesheetAllergies;
import com.Edvak_EHR_Automation_V1.utilities.DataReader;
import com.Edvak_EHR_Automation_V1.utilities.GenerateRandomNumberBetweenLength;
import com.Edvak_EHR_Automation_V1.utilities.GenerateRandomNumberOfLengthN;
import com.Edvak_EHR_Automation_V1.utilities.TestData;

public class TC_BillingGenerateClaims extends BaseClass {
	DataReader dr = new DataReader();
	GenerateRandomNumberBetweenLength random= new GenerateRandomNumberBetweenLength();
	TestData td = new TestData();


    @Test(priority = 0)
    public void testQuickRegistration() throws InterruptedException {
        LoginPage lp = new LoginPage(driver);
        QuickRegisterPage qr = new QuickRegisterPage(driver);
        FacesheetAllergies fs = new FacesheetAllergies(driver);

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
    }

    @Test(priority = 1, dataProvider = "dataProviderTest", dependsOnMethods = {"testQuickRegistration"})
    void testBillingGenerateClaims(HashMap<String, String> data) throws InterruptedException, IOException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[normalize-space()='attach_money']")));

        WebElement billingButton = driver.findElement(By.xpath("//span[normalize-space()='attach_money']"));
        clickWithRetry(billingButton, 3);
        logger.info("Billing button is clicked");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='flex gap-sm items-center ml-auto']//sl-button")));
        WebElement newCharge = driver.findElement(By.xpath("//div[@class='flex gap-sm items-center ml-auto']//sl-button"));
        newCharge.click();
        handleAlertIfPresent(driver);

        // Fill out patient details
        WebElement patientNameInput = driver.findElement(By.xpath("//input[@class='w-full form-input']"));
        patientNameInput.sendKeys(data.get("patientName"));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[1]/div[1]/div[1]/type-ahead[1]/div[1]/div[1]/div[1]")));

        
        Thread.sleep(400);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[1]/div[1]/div[1]/type-ahead[1]/div[1]/div[1]/div[1]")));
        WebElement patientName1 = driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[1]/div[1]/div[1]/type-ahead[1]/div[1]/div[1]/div[1]"));
        patientName1.click();
        
        // Handling encounter and other related inputs
        fillEncounterDetails(data, wait);
        

        Thread.sleep(4000);
        // ICD and CPT input
        fillIcdAndCptDetails(data);

        // Generate Claim
        generateClaim(data);
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
    }

    private void handleAlertIfPresent(WebDriver driver) {
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
            logger.info("Alert accepted");
        } catch (NoAlertPresentException e) {
            logger.info("No alert present");
        }
    }

    private void fillEncounterDetails(HashMap<String, String> data, WebDriverWait wait) throws InterruptedException, IOException {
        // Encounter selection
        WebElement encounterDropdown = driver.findElement(By.xpath("//div[@class='border border-[#CBD5E1] flex form-select w-full bg-white']"));
        encounterDropdown.click();
        logger.info("Encounter dropdown clicked");

        WebElement newEncounterOption = driver.findElement(By.xpath("//div/sl-button-group/sl-button[2]"));
        newEncounterOption.click();
        Thread.sleep(2000);
//        

        WebElement ngSelect = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("ng-select")));
        ngSelect.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ng-dropdown-panel-items")));
        WebElement option = driver.findElement(By.xpath("//ng-dropdown-panel//div//div[2]//div[1]"));
        option.click();
        
        logger.info("provider name entered");
        WebElement ngSelect1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//ng-select//div[@class='ng-select-container']//span[@class='ng-arrow-wrapper']")));
        ngSelect1.click();
        logger.info("---------");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ng-dropdown-panel-items")));
        WebElement option1 = driver.findElement(By.xpath("(//*[@formcontrolname='location']/descendant::div[@role='option'])["+random.generateRandomNumber(1,3)+"]"));
        option1.click();
    	
//        (//*[@formcontrolname='location']/descendant::div[@role='option'])[2]
        
        WebElement ngSelect2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[1]/div[2]/app-encounter-selection[1]/sl-dropdown[1]/main[1]/div[2]/div[1]/div[2]/div[2]/ng-select[1]/div[1]/div[1]/div[2]/input[1]")));
        ngSelect2.click();
        logger.info("---------");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ng-dropdown-panel-items")));
        WebElement option2 = driver.findElement(By.xpath("//ng-dropdown-panel//div//div[2]//div[1]"));
        option2.click();    
        
        WebElement ngSelect3 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[1]/div[2]/app-encounter-selection[1]/sl-dropdown[1]/main[1]/div[2]/div[1]/div[2]/div[3]/input[1]")));
        ngSelect3.sendKeys(TestData.randomizePolicyDates().getFrom());
        
        WebElement createEncounter = driver.findElement(By.xpath("//div//div//div[3]//sl-button"));
        createEncounter.click();
       
    }
  

    private void fillIcdAndCptDetails(HashMap<String, String> data) throws InterruptedException {
    	WebElement icd = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[4]/div[1]/div[1]/div/input"));
        icd.sendKeys(data.get("icd"));
        Thread.sleep(3000);
        WebElement icd1 = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[4]/div[1]/div[2]/div/div[1]/div"));
        icd1.click();
        
        
        WebElement cpt = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[4]/div[2]/div[1]/div/div/input"));
        cpt.sendKeys(data.get("cpt"));
        Thread.sleep(2000);
        
        WebElement cpt1 = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[4]/div[2]/div[1]/div[2]/div/div/div"));
        cpt1.click();
        
    }

    private void generateClaim(HashMap<String, String> data) throws InterruptedException {
        WebElement submitButton = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-footer/sl-button[3]"));
        submitButton.click();

        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        Thread.sleep(4000);

        WebElement claimRow = driver.findElement(By.xpath("//table//tbody//tr[1]"));
        claimRow.click();
        Thread.sleep(10000);

        WebElement amountInput = driver.findElement(By.xpath("//tbody//tr//td[8]//input"));
        amountInput.sendKeys(data.get("amount"));
        
        //
        String claimType = data.get("claimType").toLowerCase();  // Convert to lowercase for easier comparison

        if ("paper".equals(claimType)) {
            // Perform paper claim actions
            logger.info("Processing as Paper Claim...");
            WebElement paperClaimOption = driver.findElement(By.xpath("//sl-radio-group//div//sl-radio[2]"));
            paperClaimOption.click();
            WebElement modifiersDropdown = driver.findElement(By.xpath("//app-ed-dropdown//div[1]"));
            modifiersDropdown.click();
//            modifiersDropdown.sendKeys("OA");

            WebElement modifierOption = driver.findElement(By.xpath("(//*[@id='mod1']/descendant::button)[1]"));
            modifierOption.click();

        } else if ("electronic".equals(claimType)) {
            // Perform electronic claim actions
            logger.info("Processing as Electronic Claim...");
            WebElement electronicClaimOption = driver.findElement(By.xpath("//sl-radio-group//div//sl-radio[1]"));
            electronicClaimOption.click();
            WebElement modifiersDropdown = driver.findElement(By.xpath("//app-ed-dropdown//div[1]"));
            modifiersDropdown.click();
//            modifiersDropdown.sendKeys("OA");

            WebElement modifierOption = driver.findElement(By.xpath("(//*[@id='mod1']/descendant::button)[1]"));
            modifierOption.click();
        }
        else if ("Self".equals(claimType)){
        	 logger.info("Processing as Electronic Claim...");
        	 WebElement selfpay = driver.findElement(By.xpath(""));
        }
        else {
            logger.error("Invalid claim type provided: " + claimType);
            throw new IllegalArgumentException("Invalid claim type provided in the test data.");
        }

//        WebElement paperClaimOption = driver.findElement(By.xpath("//*[@id=\"tour-guide-billing-encounter-step1\"]/div[2]/div[1]/div[1]/sl-radio-group/div/sl-radio[2]"));
//        paperClaimOption.click();

        WebElement cptCodeIcon = driver.findElement(By.xpath("//*[@id=\"tour-guide-billing-encounter-step5\"]/sl-icon-button"));
        cptCodeIcon.click();

        WebElement cptCodeOption = driver.findElement(By.xpath("//ed-drawer-body//ul//li[25]"));
        cptCodeOption.click();
        WebElement closeDrawer = driver.findElement(By.xpath("//ed-drawer-header//sl-icon-button"));
        closeDrawer.click();
       
        WebElement generateClaimButton = driver.findElement(By.xpath("//*[@id=\"tour-guide-billing-encounter-step7\"]"));
        generateClaimButton.click();
        logger.info("Claim generated successfully.");
    }

    @DataProvider(name = "dataProviderTest")
    public Object[][] dataProvider() throws IOException {
//        HashMap<String, String> dataSet1 = new HashMap<>();
//        dataSet1.put("patientName", "Sourav Susari");
//        dataSet1.put("date", "8/16/2024");
//        dataSet1.put("icd", "cholera");
//        dataSet1.put("cpt", "99202");
//        dataSet1.put("amount", "200");
//        dataSet1.put("claimType", "paper");
//
//        HashMap<String, String> dataSet2 = new HashMap<>();
//        dataSet2.put("patientName", "Olly Pope");
//        dataSet2.put("date", "8/16/2024");
//        dataSet2.put("icd", "cholera");
//        dataSet2.put("cpt", "99202");
//        dataSet2.put("amount", "300");
//        dataSet2.put("claimType", "electronic");
    	List<HashMap<String, String>> data = dr.getJsonDataToMap();
    	System.out.println(data);
    	return new Object[][] {
    	    {data.get(0)},
    	    {data.get(1)},
    	    // Add more data sets if needed
    	};

    }

	

	
	private boolean isAlertPresent(WebDriver driver) {
		// TODO Auto-generated method stub
		return false;
	}
}
//"C:\\Users\\sksusari\\Documents\\Test\\Billing.json"
