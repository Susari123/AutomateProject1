package com.Edvak_EHR_Automation_V1.testCases;

import java.time.Duration;


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
	public void quickRegistration() throws InterruptedException {
		
		LoginPage lp= new LoginPage(driver);
		QuickRegisterPage qr = new QuickRegisterPage();
	    logger.info("********Test Starts Here********");
        logger.info("'loginTestWithValidData' test execution starts here:");
	    logger.info("Opening URL: "+baseURL);
        driver.get(baseURL);
        logger.info("Opened URL: "+baseURL);
        driver.manage().window().maximize();
		logger.info("Entering username in Username Text field");
		lp.setUserName("accountadmin2@ehr.com");
		logger.info("Entered Username in Username Text field");
		logger.info("Entering Password in password Text field");
		lp.setPassword("Edvak@123");
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
		logger.info("Clicked on Quickregister button");
		logger.info("Quick Registration page opened");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-header[1]/h2[1]")));

		String text =driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-header[1]/h2[1]")).getText();
        logger.info(text);		
//       
		logger.info("Quick Register Text is visible");
		String text2 =driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/form[1]/div[1]/div[1]/div[1]/div[1]/label[1]")).getText();
        logger.info(text2);		
        logger.info("mrn Text is visible");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/form[1]/div[1]/div[1]/div[1]/div[1]/span[1]")));
        String text3 =driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/form[1]/div[1]/div[1]/div[1]/div[1]/span[1]")).getText();
        logger.info(text3);
		logger.info("mrn Number  is visible");
		WebElement FirstName = driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/form[1]/div[1]/div[1]/div[1]/div[2]/div[1]/div[2]/input[1]"));
		FirstName.sendKeys("sourav");
		logger.info("First name is entered");
		WebElement LastName = driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/form[1]/div[1]/div[1]/div[1]/div[2]/div[2]/div[2]/input[1]"));
		LastName.sendKeys("susari");
		logger.info("Last name is entered");
		WebElement dobField = driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/form[1]/div[1]/div[1]/div[1]/div[3]/div[1]/div[2]/input[1]"));
        dobField.sendKeys("11-09-2000");
		logger.info("Date of Birth is entered");
		
		WebElement genderDropdown = driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/form[1]/div[1]/div[1]/div[1]/div[3]/div[2]/div[2]/select[1]"));
        Select genderSelect = new Select(genderDropdown);
        genderSelect.selectByVisibleText("Male");
		logger.info("Clicked Select Sex AT Birth");
		
		WebElement mobileNumber = driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/form[1]/div[1]/div[1]/div[1]/div[4]/div[1]/div[2]/input[1]"));
		mobileNumber.sendKeys("0123456789");
		logger.info("mobile number entered");
		WebElement email = driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/form[1]/div[1]/div[1]/div[1]/div[5]/div[1]/div[1]/input[1]"));
		email.sendKeys("sourav12@gmail.com");		
		logger.info("email entered");
		WebElement address1 = driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/form[1]/div[1]/div[1]/div[1]/div[6]/div[1]/div[1]/div[2]/input[1]"));
        address1.sendKeys("123 Main Street");
		WebElement cityField = driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/form[1]/div[1]/div[1]/div[1]/div[6]/div[2]/div[1]/div[2]/app-editable-control[1]/ed-form-row[1]/div[1]/input[1]"));
        
		cityField.sendKeys("new York", Keys.TAB);
		Thread.sleep(300);
       // wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("")));
        WebElement stateField = driver.findElement(By.xpath("//select[@aria-label='State']"));
        Select S1= new Select(stateField);
        logger.info("wait-------");
        Thread.sleep(300);
        S1.selectByVisibleText(" NY ");


        logger.info("---------");

		WebElement zipcode = driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/form[1]/div[1]/div[1]/div[1]/div[6]/div[2]/div[3]/div[2]/app-editable-control[1]/ed-form-row[1]/div[1]/input[1]"));
        zipcode.sendKeys("32216");
        logger.info("Zipcode entered");
        
        WebElement referingProvider = driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/form[1]/div[1]/div[1]/div[1]/div[7]/div[1]/div[1]/type-ahead[1]/div[1]/input[1]"));
        referingProvider.sendKeys("marry smith");
        
        logger.info("---------");
//        WebElement radioButton = driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/form[1]/div[1]/div[2]"));
//        if (!radioButton.isSelected()) {
//            radioButton.click();
//        }
		WebElement Notes = driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/form[1]/div[1]/div[4]/textarea[1]"));
		Notes.sendKeys("Notes added");
		logger.info("Notes added sucessufully");
		WebElement save = driver.findElement(By.xpath("//*[@id=\"main\"]/ed-drawer/ed-drawer-footer/sl-button[2]"));
		save.click();
		WebElement cancel = driver.findElement(By.xpath("//*[@id=\"main\"]/ed-drawer/ed-drawer-footer/sl-button[1]"));
		cancel.click();
		
	}

}
