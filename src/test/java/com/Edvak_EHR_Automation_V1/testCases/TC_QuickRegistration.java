package com.Edvak_EHR_Automation_V1.testCases;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.testng.annotations.Test;

import com.Edvak_EHR_Automation_V1.pageObjects.LoginPage;
import com.Edvak_EHR_Automation_V1.pageObjects.QuickRegisterPage;
import com.gargoylesoftware.htmlunit.javascript.host.Console;

public class TC_QuickRegistration extends BaseClass {
	
	@Test(priority=0)
	public void testQuickRegistration() throws InterruptedException {
		
		LoginPage lp= new LoginPage(driver);
		QuickRegisterPage qr = new QuickRegisterPage(driver);
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
				.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/aside[1]/main[1]/nav[1]/a[2]/span[1]/.."));
		element1.click();		
		logger.info("Clicked on dashboard Patient Icon");		
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/app-root/div/div[2]/app-find-a-patient/div/header/div[1]/div/div/sl-icon-button")));
		WebElement element2 = driver
				.findElement(By.xpath("/html/body/app-root/div/div[2]/app-find-a-patient/div/header/div[1]/div/div/sl-icon-button"))
				.getShadowRoot().findElement(By.cssSelector("button"));
		element2.click();
		logger.info("Clicked on patient plus icon");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[1]//a[1]")));
		WebElement element3 = driver
				.findElement(By.xpath("//li[1]//a[1]"));
		element3.click();
		logger.info("Clicked on Quick register button");
		logger.info("Quick Registration page opened");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-header[1]/h2[1]")));
        qr.getTextForQuickRegistrationText();	  
		logger.info("Quick Register Text is visible");
		qr.getTextForMRNText();		
        logger.info("verified MRN Text is visible");
        qr.getTextForMRNnumber();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/form[1]/div[1]/div[1]/div[1]/div[1]/span[1]")));
		logger.info("verified MRN Number  is visible");
		qr.setFirstName("srk");
		logger.info("First name is entered successufully");
		qr.setLastName("Susari");
		logger.info("Last name is entered Sucessfullly");
		qr.setDob("11-09-2000");
		logger.info("Date of Birth is entered Sucessfullly");		
		WebElement genderDropdown = driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/form[1]/div[1]/div[1]/div[1]/div[3]/div[2]/div[2]/select[1]"));
        Select genderSelect = new Select(genderDropdown);
        genderSelect.selectByVisibleText("Male");
		logger.info("Clicked Select Sex AT Birth");
		qr.setMobilePhone("93484980878");
		logger.info("mobile number entered Sucessfullly");
		qr.setHomePhone("9098787869");
		logger.info("Home mobile number entered Sucessfullly");
		qr.setEmail("sourav@gmail.com");		
		logger.info("email entered Sucessfullly");
		qr.setAddressLine1("123 Main Street");
		logger.info("Address1 enteres Sucessfullly");
		qr.setAddressline2("123 Main Sucessfullly");
		WebElement cityField = driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/form[1]/div[1]/div[1]/div[1]/div[6]/div[2]/div[1]/div[2]/app-editable-control[1]/ed-form-row[1]/div[1]/input[1]"));        
		cityField.sendKeys("new York", Keys.TAB);
		Thread.sleep(300);
        WebElement stateField = driver.findElement(By.xpath("//select[@aria-label='State']"));
        Select S1= new Select(stateField);
        logger.info("wait-------");
        Thread.sleep(500);
        S1.selectByVisibleText(" NY ");
        logger.info("State fields is successufully ");
        qr.setZipcode("32432");
        logger.info("Zipcode entered Sucessfully");
        qr.setReferingProvider("marry Smith");       
        logger.info("Refering provider is added Sucessfully");


        WebElement searchField = driver.findElement(By.xpath("//input[@placeholder='Search insurance plan']"));
        searchField.sendKeys("AARP MCR Advanage HMO");
        Thread.sleep(500);


        List<WebElement> suggestionElements = driver.findElements(By.xpath("//*[@id=\"main\"]/ed-drawer/ed-drawer-body/form/div/div[2]/div[2]/div/div[1]/type-ahead/div/div/app-dropdown-templates-loader[1]/app-dropdown-template-insurance/div"));


        for (WebElement suggestion : suggestionElements) {
            if (suggestion.isDisplayed()) {
              logger.info("clicked");
                suggestion.click();
                break; 
            }
        }
        qr.setPolicyNumber("ABCG123456");
        logger.info("Police Number Entered Sucessfully");
        qr.setNoteText("Notes Added");
		logger.info("Notes added sucessufully");
		WebElement save = driver.findElement(By.xpath("//*[@id=\"main\"]/ed-drawer/ed-drawer-footer/sl-button[2]"));
		WebElement cancel = driver.findElement(By.xpath("//*[@id=\"main\"]/ed-drawer/ed-drawer-footer/sl-button[1]"));
		  if (save.isEnabled()) {
	            // Click on the Save button
	            logger.info("saved");
	            save.click();
	            logger.info("saved");
	        } else {
	            // Click on the Close button
	        	logger.info("close");
	            cancel.click();
	            
	        }
//		WebElement save = driver.findElement(By.xpath("//*[@id=\"main\"]/ed-drawer/ed-drawer-footer/sl-button[2]"));
//		save.click();
//		logger.info("saved");
//		WebElement cancel = driver.findElement(By.xpath("//*[@id=\"main\"]/ed-drawer/ed-drawer-footer/sl-button[1]"));
//		cancel.click();
//		WebElement cross = driver.findElement(By.xpath("//*[@id=\"main\"]/ed-drawer/ed-drawer-header/sl-icon-button"));
//		cross.click();
	}

}
