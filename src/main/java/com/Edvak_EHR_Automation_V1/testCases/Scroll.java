package com.Edvak_EHR_Automation_V1.testCases;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.Edvak_EHR_Automation_V1.pageObjects.LoginPage;


public class Scroll extends BaseClass {
    

//    @BeforeMethod
//    public void setUp() {
//        // Open the application URL before each test
//        driver.get(baseURL);
//        driver.manage().window().maximize();
//    }

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
        clickWithRetry(driver.findElement(By.xpath("//nav/a[5]")), 3);
        logger.info("Billing button is clicked");
        Thread.sleep(2000);
    }

    @Test(priority = 1)
    public void scrollInPage() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
//        WebElement billingPageHeader = driver.findElement(By.xpath("//h4[normalize-space()='billing']"));
//        wait.until(ExpectedConditions.visibilityOf(billingPageHeader));
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//form//div//sl-button[@id='tour-guide-billing-Step4']")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr//td")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//img[contains(@src, 'loader.svg')]")));
        Thread.sleep(3000);
        WebElement parentElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[1]/div/span/sl-tooltip")));

        // Hover over the parent element to make the copy button visible
        Actions actions = new Actions(driver);
        actions.moveToElement(parentElement).perform(); // This will make the button visible

        // Wait until the copy button is visible
        WebElement copyButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[1]/div/span/sl-tooltip/button")));

        // Click the copy button
        copyButton.click();
        System.out.println("Copy button clicked successfully.");

//         Scroll to the bottom of the page using Page Down keys
//        scrollToBottomUsingPageDown();
    }
    @Test(priority = 2)
    public void scrollInContainerUsingPageDown() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        // Wait for the scrollable container to be visible
        WebElement scrollableContainer = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".table-container")));

        // Focus on the container without clicking
        Actions actions = new Actions(driver);
        actions.moveToElement(scrollableContainer).perform(); // Focuses on the element

        // Scroll down using PAGE_DOWN key without clicking
        for (int i = 0; i < 5; i++) { // Adjust the number based on the height of the content
            actions.sendKeys(Keys.PAGE_DOWN).perform();
            // try {
            //     Thread.sleep(500); // Wait for content to load if necessary
            // } catch (InterruptedException e) {
            //     e.printStackTrace();
            // }
        }
        logger.info("Scrolled to the bottom of the table container using Page Down keys without clicking.");
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
