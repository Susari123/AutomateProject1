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

public class TC_BillingGenerateClaims  extends BaseClass {
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
		lp.setPassword("Admin@321");
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
				.findElement(By.xpath("//span[normalize-space()='attach_money']"));
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
        logger.info("Biiling Button is clicked");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"sl-tab-panel-1\"]/app-coding-list/ed-col/section/div")));
        WebElement NewCharge= driver.findElement(By.xpath("//*[@id=\"sl-tab-panel-1\"]/app-coding-list/ed-col/section/form/div/sl-button"));
        NewCharge.click();
        
        WebElement Patientname = driver.findElement(By.xpath("//input[@class='w-full form-input']"));

        String patientName = "Billingtest Prod";
        Patientname.sendKeys(patientName);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[1]/div[1]/div[1]/type-ahead[1]/div[1]/div[1]/div[1]")));

    
        Thread.sleep(400);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[1]/div[1]/div[1]/type-ahead[1]/div[1]/div[1]/div[1]")));
        WebElement patientName1 = driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[1]/div[1]/div[1]/type-ahead[1]/div[1]/div[1]/div[1]"));
        patientName1.click();
        
        WebElement Encounter = driver.findElement(By.xpath("//div[@class='border border-[#CBD5E1] flex form-select w-full bg-white']"));
        Encounter.click();
        
        WebElement newEncounter = driver.findElement(By.xpath("//div/sl-button-group/sl-button[2]"));
        newEncounter.click();
        Thread.sleep(2000);
        
    	WebElement genderDropdown = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[1]/div[2]/app-encounter-selection/sl-dropdown/main/div[2]/div/div[1]/ng-select"));
    	genderDropdown.click();
    	Thread.sleep(100);
    	WebElement selectoption = driver.findElement(By.xpath("//span[@class='ng-option-label ng-star-inserted'][normalize-space()='Marcia Galbraith']"));
    	selectoption.click();
    	
    	WebElement genderDropdown1 = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[1]/div[2]/app-encounter-selection/sl-dropdown/main/div[2]/div/div[2]/div[1]/ng-select"));
    	genderDropdown1.click();
    	Thread.sleep(100);
    	WebElement selectoption1 = driver.findElement(By.xpath("//span[@class='ng-option-label ng-star-inserted'][normalize-space()='Houston Location']"));
    	selectoption1.click();
    	
    	WebElement genderDropdown2 = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[1]/div[2]/app-encounter-selection/sl-dropdown/main/div[2]/div/div[2]/div[2]/ng-select"));
    	genderDropdown2.click();
    	Thread.sleep(100);
    	WebElement selectoption2 = driver.findElement(By.xpath("//span[normalize-space()='Care Coordination']"));
    	selectoption2.click();
    	
    	WebElement createEncounter = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[1]/div[2]/app-encounter-selection/sl-dropdown/main/div[2]/div/div[3]/sl-button"));
    	createEncounter.click();
    	
    	WebElement genderDropdown3 = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[3]/div[1]/div[2]/ng-select"));
    	genderDropdown3.click();
    	Thread.sleep(100);
    	WebElement selectoption3 = driver.findElement(By.xpath("//span[@class='ng-option-label ng-star-inserted'][normalize-space()='Marcia Galbraith (Neurological Surgery)']"));
    	selectoption3.click();
    	
    	WebElement genderDropdown4 = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[3]/div[1]/div[3]/ng-select"));
    	genderDropdown4.click();
    	Thread.sleep(100);
    	WebElement selectoption4 = driver.findElement(By.xpath("//span[@class='ng-option-label ng-star-inserted'][normalize-space()='Marcia Galbraith (Neurological Surgery)']"));
    	selectoption4.click();
    	
    	WebElement genderDropdown5 = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[3]/div[1]/div[4]/ng-select"));
    	genderDropdown5.click();
    	Thread.sleep(100);
    	WebElement selectoption5 = driver.findElement(By.xpath("//span[@class='ng-option-label ng-star-inserted'][normalize-space()='Marcia Galbraith (Neurological Surgery)']"));
    	selectoption5.click();
    	
    	WebElement cpt = driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[4]/div[1]/div[1]/div[1]/input[1]"));
    	cpt.sendKeys("");
    	
    	
}
}
