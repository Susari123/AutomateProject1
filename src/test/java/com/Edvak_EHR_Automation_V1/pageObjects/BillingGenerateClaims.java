package com.Edvak_EHR_Automation_V1.pageObjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;


public class BillingGenerateClaims {
	WebDriver ldriver;
	public BillingGenerateClaims(WebDriver rdriver) {
		ldriver = rdriver;
		PageFactory.initElements(rdriver, this);
	}
	@FindBy(xpath="//*[@id=\"sidemenuWrapper\"]/main/nav/a[2]/span[1]")
	WebElement PatientIcon;
}
