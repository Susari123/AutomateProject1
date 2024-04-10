package com.Edvak_EHR_Automation_V1.testCases;

import java.io.IOException;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Edvak_EHR_Automation_V1.pageObjects.LoginPage;
import com.Edvak_EHR_Automation_V1.testCases.BaseClass;
import com.Edvak_EHR_Automation_V1.utilities.XLUtils;

import junit.framework.Assert;

public class TC_LoginWithValidData extends BaseClass {

	@Test(priority=0)
	public void testIfLoginPageOpened() {
		LoginPage lp = new LoginPage(driver);
		logger.info("********Test Starts Here********");
		logger.info("'testIfLoginPageOpened' test execution starts here:");
		logger.info("Opening URL: " + baseURL);
		driver.get(baseURL);
		logger.info("Opened URL: " + baseURL);
		driver.manage().window().maximize();
		logger.info("Verifying if Login page Edvak logo is present");
		WebElement ImageFile = driver.findElement(By.xpath("//img[@class='w-52 mb-md']"));
		Boolean ImagePresent = (Boolean) ((JavascriptExecutor) driver).executeScript(
				"return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0",
				ImageFile);
		if (!ImagePresent) {
			logger.info("Getting image of Edvak logo at Login page and verifying if image is present");
			logger.info("Expected image of 'Edvak logo at Login page' not present");
			logger.info("'testIfLoginPageOpened' test FAILED");
			Assert.assertTrue(false);
		} else {
			logger.info("Getting image of Edvak logo at Login page and verifying if image is present");
			logger.info("Expected image of 'Edvak logo at Login page' is present");
			logger.info("'testIfLoginPageOpened' test PASSED");
			Assert.assertTrue(true);
		}
		

	}

	@Test(dataProvider="LoginData",dependsOnMethods = {"testIfLoginPageOpened"})
	public void loginTestWithValidData(String user,String pwd) throws InterruptedException
	{   
	    LoginPage lp= new LoginPage(driver);
	    logger.info("********Test Starts Here********");
        logger.info("'loginTestWithValidData' test execution starts here:");
	    logger.info("Opening URL: "+baseURL);
        driver.get(baseURL);
        logger.info("Opened URL: "+baseURL);
        driver.manage().window().maximize();
		logger.info("Entering username in Username Text field");
		lp.setUserName(user);
		logger.info("Entered Username in Username Text field");
		logger.info("Entering Password in password Text field");
		lp.setPassword(pwd);
		logger.info("Entered Password in password Text field");
		logger.info("Clicking on Login button");
		WebElement element = driver
				.findElement(By.xpath("/html/body/app-root/div/div/app-login/section/div/div/form/div[3]/sl-button"))
				.getShadowRoot().findElement(By.cssSelector("button"));
		Actions actions = new Actions(driver);
		actions.moveToElement(element).click().build().perform();
		logger.info("Clicked on Login button");
		logger.info("Clicking on dashboard user icon");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/div[1]/app-header[1]/header[1]/div[1]/section[1]/div[2]/button[1]")));
		lp.clickUserIconDashboard();
		logger.info("Clicked on dashboard user icon");
		logger.info("Getting email of logged in user");
		lp.getTextForLoggedInUserEmail();
		logger.info("Got email of logged in user");
		logger.info("Verifying if user is able to log into his account");
		
		if(driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/div[1]/app-header[1]/header[1]/div[1]/section[1]/div[2]/div[1]/div[1]/div[2]/p[1]")).getText().equalsIgnoreCase(user))
		{   
			logger.info("User is able to Login using valid credentials");
			logger.info("User is logging out of his account");
			lp.clickLogoutButton();
			logger.info("User logged out of his account");
			Assert.assertTrue(true);
		}
		else
		{
			Assert.assertFalse(false);
		    logger.info("User is not able to login with valid credentials");
		}

	}
	
	@DataProvider(name="LoginData")
	String [][] getData() throws IOException
	{
		String path=System.getProperty("user.dir")+"/src/test/java/com/Edvak_EHR_Automation_V1/testData/LoginData.xlsx";
		
		int rownum=XLUtils.getRowCount(path, "Sheet1");
		int colcount=XLUtils.getCellCount(path,"Sheet1",1);
		
		String logindata[][]=new String[rownum][colcount];
		
		for(int i=1;i<=rownum;i++)
		{
			for(int j=0;j<colcount;j++)
			{
				logindata[i-1][j]=XLUtils.getCellData(path,"Sheet1", i,j);//1 0
			}
				
		}
	return logindata;
    }
}