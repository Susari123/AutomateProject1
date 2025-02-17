package com.Edvak_EHR_Automation_V1.pageObjects;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PaymentPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public PaymentPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    // Locators (as By objects)
    private By paymentTab = By.xpath("//sl-tab-group//sl-tab[3]");
    private By plusButton = By.xpath("//sl-icon-button[@name='add']");
    private By newPaymentHeader = By.xpath("//ed-drawer-header//h6[contains(text(), 'New Payment')]");
    private By submissionDateInput = By.xpath("//input[@formcontrolname='submission_Date']");
    private By paymentTypeDropdown = By.xpath("//ed-drawer/ed-drawer-body/div[2]/div/ng-select/div/div/div[3]");
    private By dropdownOptions = By.xpath("//ng-dropdown-panel//div[2]//div");

    private By patientPaymentOption = By.xpath("//ng-dropdown-panel//span[contains(text(), ' Patient ')]");
    private By patientSearchInput = By.xpath("//input[@placeholder ='Search Patient']");
    private By patientSearchResult = By.xpath("//ed-drawer/ed-drawer-body/div[3]/div/type-ahead/div/div/div/section");

    private By insurancePaymentOption = By.xpath("//ng-dropdown-panel//span[contains(text(), 'Insurance')]");
    private By insuranceSearchInput = By.xpath("//input[@placeholder ='Search insurance plan']");

    public void clickPaymentTab() {
        driver.findElement(paymentTab).click();
    }

    public void clickPlusButton() {
        driver.findElement(plusButton).click();
    }

    public void waitForNewPaymentHeader() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(newPaymentHeader));
    }

    public void enterSubmissionDate(String date) {
        driver.findElement(submissionDateInput).sendKeys(date);
    }

    public void openPaymentTypeDropdown() {
        driver.findElement(paymentTypeDropdown).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(dropdownOptions));
    }

    public void selectPatientPaymentOption() {
        driver.findElement(patientPaymentOption).click();
    }

    public void searchAndSelectPatient(String patientName) {
        driver.findElement(patientSearchInput).sendKeys(patientName);
        wait.until(ExpectedConditions.elementToBeClickable(patientSearchResult)).click();
    }

    public void selectInsurancePaymentOption() {
        driver.findElement(insurancePaymentOption).click();
    }

    public void searchAndSelectInsurancePlan(String insuranceName) {
        driver.findElement(insuranceSearchInput).sendKeys(insuranceName);
    }

    // -------------------- THE 3 MISSING METHODS -----------------------
    public boolean isPatientPaymentOptionEnabled() {
        return driver.findElement(patientPaymentOption).isEnabled();
    }

    public boolean isInsurancePaymentOptionEnabled() {
        return driver.findElement(insurancePaymentOption).isEnabled();
    }

    public void selectInsurancePlanResult() {
        By insurancePlanResult = By.xpath("//app-dropdown-templates-loader[1]/app-dropdown-template-insurance/div/section/span");
        wait.until(ExpectedConditions.visibilityOfElementLocated(insurancePlanResult));
        driver.findElement(insurancePlanResult).click();
    }
    public void selectModeOfPayment(String mode) {
    WebElement modeOfPayment = driver.findElement(By.xpath("//ed-drawer/ed-drawer-body/div[4]/div/ng-select/div/span"));
    modeOfPayment.click();

    WebElement paymentOption = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//ng-dropdown-panel//span[contains(text(), ' " + mode + " ')]")
    ));
    paymentOption.click();
   
}
public void enterPaymentAmount(String amount) {
    WebElement amountField = driver.findElement(By.xpath("//app-new-payments/main/ed-drawer/ed-drawer-body/div[5]/div/input"));
    amountField.sendKeys(amount);
    
}
public void enterPaymentNotes(String note) {
    WebElement notesField = driver.findElement(By.xpath("//app-new-payments/main/ed-drawer/ed-drawer-body/div[6]/div/textarea"));
    notesField.sendKeys(note);
    
}
public void submitPayment() {
    WebElement addPaymentButton = driver.findElement(By.xpath("//ed-drawer/ed-drawer-footer/sl-button[1]"));
    addPaymentButton.click();
    
}
public void waitForPaymentToAppear() {
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table//tbody//tr")));
}
public void openCreatedPayment() {
    WebElement paymentEntry = driver.findElement(By.xpath("//table//tbody//tr"));
    paymentEntry.click();
    

    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//app-patient-payments/div/header/h4")));
}
public String getPaymentType() {
    WebElement paymentTypeElement = driver.findElement(By.xpath("//app-patient-payments/div/div[1]/div[1]/div[2]/div[1]/strong"));
    return paymentTypeElement.getText().trim();
}

}
