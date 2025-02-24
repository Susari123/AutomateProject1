package com.Edvak_EHR_Automation_V1.testCases;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

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
import com.Edvak_EHR_Automation_V1.utilities.LoginUtils;


public class TC_Refund extends BaseClass{
	
   @Test(priority = 0)
public void testQuickRegistration() throws InterruptedException {
    logger.info("********Test Starts Here********");
    logger.info("'testQuickRegistrationWithValidData' test execution starts here:");

    // Use LoginUtils for login instead of repeating login steps
    LoginUtils.loginToApplication(driver, baseURL, "souravsusari311@gmail.com", "Edvak@3210");

    BillingGenerateClaims billingPage = new BillingGenerateClaims(driver);

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
    wait.until(ExpectedConditions.visibilityOf(billingPage.getBillingIconElement()));
    wait.until(ExpectedConditions.visibilityOf(billingPage.getDashboardElement()));

    Assert.assertTrue(billingPage.isDashboardDisplayed(), "Dashboard should be visible after login.");

    clickWithRetry(billingPage.getBillingIconElement(), 3);
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
         wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[contains(@class, 'skeleton')]")));
         Thread.sleep(4000);
         try {
        	    // Check if 'No payments found' message is present
                    @SuppressWarnings("SizeReplaceableByIsEmpty")
        	    boolean noPaymentsFoundPresent = driver.findElements(By.xpath("//div[contains(text(), ' No payments found ')]")).size() > 0;

        	    if (!noPaymentsFoundPresent) {
        	        WebElement hoverArea = driver.findElement(By.xpath("//app-payment-list-table/div/table/tbody/tr[1]/td[5]/div"));
        	        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", hoverArea);
        	        System.out.println("Scrolled hover area into view.");
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
         logger.info("Refund opened");
         Thread.sleep(4000);
         Map<String, String> xpathsofElement = new HashMap<>();
         xpathsofElement.put("Cross Button", "//main/ed-drawer/ed-drawer-header/sl-icon-button");
         xpathsofElement.put("Refund Details", "//app-refund/main/ed-drawer/ed-drawer-header/h6");
         xpathsofElement.put("Cancel button ", "//sl-button[contains(text(), 'Cancel')]");
         xpathsofElement.put("Refund amount Button", "//sl-button[contains(text(), 'Refund amount')]");

         // Iterate over xpathsofElement and validate each element
         for (Map.Entry<String, String> entry : xpathsofElement.entrySet()) {
             String buttonName = entry.getKey();
             String xpath = entry.getValue();
             try {
                 WebElement button = driver.findElement(By.xpath(xpath));
                 Assert.assertTrue(button.isDisplayed(), buttonName + " is not displayed.");
                 System.out.println(buttonName + " is present.");
             } catch (NoSuchElementException e) {
                 Assert.fail(buttonName + " is not present.");
             }
         }
         LocalDate currentDate = LocalDate.now();
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
         String formattedDate = currentDate.format(formatter);

        
         WebElement issuedDate = driver.findElement(By.xpath("//ed-drawer-body/div/form/ed-col/div[2]/div/input"));
         issuedDate.sendKeys(formattedDate);
         WebElement status = driver.findElement(By.xpath("//ed-drawer-body/div/form/ed-col/div[3]/div"));
         status.click();
         Thread.sleep(200);
            @SuppressWarnings("unused")
         WebElement option = driver.findElement(By.xpath("//div/span[contains(text(), 'Processing')]"));
//         option.click();
         WebElement click1 = driver.findElement(By.xpath("//div/form/ed-col/div[3]/div/ng-select/div/span[2]"));
         click1.click();
         WebElement RefundPaymentMethod = driver.findElement(By.xpath("//div/form/ed-col/div[5]/ng-select"));
         RefundPaymentMethod.click();
         WebElement reason = driver.findElement(By.xpath("//div/form/ed-col/div[6]/div/textarea"));
         String randomString = generateRandomString(10); // Length of 10 characters
         reason.sendKeys(randomString);
         Thread.sleep(200);
         WebElement option1 = driver.findElement(By.xpath("//div/span[contains(text(), 'Cash')]"));
         option1.click();
         WebElement RefundAmount = driver.findElement(By.xpath("//sl-button[contains(text(), 'Refund amount')]"));
         RefundAmount.click();
        
//    	 logger.info("Finished");
    	 

    }
    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            randomString.append(characters.charAt(index));
        }
        return randomString.toString();
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