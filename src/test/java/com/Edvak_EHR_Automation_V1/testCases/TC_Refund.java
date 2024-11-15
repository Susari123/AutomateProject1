package com.Edvak_EHR_Automation_V1.testCases;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.Edvak_EHR_Automation_V1.pageObjects.BillingGenerateClaims;
import com.Edvak_EHR_Automation_V1.pageObjects.LoginPage;
import com.Edvak_EHR_Automation_V1.utilities.DataReader;

public class TC_Refund extends BaseClass{
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
    @Test(priority = 1, dependsOnMethods = {"testQuickRegistration"})
    public void Refund()throws InterruptedException {
    	WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
    	 wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr//td")));
    	 WebElement RefundTab = driver.findElement(By.xpath("//sl-tab-group//sl-tab[5]"));
    	 RefundTab.click();
    	 wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//img[contains(@src, 'loader.svg')]")));
    	 try {
             
             WebElement element = driver.findElement(By.xpath("//app-refunds-list/ed-col/section/form/div[1]/h4"));
             if (element.isDisplayed() && !element.getText().isEmpty()) {
                 System.out.println("Text is present: " + element.getText());
             } else {
                 System.out.println("Text is not present or the element is empty.");
             }
         } catch (Exception e) {
             System.out.println("An error occurred: " + e.getMessage());
         }
    	 WebElement plusButton = driver.findElement(By.xpath("//app-refunds-list/ed-col/section/form/div[1]/sl-icon-button"));
    	 plusButton.click();
    	 logger.info("Plus button is clicked ");
    	 wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//ed-drawer/ed-drawer-body/div/div/div/sl-button")));
    	 WebElement paragraph = driver.findElement(By.xpath("//app-right-side-bar/ed-modal/app-refund/main/ed-drawer/ed-drawer-body/div/div/p"));
         WebElement selectPaymentButton = driver.findElement(By.xpath("//sl-button[contains(text(), 'Select payment')]"));
         if (paragraph.isDisplayed() && selectPaymentButton.isDisplayed()) {
             System.out.println("Paragraph and button are visible.");
             selectPaymentButton.click();
             System.out.println("Clicked the 'Select payment' button.");
         } else {
             System.out.println("One or both elements are not visible.");
         }
    	 wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//img[contains(@src, 'loader.svg')]")));
    	
    	 Map<String, String> buttonXpaths = new HashMap<>();
         buttonXpaths.put("Select Patient Payment", "//app-refund/main/div/app-select-payment/ed-drawer/ed-drawer-header/h6");
         buttonXpaths.put("Patient", "//app-select-payment/ed-drawer/ed-drawer-body/div[1]/div/sl-button[1]");
         buttonXpaths.put("Insurance", "//app-select-payment/ed-drawer/ed-drawer-body/div[1]/div/sl-button[2]");
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
         wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//img[contains(@src, 'loader.svg')]")));
         Thread.sleep(2000);
         try {
        	    // Check if 'No payments found' message is present
        	    boolean noPaymentsFoundPresent = driver.findElements(By.xpath("//div[contains(text(), ' No payments found ')]")).size() > 0;

        	    if (!noPaymentsFoundPresent) {
        	        // Locate the hover area
        	        WebElement hoverArea = driver.findElement(By.xpath("//app-payment-list-table/div/table/tbody/tr[1]/td[5]/div"));

        	        // Scroll the hover area into view
        	        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", hoverArea);
        	        System.out.println("Scrolled hover area into view.");

        	        // Perform hover using Actions class
        	        Actions actions = new Actions(driver);
        	        actions.moveToElement(hoverArea).perform();
        	        System.out.println("Hovered over the hover area.");

        	        // Wait for the button to become visible and clickable
        	        WebDriverWait wait1 = new WebDriverWait(driver, Duration.ofSeconds(10));
        	        WebElement targetButton = wait1.until(ExpectedConditions.elementToBeClickable(
        	                By.xpath("//app-payment-list-table/div/table/tbody/tr[1]/td[5]/div/sl-button")));

        	        // Click the button
        	        targetButton.click();
        	        System.out.println("Clicked the target button.");
        	    } else {
        	        System.out.println("'No payments found' message is displayed. Skipping button click.");
        	    }
        	} catch (NoSuchElementException e) {
        	    System.out.println("Element not found: " + e.getMessage());
        	} catch (Exception e) {
        	    System.out.println("An error occurred: " + e.getMessage());
        	}   
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