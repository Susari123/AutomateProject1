package com.Edvak_EHR_Automation_V1.testCases;



import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import io.github.bonigarcia.wdm.WebDriverManager;

public class TestScript {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();
		driver.get("https://darwinapi.edvak.com");
		driver.findElement(By.xpath("//input[@placeholder='Email address']")).sendKeys("souravsusari311@gmail.com");
		driver.findElement(By.xpath("//input[@placeholder='Password']")).sendKeys("Admin@321");
		WebElement element = driver
				.findElement(By.xpath("/html/body/app-root/div/div/app-login/section/div/div/form/div[3]/sl-button"))
				.getShadowRoot().findElement(By.cssSelector("button"));
		Actions actions = new Actions(driver);
		actions.moveToElement(element).click().build().perform();
		Thread.sleep(5000);
		driver.close();
	}

}
