package com.Edvak_EHR_Automation_V1.pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage {

	WebDriver ldriver;

	public LoginPage(WebDriver rdriver) {
		ldriver = rdriver;
		PageFactory.initElements(rdriver, this);
	}

	@FindBy(xpath = "//img[@class='w-52 mb-md']")
	WebElement edvakLogoLoginPage;

	@FindBy(xpath = "//input[@placeholder='Email address']")
	WebElement userName;

	@FindBy(xpath = "//input[@placeholder='Password']")
	WebElement passWord;

	// driver.findElement(By.id("/html/body/app-root/div/div/app-login/section/div/div/form/div[3]/sl-button")).getShadowRoot().findElement(By.cssSelector("label")).findElement(By.tagName("input"))

	@FindBy(xpath = "//input[@id='btnLogin']")
	WebElement loginButton;

	@FindBy(xpath = "//a[@class='text-gray-600']")
	WebElement forgetPasswordLink;
	
	
	@FindBy(xpath = "/html[1]/body[1]/app-root[1]/div[1]/div[2]/div[1]/app-header[1]/header[1]/div[1]/section[1]/div[2]/button[1]")
	WebElement userIconDashboard;
	
    @FindBy(xpath="/html[1]/body[1]/app-root[1]/div[1]/div[2]/div[1]/app-header[1]/header[1]/div[1]/section[1]/div[2]/div[1]/div[1]/div[2]/p[1]")
    WebElement loggedUserEmail;
    
    @FindBy(xpath="/html[1]/body[1]/app-root[1]/div[1]/div[2]/div[1]/app-header[1]/header[1]/div[1]/section[1]/div[2]/div[1]/div[3]/div[1]")
    WebElement logoutButton;
    
    @FindBy(xpath="//div[@aria-label='The email address or password you entered is not valid']")
    WebElement invalidLoginErrorMessage;
    
	public void setUserName(String uname) {
		userName.sendKeys(uname);
	}

	public void setPassword(String pswd) {
		passWord.sendKeys(pswd);
	}

	public void clickLogin()

	{
		loginButton.click();

	}
	
	public void clickUserIconDashboard()

	{
		userIconDashboard.click();

	}
	
	public void getTextForLoggedInUserEmail() 
	{
		loggedUserEmail.getText();
	}

	public void clickLogoutButton()
	{
		logoutButton.click();
	}
	
	public void getTextForInvalidLoginErrorMessage() 
	{
		invalidLoginErrorMessage.getText();
	}
}
