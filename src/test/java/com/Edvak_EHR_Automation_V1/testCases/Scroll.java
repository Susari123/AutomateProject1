package com.Edvak_EHR_Automation_V1.testCases;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.Edvak_EHR_Automation_V1.pageObjects.BillingGenerateClaims;
import com.Edvak_EHR_Automation_V1.pageObjects.LoginPage;
import com.Edvak_EHR_Automation_V1.utilities.DataReader;

public class Scroll extends BaseClass  {
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
        lp.setPassword("Admin@123456");
        logger.info("Entered Password in password Text field");

        logger.info("Clicking on Login button");
        WebElement loginButton = driver
                .findElement(By.xpath("/html/body/app-root/div/div/app-login/section/div/div/form/div[3]/sl-button"))
                .getShadowRoot().findElement(By.cssSelector("button"));
        new Actions(driver).moveToElement(loginButton).click().build().perform();
        logger.info("Clicked on Login button");
        Thread.sleep(1000);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
        
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//nav/a[5]/span[1]/sl-icon")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//app-header/header/h4")));
        WebElement dashboardElement = driver.findElement(By.xpath("//app-header/header/h4"));
//        bi.getTextBillingPageHeader();
        Assert.assertTrue(dashboardElement.isDisplayed(), "Dashboard should be visible after login.");
        clickWithRetry(driver.findElement(By.xpath("//nav/a[5]/span[1]/sl-icon")), 3);
        logger.info("Billing button is clicked");
        
        
    }
	 @Test(priority = 1)
	 public void scrollInPage() {
		 WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
	        // Assertion to verify that Billing page is loaded
	       WebElement billingPageHeader = driver.findElement(By.xpath("//h4[normalize-space()='billing']"));
//	       Assert.assertTrue(billingPageHeader.isDisplayed(), "Billing page should be displayed.");
	       wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form//div//sl-button[@id='tour-guide-billing-Step4']")));
	       wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr//td")));
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
}
