package com.Edvak_EHR_Automation_V1.testCases;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Edvak_EHR_Automation_V1.pageObjects.LoginPage;
import com.Edvak_EHR_Automation_V1.utilities.DataReader;
import com.Edvak_EHR_Automation_V1.utilities.DataReaderFilter;

public class TC_allTest extends BaseClass {

    DataReader dr = new DataReader();

    @Test(priority = 0)
    public void testQuickRegistration() throws InterruptedException {
        LoginPage lp = new LoginPage(driver);
        logger.info("********Test Starts Here********");
        logger.info("'testQuickRegistrationWithValidData' test execution starts here:");
        logger.info("Opening URL: " + baseURL);
        
        driver.get(baseURL);
        driver.manage().window().maximize();

        lp.setUserName("souravsusari311@gmail.com");
        lp.setPassword("Admin@123456");

        WebElement loginButton = driver
                .findElement(By.xpath("/html/body/app-root/div/div/app-login/section/div/div/form/div[3]/sl-button"))
                .getShadowRoot().findElement(By.cssSelector("button"));
        new Actions(driver).moveToElement(loginButton).click().build().perform();

        Thread.sleep(1000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//nav/a[5]/span[1]/sl-icon")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//app-header/header/h4")));

        WebElement dashboardElement = driver.findElement(By.xpath("//app-header/header/h4"));
        Assert.assertTrue(dashboardElement.isDisplayed(), "Dashboard should be visible after login.");
        
        clickWithRetry(driver.findElement(By.xpath("//nav/a[5]")), 5);
        logger.info("Billing button is clicked");
    }
    
    @Test(priority = 1, dataProvider = "billingDataProvider", dependsOnMethods = {"testQuickRegistration"})
    public void testBillingGenerateClaims(List<HashMap<String, String>> dataList) throws InterruptedException, IOException {
        for (HashMap<String, String> data : dataList) {
            System.out.println("Processing claim for patient: " + data.get("patientName"));
            TC_BillingGenerateClaims billingGenerateClaims = new TC_BillingGenerateClaims();
            billingGenerateClaims.testBillingGenerateClaims(data);
            
        }
    }
    @Test(priority = 2, dataProvider = "manageClaimsDataProvider")
//    public void runStatementReadyTests(HashMap<String, Object> data) throws InterruptedException, IOException, TimeoutException {
//    	TC_StatementReady statementReady = new TC_StatementReady(driver);
//        TC_BillingGenerateClaims billingGenerateClaims = new TC_BillingGenerateClaims();
//        billingGenerateClaims.verifyEncountersInManageClaims();
//        statementReady.ManageClaims(data);
//           
//    }

    @DataProvider(name = "billingDataProvider")
    public Object[][] billingDataProvider() throws IOException {
        List<HashMap<String, String>> data = dr.getJsonDataToMap(); // Assuming this retrieves JSON data as a List of HashMaps
        return new Object[][] {{ data }}; // Pass the entire list as a single parameter
    }

    @DataProvider(name = "manageClaimsDataProvider")
    public Object[][] manageClaimsDataProvider() throws IOException {
        DataReaderFilter dataReader = new DataReaderFilter();
        HashMap<String, Object> jsonData = dataReader.getJsonDataToMapFilter();
        return new Object[][] {
            { jsonData }
        };
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
        Assert.assertTrue(clicked, "Element should be clicked within retry limit.");
    }
}
