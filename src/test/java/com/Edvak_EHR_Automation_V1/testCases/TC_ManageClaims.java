package com.Edvak_EHR_Automation_V1.testCases;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
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
import java.util.stream.Collectors;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.Keys;
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
 // Method to read and parse the filter data from the HashMap
    private JSONObject readFilterData(HashMap<String, String> data) {
        // Retrieve the filter_cases string from the HashMap
        String filterCasesString = data.get("filter_cases");
        
        // Log the filter cases string for debugging
        System.out.println("Filter cases: " + filterCasesString);

        // Parse the string into a JSONObject and return it
        JSONObject jsonObject = new JSONObject(filterCasesString);
        return jsonObject;
    }
    @Test(priority = 1, dataProvider = "dataProviderTest", dependsOnMethods = {"testQuickRegistration"})
    void statementReady(HashMap<String, String> data) throws InterruptedException, IOException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
        JSONObject filterData = readFilterData(data);
        Thread.sleep(3000);
        // Ensure that the Billing page is loaded
        WebElement billingPageHeader = driver.findElement(By.xpath("//h2[normalize-space()='billing']"));
        Assert.assertTrue(billingPageHeader.isDisplayed(), "Billing page should be displayed.");
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form//div//sl-button[@id='tour-guide-billing-Step4']")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr//td")));
        
        WebElement manageClaim = driver.findElement(By.xpath("//sl-tab-group//sl-tab[2]"));
        manageClaim.click();
        System.out.println("Manage claim");
        // Retrieve filter_cases as a JSON string
        
        
        WebElement filter = driver.findElement(By.xpath("//app-filter-panel-head//sl-dropdown"));
        if (filter != null) {
            System.out.println("Filter element found and clicking.");
            filter.click();
        } else {
            System.out.println("Filter element not found!");
        }
        // Access and apply the Status Filter
        if (filterData.has("status")) {
            JSONArray statuses = filterData.getJSONArray("status");
            for (int j = 0; j < statuses.length(); j++) {
                String status = statuses.getString(j);
                WebElement statusElement = driver.findElement(By.xpath("//p[contains(text(), '" + status + "')]"));
                statusElement.click();
                System.out.println("Status clicked: " + status);
            }
        }

        // Access and apply the Patient Name Filter
        if (filterData.has("patientNames")) {
            JSONArray patientNames = filterData.getJSONArray("patientNames");
            for (int j = 0; j < patientNames.length(); j++) {
                String patientName = patientNames.getString(j);
                WebElement patientInput = driver.findElement(By.xpath("//input[@placeholder='Search Patient Name']"));
                patientInput.sendKeys(patientName);
                Thread.sleep(2000);
                WebElement patientNameOption = driver.findElement(By.xpath("//p[contains(text(), '" + patientName + "')]"));
                patientNameOption.click();
                patientInput.sendKeys(Keys.ENTER);
            }
        }

        // Access and apply the Provider Name Filter
        if (filterData.has("providerNames")) {
            JSONArray providerNames = filterData.getJSONArray("providerNames");
            for (int j = 0; j < providerNames.length(); j++) {
                String providerName = providerNames.getString(j);
                WebElement providerInput = driver.findElement(By.xpath("//input[@id='Provider Name']"));
                providerInput.sendKeys(providerName);
                Thread.sleep(2000);
                WebElement providerOption = driver.findElement(By.xpath("//p[contains(text(), '" + providerName + "')]"));
                providerOption.click();
                providerInput.sendKeys(Keys.ENTER);
            }
        }

        // Access and apply the Created By Filter
        if (filterData.has("createdByNames")) {
            JSONArray createdByNames = filterData.getJSONArray("createdByNames");
            for (int j = 0; j < createdByNames.length(); j++) {
                String createdBy = createdByNames.getString(j);
                WebElement createdByInput = driver.findElement(By.xpath("//input[@id='Created By']"));
                createdByInput.sendKeys(createdBy);
                Thread.sleep(2000);
                WebElement createdByOption = driver.findElement(By.xpath("//p[contains(text(), '" + createdBy + "')]"));
                createdByOption.click();
                createdByInput.sendKeys(Keys.ENTER);
            }
        }

        // Access and apply the Date Range Filter
        if (filterData.has("dateRange")) {
            JSONObject dateRange = filterData.getJSONObject("dateRange");
            String fromDate = dateRange.getString("from");
            String toDate = dateRange.getString("to");

            if (!fromDate.isEmpty()) {
                WebElement fromDateInput = driver.findElement(By.xpath("//input[@placeholder='From Date']"));
                fromDateInput.sendKeys(fromDate);
            }

            if (!toDate.isEmpty()) {
                WebElement toDateInput = driver.findElement(By.xpath("//input[@placeholder='To Date']"));
                toDateInput.sendKeys(toDate);
            }
        }

        // Apply the filter by clicking the 'Apply' button
        WebElement applyButton = driver.findElement(By.xpath("//sl-button[@id='applyFilterButton']"));
        applyButton.click();

        driver.quit();
    }

        
//
        @DataProvider(name = "dataProviderTest")
        public Object[][] dataProvider() throws IOException {
        // Specify the path to the JSON file
        DataReaderFilter df = new  DataReaderFilter();
        String filePath = "C:\\Users\\sksusari\\Documents\\Test\\filter_cases.json";
        
        // Get the JSON data as a List of HashMaps
        HashMap<String, String> data = df.getJsonDataToMapFilter();
        Object[][] dataArray = new Object[data.size()][1];
        
        for (int i = 0; i < data.size(); i++) {
            dataArray[i][0] = data.get(i);  // Each test will receive a HashMap as input
        }

        return dataArray;
        }
    
    }