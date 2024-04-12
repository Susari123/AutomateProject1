package com.Edvak_EHR_Automation_V1.pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class QuickRegisterPage {
	WebDriver ldriver;

	public void quickRegisterPage(WebDriver rdriver) {
		ldriver = rdriver;
		PageFactory.initElements(rdriver, this);
	}
	@FindBy(xpath ="/html/body/app-root/div/div[2]/app-find-a-patient/div/header/div[1]/div/div/div/ul/li[1]/a")
	WebElement quickRegistration;

	@FindBy(xpath ="//*[@id=\"main\"]/ed-drawer/ed-drawer-header/h2")
	WebElement quickRegistrationText;
	
	@FindBy(xpath ="//*[@id=\"main\"]/ed-drawer/ed-drawer-body/form/div/div[1]/header/h6")
	WebElement basicInfoText;
	
	@FindBy(xpath ="/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-quick-registration/main/ed-drawer/ed-drawer-header/sl-icon-button//button")
	WebElement crossButton;
	
	@FindBy(xpath ="//*[@id=\"main\"]/ed-drawer/ed-drawer-body/form/div/div[1]/div/div[1]/label")
	WebElement MRNText;
	
	@FindBy(xpath ="//*[@id=\"main\"]/ed-drawer/ed-drawer-body/form/div/div[1]/div/div[1]/span")
	WebElement MRNnumber;
	
	@FindBy(xpath ="//input[@name='first_name']")
	WebElement FirstName;
	
	@FindBy(xpath ="//input[@name='last_name']")
	WebElement LastName;
	
	@FindBy(xpath ="//input[@name='dob']")
	WebElement Dob;
	
	@FindBy(xpath ="//input[@type='tel']")
	WebElement MobilePhone;
	
	@FindBy(xpath ="//*[@id=\"main\"]/ed-drawer/ed-drawer-body/form/div/div[1]/div/div[4]/div[2]/div/div/input")
	WebElement HomePhone;
	
	@FindBy(xpath ="//input[@name='email']")
	WebElement Email;
	
	@FindBy(xpath ="//input[@placeholder='Enter/Search a address']")
	WebElement AddressLine1;
	
	@FindBy(xpath ="/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/form[1]/div[1]/div[1]/div[1]/div[6]/div[1]/div[2]/div[2]/input[1]")
	WebElement Addressline2;
	
	@FindBy(xpath ="/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/form[1]/div[1]/div[1]/div[1]/div[6]/div[2]/div[1]/div[2]/app-editable-control[1]/ed-form-row[1]/div[1]/input[1]")
	WebElement City;
	
	@FindBy(xpath ="//select[@aria-label='State']")
	WebElement State;
	
	@FindBy(xpath ="//*[@id=\"main\"]/ed-drawer/ed-drawer-body/form/div/div[1]/div/div[6]/div[2]/div[3]/div[2]/app-editable-control/ed-form-row/div/input")
	WebElement Zipcode;
	
	@FindBy(xpath ="//input[@placeholder='Search Provider']")
	WebElement ReferingProvider;
	
	@FindBy(xpath ="//label[@for='billingStatus1']")
	WebElement BillingType;
	
	@FindBy(xpath ="//textarea[@class='form-textarea h-14 ng-tns-c150-2 ng-untouched ng-pristine ng-valid']")
	WebElement NoteText;
	
	@FindBy(xpath ="This element is inside Shadow DOM & for such elements XPath won't support.")
	WebElement closeButton;
	
	@FindBy(xpath ="This element is inside Shadow DOM and for such elements XPath won't support.")
	WebElement SaveButton;
	
	public void setFirstName(String fname) {
		FirstName.sendKeys(fname);
	}
	
	public void setLastName(String lname) {
		LastName.sendKeys(lname);
	}
	
	public void setDob(String dob) {
		Dob.sendKeys(dob);
	}
	
	public void setMobilePhone(String mobilePhone) {
		MobilePhone.sendKeys(mobilePhone);
	}
	
	
	
	public void setHomePhone(String email) {
		Email.sendKeys(email);
	}
}
