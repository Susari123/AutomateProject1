package com.Edvak_EHR_Automation_V1.testCases;

import java.io.File;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
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
import com.Edvak_EHR_Automation_V1.utilities.DateUtils;
import com.Edvak_EHR_Automation_V1.utilities.EncounterDataProvider;


public class TC_Payment extends BaseClass{
	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));

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
    @Test(priority=1,dataProvider = "combinedDataProvider", dependsOnMethods = {"testQuickRegistration"})
	public void payment(String encounterNumber, String status, String currentDate, String futureDate) throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
        // Assertion to verify that Billing page is loaded
    //    WebElement billingPageHeader = driver.findElement(By.xpath("//h2[normalize-space()='billing']"));
    //    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form//div//sl-button[@id='tour-guide-billing-Step4']")));
       wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr//td")));
       WebElement paymentTab = driver.findElement(By.xpath("//sl-tab-group//sl-tab[3]"));
       paymentTab.click();
//       wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(" //*[@id=\"sl-tab-panel-9\"]/app-payments-list/ed-col/section/div[2]/table/tbody/tr[1]")));
      // Access the shadow host element (in this case the sl-icon-button)
       WebElement plus = driver.findElement(By.xpath("//sl-icon-button[@name='add']"));
       plus.click();
       wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ed-drawer-header//h2[contains(text(), 'New Payment')]")));
       WebElement date = driver.findElement(By.xpath("//input[@formcontrolname='submission_Date']"));
       date.sendKeys(currentDate);
    //    currentDate
        WebElement paymentType = driver.findElement(By.xpath("//ed-drawer/ed-drawer-body/div[2]/div/ng-select/div/div/div[3]"));
        paymentType.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ng-dropdown-panel//div[2]//div")));
        System.out.println("Status: " + status);
        if (status.equalsIgnoreCase("Statement Ready")) {
            // If the status is "Statement Ready", select the Patient Payment option
            WebElement patientPaymentOption = driver.findElement(By.xpath("//ng-dropdown-panel//span[contains(text(), ' Patient ')]"));
            if (patientPaymentOption.isEnabled()) {
                patientPaymentOption.click();
                System.out.println("Patient Payment option selected.");
            } else {
                System.out.println("Patient Payment option is not available.");
            }
            Thread.sleep(200);
            WebElement PatientName = driver.findElement(By.xpath("//input[@placeholder ='Search Patient']"));
            PatientName.sendKeys("Radiousone Smith");          
            WebElement optionpatient = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//ed-drawer/ed-drawer-body/div[3]/div/type-ahead/div/div/div/section")));
            optionpatient.click();
            
        } else {
            // For other statuses, select the Insurance option
            WebElement insuranceOption = driver.findElement(By.xpath("//ng-dropdown-panel//span[contains(text(), 'Insurance')]"));
            if (insuranceOption.isEnabled()) {
                insuranceOption.click();
                System.out.println("Insurance option selected.");
            } else {
                System.out.println("Insurance option is not available.");
            }
            Thread.sleep(200);
            WebElement insurancePlanName = driver.findElement(By.xpath("//input[@placeholder ='Search insurance plan']"));
            insurancePlanName.sendKeys("CareCore National, Inc.");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//app-dropdown-templates-loader[1]/app-dropdown-template-insurance/div/section/span")));
            WebElement option = driver.findElement(By.xpath("//app-dropdown-templates-loader[1]/app-dropdown-template-insurance/div/section/span"));
            option.click(); 
        }
        WebElement modeOfPayment = driver.findElement(By.xpath("//ed-drawer/ed-drawer-body/div[4]/div/ng-select/div/span"));
        modeOfPayment.click();     
        WebElement cash = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//ng-dropdown-panel//span[contains(text(), ' Cash ')]")));
        cash.click();
        WebElement amount = driver.findElement(By.xpath("//app-new-payments/main/ed-drawer/ed-drawer-body/div[5]/div/input"));
        String randomAmount = TC_Payment.getRandomAmount();
        amount.sendKeys(randomAmount);
        WebElement notes = driver.findElement(By.xpath("//app-new-payments/main/ed-drawer/ed-drawer-body/div[6]/div/textarea"));
        notes.sendKeys("NoteADDED");
        WebElement addPayment = driver.findElement(By.xpath("//ed-drawer/ed-drawer-footer/sl-button[1]"));
        addPayment.click();
        Thread.sleep(4000);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr")));
        WebElement payment = driver.findElement(By.xpath("//table//tbody//tr"));
    	payment.click();
    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//app-patient-payments/div/header/h3")));
        WebElement element = driver.findElement(By.xpath("//app-patient-payments/div/div[1]/div[1]/div[2]/div[1]/strong"));
        String value = element.getText().trim();
        if (value.equalsIgnoreCase("Insurance")) {
        	TC_Payment.insurancePayment(encounterNumber);     
        } else {
        	TC_Payment.patientPayment(encounterNumber);
        }
