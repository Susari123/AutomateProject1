package com.Edvak_EHR_Automation_V1.pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;


public class FacesheetAllergies {
	WebDriver ldriver;
	public FacesheetAllergies(WebDriver rdriver) {
		ldriver = rdriver;
		PageFactory.initElements(rdriver, this);
	}
	
	@FindBy(xpath="//*[@id=\"sidemenuWrapper\"]/main/nav/a[2]/span[1]")
	WebElement PatientIcon;
	
	@FindBy(xpath="/html/body/app-root/div/div[2]/app-find-a-patient/div/div/table/tbody/tr[2]")
	WebElement PatientDetails;
	
	@FindBy(xpath="//h3[normalize-space()='Allergies']")
	WebElement allergies;
	
	@FindBy(xpath="/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer/ed-drawer-header/div[1]/sl-icon-button")
	WebElement PlusButton;
	
	@FindBy(xpath="/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-allergies[1]/div[1]/ed-drawer[1]/ed-drawer-body[1]/main[1]/div[1]/div[1]/input[1]")
	WebElement allergen;
	
	@FindBy(xpath="/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-allergies[1]/div[1]/ed-drawer[1]/ed-drawer-body[1]/main[1]/div[2]/div[1]/div[1]/input[1]")
	WebElement dateOfOnset;
	
	@FindBy(xpath="/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-allergies[1]/div[1]/ed-drawer[1]/ed-drawer-body[1]/main[1]/div[2]/div[2]/div[1]/select[1]")
	WebElement Servirity;
	
	@FindBy(xpath="/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-allergies[1]/div[1]/ed-drawer[1]/ed-drawer-body[1]/main[1]/div[2]/div[3]/div[1]/input[1]")
	WebElement Reactions;
	
	@FindBy(xpath="/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-allergies[1]/div[1]/ed-drawer[1]/ed-drawer-body[1]/main[1]/div[2]/div[4]/div[1]/textarea[1]")
	WebElement notes;
	
	@FindBy(xpath="/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-allergies[1]/div[1]/ed-drawer[1]/ed-drawer-body[1]/main[1]/div[2]/div[4]/div[1]/textarea[1]")
	WebElement save;
	
	@FindBy(xpath="/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer[1]/ed-drawer-footer/sl-button[1]")
	WebElement cancel;
	
	@FindBy(xpath="/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer[1]/ed-drawer-footer/sl-button[3]")
	WebElement saveAndadd;
	
	@FindBy(xpath="/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-allergies[1]/div[1]/ed-drawer[1]/div[1]/nav[1]/button[2]")
	WebElement inActive;
	
	@FindBy(xpath="/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-allergies[1]/div[1]/ed-drawer[1]/div[1]/nav[1]/button[3]")
	WebElement markasError;
	
	@FindBy(xpath="/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer/ed-drawer-body/div/div/div/div[1]/sl-checkbox")
	WebElement checkbox;
	
	@FindBy(xpath="/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer/div[2]/div/sl-button[1]")
	WebElement inactiveButton;
	
	@FindBy(xpath="/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer/div[2]/div/sl-button[2]")
	WebElement markAsErrorButton;
	
	@FindBy(xpath="/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer/div[2]/div/sl-button[1]")
	WebElement activeButton;
	
	@FindBy(xpath="/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer/div[2]/div/sl-button[2]")
	WebElement InactivemarkAsError;
	
	@FindBy(xpath="/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer/div[1]/nav/button[3]")
	WebElement markasErrorsection;
	
	@FindBy(xpath="/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer/ed-drawer-header/div[2]/sl-tooltip/button")
	WebElement print;
	
	@FindBy(xpath="/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-allergies/div[1]/ed-drawer/ed-drawer-header/div[2]/sl-icon-button")
	WebElement cross;
	
	public void clickPatientIcon() {
		PatientIcon.click();
		
	}
	
	public void clickallergiesIcon() {
		allergies.click();
		
	}
	
	public void clickPlusButtonIcon() {
		PlusButton.click();	
	}
	
	public void setAllergen(String allergenname) {
		allergen.sendKeys(allergenname);	
	}
	
	public void setDateOfOnset(String date) {
		dateOfOnset.sendKeys(date);	
	}

}
	

