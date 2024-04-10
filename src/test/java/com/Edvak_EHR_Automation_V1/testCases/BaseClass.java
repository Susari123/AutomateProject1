package com.Edvak_EHR_Automation_V1.testCases;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import com.Edvak_EHR_Automation_V1.utilities.ReadConfig;

import io.github.bonigarcia.wdm.WebDriverManager;

public class BaseClass {

	ReadConfig readconfig = new ReadConfig();
	public String baseURL = readconfig.getApplicationURL();
	public String loginURL = readconfig.getApplicationLoginURL();
	public String username = readconfig.getUsername();
	public String password = readconfig.getPassword();

	public static WebDriver driver;

	public static Logger logger;

	@Parameters("browser")
	@BeforeClass
	public void setup(String br)

	{

		logger = Logger.getLogger("Edvak EHR Testing");
		PropertyConfigurator.configure("Log4j.properties");

		if (br.equals("chrome")) {
			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver();
		} else if (br.equals("firefox")) {
			WebDriverManager.chromedriver().setup();
			driver = new FirefoxDriver();
		} else if (br.equals("ie")) {
			WebDriverManager.chromedriver().setup();
			driver = new InternetExplorerDriver();
		} else if (br.equals("edge")) {
			WebDriverManager.chromedriver().setup();
			driver = new EdgeDriver();
		}

	}

	@AfterClass
	public void tearDown() {
		driver.quit();
	}

	@AfterMethod
	public void captureScreen(ITestResult result) throws IOException {
		if (ITestResult.FAILURE == result.getStatus()) {
			try {
				// To create reference of TakesScreenshot
				TakesScreenshot screenshot = (TakesScreenshot) driver;
				// Call method to capture screenshot
				File src = screenshot.getScreenshotAs(OutputType.FILE);
				File target = new File(System.getProperty("user.dir") + "/Screenshots/" + result.getName() + ".png");
				FileUtils.copyFile(src, target);
				System.out.println("Screenshot taken");
			} catch (Exception e) {
				System.out.println("Exception while taking screenshot " + e.getMessage());
			}

		}
	}
}