////        deleteJsonFile("encounters_with_status.json");
    }
    public static void insurancePayment(String encounterNumber) throws InterruptedException {
    	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
    	try {
            // Try to find the first button
            WebElement firstButton = driver.findElement(By.xpath("//html/body/app-root/div/div[2]/app-patient-payments/div/div[2]/div[1]/div/table/tbody/tr/td/div/sl-tooltip/sl-button"));
            
            // If the first button is found, click it
            if (firstButton != null && firstButton.isDisplayed()) {
                firstButton.click();
                System.out.println("First button clicked successfully.");
            }

        } catch (NoSuchElementException e) {
            // If the first button is not found, click the second button/html/body/app-root/div/div[2]/app-patient-payments/div/div[2]/div[1]/div/table/tbody/tr/td/div/sl-tooltip/sl-button
            try {
                WebElement secondButton = driver.findElement(By.xpath("//app-patient-payments/div/div[2]/div[1]/div[2]/sl-tooltip/sl-button"));
                secondButton.click();
                System.out.println("Second button clicked successfully.");
            } catch (NoSuchElementException secondException) {
                System.out.println("Both buttons are not found.");
            }
        }
    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='searchClaim']")));
        WebElement searchClaim = driver.findElement(By.xpath("//input[@formcontrolname='searchClaim']"));
        System.out.println(searchClaim);
       
        searchClaim.sendKeys(encounterNumber);
    	wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ed-drawer/ed-drawer-body/div/div[2]/div/table/tbody/tr")));
    	WebElement claim = driver.findElement(By.xpath("//ed-drawer-body/div/div[2]/div/table/tbody/tr/td[1]/div/sl-checkbox"));
    	claim.click();

    }
    public static void patientPayment(String encounterNumber) throws InterruptedException {
    	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
    	try {
            // Try to find the first button
            WebElement firstButton = driver.findElement(By.xpath("//html/body/app-root/div/div[2]/app-patient-payments/div/div[2]/div[1]/div/table/tbody/tr/td/div/sl-tooltip/sl-button"));
            
            // If the first button is found, click it
            if (firstButton != null && firstButton.isDisplayed()) {
                firstButton.click();
                System.out.println("First button clicked successfully.");
            }

        } catch (NoSuchElementException e) {
            // If the first button is not found, click the second button/html/body/app-root/div/div[2]/app-patient-payments/div/div[2]/div[1]/div/table/tbody/tr/td/div/sl-tooltip/sl-button
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
    // DataProvider method for TestNG
   @DataProvider(name = "encounterDataProvider")
    public Object[][] encounterDataProvider() {
        // Use the EncounterDataProvider to read encounter data
        EncounterDataProvider reader = new EncounterDataProvider();
        List<EncounterDataProvider.EncounterData> encounterDataList = reader.readEncounterDataFromJson();

        // Prepare the data in a format that TestNG's DataProvider can return
        Object[][] data = new Object[encounterDataList.size()][2]; // 2 for encounter number and status

        for (int i = 0; i < encounterDataList.size(); i++) {
            data[i][0] = encounterDataList.get(i).getEncounterNumber(); // Encounter number
            data[i][1] = encounterDataList.get(i).getStatus(); // Status
        }

        return data;
    }
    @DataProvider(name = "dateDataProvider")
    public Object[][] dateDataProvider() {
        // Get the current and future dates using DateUtils
        String[] dates = DateUtils.getCurrentAndFutureDate(10); // Get current and future date, 10 days ahead

        // Return the dates as an Object[][] array to be used by the TestNG test method
        return new Object[][] {
            { dates[0], dates[1] }  // Current date and future date
        };
    }
    @DataProvider(name = "combinedDataProvider")
    public Object[][] combinedDataProvider() {
        // Fetch data from both encounterDataProvider and dateDataProvider
        Object[][] encounterData = encounterDataProvider();
        Object[][] dateData = dateDataProvider();

        // Combine both sets of data dynamically
        Object[][] combinedData = new Object[encounterData.length][4]; // 4 columns: encounter number, status, current date, future date

        for (int i = 0; i < encounterData.length; i++) {
            combinedData[i][0] = encounterData[i][0]; // Encounter number
            combinedData[i][1] = encounterData[i][1]; // Status
            combinedData[i][2] = dateData[0][0]; // Current date
            combinedData[i][3] = dateData[0][1]; // Future date
        }

        return combinedData;
    }
    public static String getRandomAmount() {
        Random rand = new Random();

        // Generate a random integer between 100 and 500
        double randomAmount = 100 + (500 - 100) * rand.nextDouble();

        // Randomly decide if the number should have decimal places or not
        boolean addDecimals = rand.nextBoolean();  // Randomly returns true or false

        if (addDecimals) {
            // Format the number to 2 decimal places
            DecimalFormat df = new DecimalFormat("#.00");
            return df.format(randomAmount);
        } else {
            // Return the number as an integer (without decimals)
            return String.valueOf((int) randomAmount);
        }
    }
    private void deleteJsonFile(String filePath) {
        File file = new File(filePath);

        // Check if the file exists and delete it
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



