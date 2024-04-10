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
import com.Edvak_EHR_Automation_V1.utilities.XLUtils;

import junit.framework.Assert;

public class TC_LoginTestWithInvalidDataa extends BaseClass{

	@Test(dataProvider="LoginDataWithInvalidDataa")	
	public void loginTestWithInValidData(String user,String pwd) throws InterruptedException

	{   
	    LoginPage lp= new LoginPage(driver);
		logger.info("********Test Starts Here********");
        logger.info("'loginTestWithInValidData' test execution starts here:");
	    logger.info("Opening URL: "+baseURL);
        driver.get(baseURL);
        logger.info("Opened URL: "+baseURL);
		driver.manage().window().maximize();
		logger.info("Entering username in Username Text field "+user );
		lp.setUserName(user);
		logger.info("Entered Username in Username Text field "+user );
		logger.info("Entering Password in password Text field "+pwd);
		lp.setPassword(pwd);
		logger.info("Entered Password in password Text field "+pwd);
		logger.info("Clicking on Login button");
		WebElement element = driver
				.findElement(By.xpath("/html/body/app-root/div/div/app-login/section/div/div/form/div[3]/sl-button"))
				.getShadowRoot().findElement(By.cssSelector("button"));
		Actions actions = new Actions(driver);
		actions.moveToElement(element).click().build().perform();
		logger.info("Clicked on Login button");
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(50));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@aria-label='The email address or password you entered is not valid']")));	
		if(driver.findElement(By.xpath("//div[@aria-label='The email address or password you entered is not valid']")).equals("The email address or password you entered is not valid"))
			{
			  logger.info("Error message displayed matches the expected text");
			  logger.info("'loginTestWithInValidData' test PASSED");
		      driver.get(baseURL);
			  Assert.assertTrue(true);
			}
		else
		{
			  logger.info("Meesage displayed does not match the expected text");
			  logger.info("'loginTestWithInValidData' test FAILED");
		      driver.get(baseURL);
			  Assert.assertTrue(false);
		}
		
	}
	
	@DataProvider(name="LoginDataWithInvalidDataa")
	String [][] getData() throws IOException
	{
		String path=System.getProperty("user.dir")+"/src/test/java/com/Edvak_EHR_Automation_V1/testData/LoginDataWithInvalidDataa.xlsx";
		
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
