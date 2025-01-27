package com.Edvak_EHR_Automation_V1.testCases;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.testng.annotations.Test;

import com.Edvak_EHR_Automation_V1.pageObjects.LoginPage;
import com.Edvak_EHR_Automation_V1.pageObjects.QuickRegisterPage;
import com.Edvak_EHR_Automation_V1.pageObjects.FacesheetAllergies;
import com.gargoylesoftware.htmlunit.javascript.host.Console;


public class TC_FacesheetAllergies extends BaseClass{
	
	@Test(priority=0)
	public void testQuickRegistration() throws InterruptedException {
		LoginPage lp= new LoginPage(driver);
		QuickRegisterPage qr = new QuickRegisterPage(driver);
		FacesheetAllergies fs = new FacesheetAllergies(driver);
	    logger.info("********Test Starts Here********");
        logger.info("'testQuickRegistrationtWithValidData' test execution starts here:");
	    logger.info("Opening URL: "+baseURL);
        driver.get(baseURL);
        logger.info("Opened URL: "+baseURL);
        driver.manage().window().maximize();
		logger.info("Entering username in Username Text field");
		lp.setUserName("marcia@ehr.com");
		logger.info("Entered Username in Username Text field");
		logger.info("Entering Password in password Text field");
		lp.setPassword("Edvak@321");
		logger.info("Entered Password in password Text field");
		logger.info("Clicking on Login button");
		WebElement element = driver
				.findElement(By.xpath("/html/body/app-root/div/div/app-login/section/div/div/form/div[3]/sl-button"))
				.getShadowRoot().findElement(By.cssSelector("button"));
		Actions actions = new Actions(driver);
		actions.moveToElement(element).click().build().perform();
		logger.info("Clicked on Login button");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/div[1]/app-header[1]/header[1]/div[1]/section[1]/div[2]/button[1]")));
		WebElement element1 = driver
				.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/aside[1]/main[1]/nav[1]/a[2]/span[1]/.."));
		int retryCount = 0;
        boolean clicked = false;
        while (retryCount < 3 && !clicked) { // Retry for a maximum of 3 times
            try {
                // Click on the element
                element1.click();
                // If click is successful, set clicked flag to true
                clicked = true;
            } catch (org.openqa.selenium.ElementClickInterceptedException e) {
                // Handle ElementClickInterceptedException
                System.out.println("Attempt " + (retryCount + 1) + ": Element click intercepted. Retrying...");
                retryCount++;
            }
        }
//		element1.click();		
		logger.info("Clicked on dashboard Patient Icon");		
		logger.info("Clicked on dashboard Patient Icon");
		Thread.sleep(1000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table/tbody/tr[2]")));
		WebElement element2 = driver
				.findElement(By.xpath("//table/tbody/tr[2]"));
		element2.click();
		logger.info("Patient Facesheet page open");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div/ed-col[2]/section[2]")));
//		WebElement element3 = driver
//				.findElement(By.xpath("//div/ed-col[2]/section[2]"));
		int maxAttempts = 3;
        int attempts = 0;
        while (attempts < maxAttempts) {
            try {
                // Find the element
                WebElement element31 = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-facesheetroutingcomponent/app-facesheet/main/div[1]/ed-col[2]/section[2]"));

                // Perform actions on the element
                element31.click();
                break;
            } catch (StaleElementReferenceException e) {
                attempts++;
                System.out.println("StaleElementReferenceException occurred. Retrying...");
            }
        }
        logger.info("clicked");
        Thread.sleep(2000);
        WebElement element21 = driver
		.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer/ed-drawer-header/div[1]/sl-icon-button\r\n"))
		.getShadowRoot().findElement(By.cssSelector("button"));
        element21.click();
        logger.info("Clicked on patient plus icon");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer[1]/ed-drawer-body/main/div[2]")));
        Thread.sleep(400);

        logger.info("-----");
  
        WebElement allergenInput = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer[1]/ed-drawer-body/main/div[1]/div/input"));

        String allergenName = "Miltown";
        allergenInput.sendKeys(allergenName);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer[1]/ed-drawer-body/main/div[2]")));

    
        Thread.sleep(400);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer[1]/ed-drawer-body/main/div[2]/div/section")));
        WebElement allergenOption = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer[1]/ed-drawer-body/main/div[2]/div/section"));
        allergenOption.click();

        WebElement Date = driver.findElement(By.xpath("//main/div[2]/div/div/input"));  
        Actions actions1 = new Actions(driver);

        // Perform the double-click action
        actions1.doubleClick(Date).perform();

        Date.sendKeys("04242024");
        logger.info("Date entered");

        WebElement dropdownElement = driver.findElement(By.xpath("//select[@id='severity']")); // Replace "dropdown_id" with the actual ID of the dropdown

        Select dropdown = new Select(dropdownElement);

        dropdown.selectByVisibleText("Fatal");
        
		WebElement save = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer[1]/ed-drawer-footer/sl-button[2]"));
		save.click();
		
		WebElement CheckOption= driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer[2]/ed-drawer-body/div/div[1]/div/div[1]/sl-checkbox"));
		CheckOption.click();
		WebElement InactiveButton= driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer[2]/div[2]/div/sl-button[1]"));
		InactiveButton.click();
        WebElement Inactive = driver.findElement(By.xpath("//nav/button[2]"));
		Inactive.click();
		WebElement CheckOption1= driver.findElement(By.xpath("//ed-drawer-body/div/div/div/div/sl-checkbox"));
		CheckOption1.click();
		WebElement MarkaserrorButton= driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer/div[2]/div/sl-button[2]"));
		MarkaserrorButton.click();
		
		
		WebElement MarkasError = driver.findElement(By.xpath("//nav/button[3]"));
		MarkasError.click();
		WebElement CheckOption2= driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer/ed-drawer-body/div/div/div/div[1]/sl-checkbox"));
		CheckOption2.click();
		WebElement Active= driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer/div[2]/div/sl-button[1]"));
		Active.click();
		
		WebElement print= driver.findElement(By.xpath("//span[@class='material-symbols-rounded sm ng-tns-c306-3']"));
		print.click();
       }
	}
	


