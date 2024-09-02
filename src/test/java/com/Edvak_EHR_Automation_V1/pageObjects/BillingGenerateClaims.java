package com.Edvak_EHR_Automation_V1.pageObjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;


public class BillingGenerateClaims {
	WebDriver ldriver;
	public BillingGenerateClaims(WebDriver rdriver) {
		ldriver = rdriver;
		PageFactory.initElements(rdriver, this);
	}
	// Locators
    @FindBy(xpath = "//span[normalize-space()='attach_money']")
    private WebElement billingButton;

    @FindBy(xpath = "//input[@class='w-full form-input']")
    private WebElement patientNameInput;

    @FindBy(xpath = "//div[@class='border border-[#CBD5E1] flex form-select w-full bg-white']")
    private WebElement encounterDropdown;

    @FindBy(xpath = "//div/sl-button-group/sl-button[2]")
    private WebElement newEncounterOption;

    @FindBy(xpath = "/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[4]/div[1]/div[1]/div/input")
    private WebElement icdInput;

    @FindBy(xpath = "/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[4]/div[2]/div[1]/div/div/input")
    private WebElement cptInput;

    @FindBy(xpath = "//ed-drawer//ed-drawer-footer//sl-button[1]")
    private WebElement submitButton;

    @FindBy(xpath = "//tbody//tr//td[8]//input")
    private WebElement amountInput;

    @FindBy(xpath = "//sl-radio-group//div//sl-radio[2]")
    private WebElement paperClaimOption;

    @FindBy(xpath = "//sl-radio-group//div//sl-radio[1]")
    private WebElement electronicClaimOption;

    @FindBy(xpath = "//*[@id=\"tour-guide-billing-encounter-step7\"]")
    private WebElement generateClaimButton;

    @FindBy(xpath = "//footer//sl-button[4]")
    private WebElement transmitButton;

    @FindBy(xpath = "//div//descendant::ed-select")
    private WebElement selfPayOption;

    @FindBy(xpath = "//div//descendant::ed-option-wrapper//ed-option[@value='self']")
    private WebElement selfPaySelect;

    // Methods to interact with elements
    public void clickBillingButton() {
        billingButton.click();
    }

    public void enterPatientName(String patientName) {
        patientNameInput.sendKeys(patientName);
    }

    public void selectEncounterDropdown() {
        encounterDropdown.click();
    }

    public void selectNewEncounterOption() {
        newEncounterOption.click();
    }

    public void enterICD(String icdCode) {
        icdInput.sendKeys(icdCode);
    }

    public void enterCPT(String cptCode) {
        cptInput.sendKeys(cptCode);
    }

    public void clickSubmitButton() {
        submitButton.click();
    }

    public void enterAmount(String amount) {
        amountInput.sendKeys(amount);
    }

    public void selectPaperClaimOption() {
        paperClaimOption.click();
    }

    public void selectElectronicClaimOption() {
        electronicClaimOption.click();
    }

    public void selectSelfPayOption() {
        selfPayOption.click();
    }

    public void selectSelfPay() {
        selfPaySelect.click();
    }

    public void clickGenerateClaimButton() {
        generateClaimButton.click();
    }

    public void clickTransmitButton() {
        transmitButton.click();
    }
}