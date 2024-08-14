package com.Edvak_EHR_Automation_V1.pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class QuickRegisterPage {
	WebDriver ldriver;

	public QuickRegisterPage(WebDriver rdriver) {
		ldriver = rdriver;
		PageFactory.initElements(rdriver, this);
	}
	@FindBy(xpath="//*[@id=\"sidemenuWrapper\"]/main/nav/a[2]/span[1]")
	WebElement PatientIcon;
	
	@FindBy(xpath= "/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-find-a-patient[1]/div[1]/header[1]/div[1]/div[1]/div[1]")
	WebElement PlusButton;
	
	@FindBy(xpath ="/html/body/app-root/div/div[2]/app-find-a-patient/div/header/div[1]/div/div/div/ul/li[1]/a")
	WebElement QuickRegistration;

	@FindBy(xpath ="//*[@id=\"main\"]/ed-drawer/ed-drawer-header/div/h2")
	WebElement QuickRegistrationText;
	
	@FindBy(xpath ="//*[@id=\"main\"]/ed-drawer/ed-drawer-body/form/div/div[1]/header/h6")
	WebElement basicInfoText;
	
	@FindBy(xpath ="/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-quick-registration/main/ed-drawer/ed-drawer-header/sl-icon-button//button")
	WebElement CrossButton;
	
	@FindBy(xpath ="//*[@id=\"main\"]/ed-drawer/ed-drawer-header/div/span")
	WebElement MRNText;
	
	@FindBy(xpath ="//*[@id=\"main\"]/ed-drawer/ed-drawer-body/form/div/div[1]/div/div[1]/span")
	WebElement MRNnumber;
	
	@FindBy(xpath ="//input[@name='first_name']")
	WebElement FirstName;
	
	@FindBy(xpath ="//input[@name='last_name']")
	WebElement LastName;
	
	@FindBy(xpath ="//input[@name='dob']")
	WebElement Dob;
	
	@FindBy(xpath ="//select[@class='form-select ng-tns-c150-4 ng-pristine ng-invalid is-invalid ng-touched']")
	WebElement SexAtBirth;
	
	@FindBy(xpath ="//*[@id=\"main\"]/ed-drawer/ed-drawer-body/form/div/div[1]/div/div[3]/div[1]/div[2]/input")
	WebElement MobilePhone;
	
	@FindBy(xpath ="//*[@id=\"main\"]/ed-drawer/ed-drawer-body/form/div/div[1]/div/div[4]/div[2]/div/div/input")
	WebElement HomePhone;
	
	@FindBy(xpath ="//*[@id=\"main\"]/ed-drawer/ed-drawer-body/form/div/div[1]/div/div[3]/div[3]/div/div/input")
	WebElement Email;
	
	@FindBy(xpath ="//input[@placeholder='Enter/Search a address']")
	WebElement AddressLine1;
	
	@FindBy(xpath ="//*[@id=\"main\"]/ed-drawer/ed-drawer-body/form/div/div[1]/div/div[4]/div[1]/div[2]/div[2]/input")
	WebElement Addressline2;
	
	@FindBy(xpath ="/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-quick-registration[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/form[1]/div[1]/div[1]/div[1]/div[6]/div[2]/div[1]/div[2]/app-editable-control[1]/ed-form-row[1]/div[1]/input[1]")
	WebElement City;
	
	@FindBy(xpath ="//*[@id=\"main\"]/ed-drawer/ed-drawer-body/form/div/div[1]/div/div[6]/div[2]/div[2]/div[2]/select")
	WebElement selectStateDropdown;
	
	@FindBy(xpath ="//*[@id=\"main\"]/ed-drawer/ed-drawer-body/form/div/div[1]/div/div[4]/div[2]/div[3]/div[2]/app-editable-control/ed-form-row/div/input")
	WebElement Zipcode;
	
	@FindBy(xpath ="//input[@placeholder='Search Provider']")
	WebElement ReferingProvider;
	
	@FindBy(xpath ="//label[@for='billingStatus1']")
	WebElement BillingType;
	
	@FindBy(xpath ="//input[@placeholder='Search insurance plan']")
	WebElement InsuranceName;
	
	@FindBy(xpath ="//div[@class='flex rounded border relative border-transparent w-full']//input[@type='text']")
	WebElement PolicyNumber;
	
	@FindBy(xpath ="//textarea[@class='form-textarea h-14 ng-tns-c150-2 ng-untouched ng-pristine ng-valid']")
	WebElement NoteText;
	
	@FindBy(xpath ="This element is inside Shadow DOM & for such elements XPath won't support.")
	WebElement CloseButton;
	
	@FindBy(xpath ="This element is inside Shadow DOM and for such elements XPath won't support.")
	WebElement SaveButton;
	
//	public void clickQuickPatientIcon()
//	{
//		PatientIcon.click();
//
//	}
	public void clickQuickRegistration()
	{
		QuickRegistration.click();

	}
	
	public void clickPlusButton()
	{
		PlusButton.click();

	}
	
	public void getTextForQuickRegistrationText() 
	{
		QuickRegistrationText.getText();
	}
	
	
	public void clickcrossButton()
	{
		CrossButton.click();

	}
	
	public void getTextForMRNText() 
	{
		MRNText.getText();
	}
	
	public void getTextForMRNnumber() 
	{
		MRNnumber.getText();
	}
	
	public void setFirstName(String fname) {
		FirstName.sendKeys(fname);
	}
	
	public void setLastName(String lname) {
		LastName.sendKeys(lname);
	}
	
	public void setDob(String dob) {
		Dob.sendKeys(dob);
	}
	
	public void setSexAtBirth(String sexAtBirth) {
		SexAtBirth.sendKeys(sexAtBirth);
	}
	
	
	public void setMobilePhone(String mobilePhone) {
		MobilePhone.sendKeys(mobilePhone);
	}
	
	public void setHomePhone(String homePhone) {
		HomePhone.sendKeys(homePhone);
	}
	public void setEmail(String email) {
		Email.sendKeys(email);
	}
	
	public void setAddressLine1(String addressLine1) {
		AddressLine1.sendKeys(addressLine1);
	}
	
	public void setAddressline2(String addressline2) {
		Addressline2.sendKeys(addressline2);
	}
	
	public void setCity(String city) {
		City.sendKeys(city);
	}
	
	public void setSelectCountryDropdown(String state)
	{
	Select selectStateDropdown = new Select(ldriver.findElement(By.xpath("//*[@id=\"main\"]/ed-drawer/ed-drawer-body/form/div/div[1]/div/div[6]/div[2]/div[2]/div[2]/select")));
    selectStateDropdown.selectByVisibleText(state);
	}
	
	public void setZipcode(String zipcode) {
		Zipcode.sendKeys(zipcode);
	}
	
	public void setReferingProvider(String referingProvider) {
		ReferingProvider.sendKeys(referingProvider);
	}
	
	public void setBillingType(String billingType) {
		BillingType.sendKeys(billingType);
	}
	
	public void setInsuranceName(String insuranceName) {
		Select s1= new Select(InsuranceName);
		InsuranceName.sendKeys(insuranceName);
	}
	
	public void setPolicyNumber(String policyNumber) {
		PolicyNumber.sendKeys(policyNumber);
	}
	
	public void setNoteText(String noteText) {
		NoteText.sendKeys(noteText);
	}
	
	public void clickCloseButton()
	{
		CloseButton.click();
	}
	
	public void clickSaveButton()
	{
		SaveButton.click();
	}


	public void clickQuickPatientIcon() {
		PatientIcon.click();
	}

	
}
