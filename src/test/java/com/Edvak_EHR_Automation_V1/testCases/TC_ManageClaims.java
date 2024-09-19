package com.Edvak_EHR_Automation_V1.testCases;
 
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import com.Edvak_EHR_Automation_V1.pageObjects.BillingGenerateClaims;
import com.Edvak_EHR_Automation_V1.pageObjects.LoginPage;
import com.Edvak_EHR_Automation_V1.utilities.DataReader;
import com.Edvak_EHR_Automation_V1.utilities.DataReaderFilter;
 
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
 
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.SearchContext;
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
 
public class TC_ManageClaims extends BaseClass {
    TC_BillingGenerateClaims tcb = new TC_BillingGenerateClaims();
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
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//header//h2[normalize-space()='dashboard']")));
//        WebElement dashboardElement = driver.findElement(By.xpath("//header//h2[normalize-space()='dashboard']"));
////        bi.getTextBillingPageHeader();
//        Assert.assertTrue(dashboardElement.isDisplayed(), "Dashboard should be visible after login.");
        tcb.clickWithRetry(driver.findElement(By.xpath("//span[normalize-space()='attach_money']")), 3);
        logger.info("Billing button is clicked");
    }



	@Test(priority = 1, dataProvider = "dataProviderTest", dependsOnMethods = {"testQuickRegistration"})
   public void Filter(HashMap<String, Object> data) throws InterruptedException, IOException {
        // Initialize WebDriverWait to wait for elements to load
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
        WebDriverWait waitShort = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Extract values from the HashMap passed from the DataProvider
        @SuppressWarnings("unchecked")
		List<String> statuses = (List<String>) data.get("status");
        @SuppressWarnings("unchecked")
		List<String> patientNames = (List<String>) data.get("patientNames");
        @SuppressWarnings("unchecked")
		List<String> providerNames = (List<String>) data.get("providerNames");
        @SuppressWarnings("unchecked")
		HashMap<String, String> dateRange = (HashMap<String, String>) data.get("dateRange");

        // Optional: Print extracted values for debugging
        System.out.println("Statuses: " + statuses);
        System.out.println("Patient Names: " + patientNames);
        System.out.println("Date Range (From): " + dateRange.get("from"));

        // Simulate loading the Billing page (if necessary)
        Thread.sleep(3000);
        // Interact with the Manage Claim tab
        WebElement manageClaim = waitShort.until(ExpectedConditions.elementToBeClickable(By.xpath("//sl-tab-group//sl-tab[2]")));
        manageClaim.click();
        System.out.println("Manage claim");

        // Open the filter dropdown
        WebElement filter = driver.findElement(By.xpath("//app-filter-panel-head//sl-dropdown"));
        filter.click();
        Thread.sleep(5000);
        logger.info("Filter dropdown opened");

     

        // Apply the filter by clicking the 'Apply' button
        try {
            WebElement applyButton = waitShort.until(ExpectedConditions.elementToBeClickable(By.xpath("//sl-button[.='Clear Filter']")));
            applyButton.click();
            System.out.println("Filter applied.");
        } catch (NoSuchElementException e) {
            System.out.println("Apply button not found.");
        }
        logger.info("Filter cleared");

        // Define a WebDriverWait for subsequent elements

        // Apply the Status Filter
        if (statuses != null) {
            for (String status : statuses) {
                try {
                    WebElement statusElement = waitShort.until(ExpectedConditions.elementToBeClickable(By.xpath("//p[contains(text(), '" + status + "')]")));
                    statusElement.click();
                    System.out.println("Status clicked: " + status);
                } catch (NoSuchElementException e) {
                    System.out.println("Status not found: " + status);
                }
            }
        }

        // Apply the Patient Name Filter
        if (patientNames != null) {
            for (String patientName : patientNames) {
                try {
                    WebElement patientInput = waitShort.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='Search Patient Name']")));
                    patientInput.clear();
                    patientInput.sendKeys(patientName);

                    WebElement patientNameOption = waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(text(), '" + patientName + "')]")));
                    patientNameOption.click();
                    patientInput.sendKeys(Keys.ENTER);
                    Thread.sleep(100);
                } catch (NoSuchElementException e) {
                    System.out.println("Patient name not found: " + patientName);
                }
            }
        }
        Thread.sleep(2000);
        // Apply the Provider Name Filter
        if (providerNames != null) {
            for (String providerName : providerNames) {
                try {
                    WebElement providerInput = waitShort.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='Provider Name']")));
                    providerInput.clear();
                    providerInput.sendKeys(providerName);

                    WebElement providerOption = waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(text(), '" + providerName + "')]")));
                    providerOption.click();
                    providerInput.sendKeys(Keys.ENTER);
                } catch (NoSuchElementException e) {
                    System.out.println("Provider name not found: " + providerName);
                }
            }
        }
        Thread.sleep(2000);
        // Apply the Created By Filter
        if (data.containsKey("createdByNames")) {
            List<String> createdByNames = (List<String>) data.get("createdByNames");
            for (String createdBy : createdByNames) {
                try {
                    WebElement createdByInput = waitShort.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='Created By']")));
                    createdByInput.clear();
                    createdByInput.sendKeys(createdBy);

                    WebElement createdByOption = waitShort.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(text(), '" + createdBy + "')]")));
                    createdByOption.click();
                    createdByInput.sendKeys(Keys.ENTER);
                } catch (NoSuchElementException e) {
                    System.out.println("Created by name not found: " + createdBy);
                }
            }
        }

        // Apply the Date Range Filter
//        if (dateRange != null) {
//            try {
//                if (!dateRange.get("from").isEmpty()) {
//                    WebElement fromDateInput = waitShort.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='From Date']")));
//                    fromDateInput.clear();
//                    fromDateInput.sendKeys(dateRange.get("from"));
//                }
//
//                if (!dateRange.get("to").isEmpty()) {
//                    WebElement toDateInput = waitShort.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@placeholder='To Date']")));
//                    toDateInput.clear();
//                    toDateInput.sendKeys(dateRange.get("to"));
//                }
//            } catch (NoSuchElementException e) {
//                System.out.println("Date range input error.");
//            }
//        }
        Thread.sleep(2000);
        WebElement recordCountElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//p[contains(text(), 'record(s) match your current filter selection')]")));
            String recordCountText = recordCountElement.getText();
            int displayedCount = 0; 
            if (recordCountText.contains("No")) {
                displayedCount = 0;  
            } else {
                displayedCount = Integer.parseInt(recordCountText.split(" ")[0]);
            }

            try {
                WebElement applyButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//sl-button[text()='Apply']")));
                applyButton.click();
                System.out.println("Filter applied.");
            } catch (NoSuchElementException e) {
                System.out.println("Apply button not found.");
            }
            Thread.sleep(4000);
          WebElement firstRow = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='tour-guide-billing-claims-step2']//table//tbody//tr")));
          WebElement element = driver.findElement(By.xpath("//*[@id=\"tour-guide-billing-claims-step2\"]/descendant::table"));
          JavascriptExecutor js = (JavascriptExecutor) driver;
          js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight;", element);  // Scrolls inside the element
    }


}