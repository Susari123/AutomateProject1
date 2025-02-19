package com.Edvak_EHR_Automation_V1.pageObjects;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
public class BillingGenerateClaims {
    WebDriver ldriver;

    public BillingGenerateClaims(WebDriver rdriver) {
        ldriver = rdriver;
        PageFactory.initElements(rdriver, this);
    }

    // ==================== Billing Page Locators ====================

    @FindBy(xpath = "//span[normalize-space()='attach_money']")
    private WebElement billingButton;

    @FindBy(xpath = "//h4[normalize-space()='billing']")
    private WebElement billingPageHeaderText;

    @FindBy(xpath = "//app-header/header/h4")
    private WebElement dashboardElement;

    @FindBy(xpath = "//nav/a[5]/span[1]/sl-icon")
    private WebElement billingIcon;

    // ==================== New Charge Panel ====================
    @FindBy(xpath = "//form//div//sl-button[@id='tour-guide-billing-Step4']")
    private WebElement newChargeButton;

    @FindBy(xpath = "//h6[contains(text(), 'New Charge')]")
    private WebElement newChargeText;

    @FindBy(xpath = "//ed-drawer-header/div[2]/sl-tooltip/sl-icon-button")
    private WebElement printButton;

    @FindBy(xpath = "//ed-drawer/ed-drawer-header/div[2]/sl-icon-button")
    private WebElement crossButton;

    // ==================== Charge Entry Inputs ====================
    @FindBy(xpath = "//input[@class='w-full form-input']")
    private WebElement patientNameInput;

    @FindBy(xpath = "/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[1]/div[1]/div/type-ahead/div/div/div")
    private WebElement patientNameDropdown;

    @FindBy(xpath = "//div[@class='border border-[#CBD5E1] flex form-select w-full bg-white']")
    private WebElement encounterDropdown;

    @FindBy(xpath = "//div/sl-button-group/sl-button[2]")
    private WebElement newEncounterOption;

    @FindBy(xpath = "/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[4]/div[1]/div[1]/div/input")
    private WebElement icdInput;

    @FindBy(xpath = "/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[4]/div[2]/div[1]/div/div/input")
    private WebElement cptInput;

    @FindBy(xpath = "//tbody//tr//td[8]//input")
    private WebElement amountInput;

    // ==================== Footer Buttons ====================
    @FindBy(xpath = "//ed-drawer//ed-drawer-footer//sl-button[1]")
    private WebElement submitButton;

    @FindBy(xpath = "//*[@id='tour-guide-billing-encounter-step7']")
    private WebElement generateClaimButton;

    @FindBy(xpath = "//footer//sl-button[4]")
    private WebElement transmitButton;

    // ==================== Claim Type Selection ====================
    @FindBy(xpath = "//sl-radio-group//div//sl-radio[2]")
    private WebElement paperClaimOption;

    @FindBy(xpath = "//sl-radio-group//div//sl-radio[1]")
    private WebElement electronicClaimOption;

    // ==================== Self Pay Selection ====================
    @FindBy(xpath = "//div//descendant::ed-select")
    private WebElement selfPayOption;

    @FindBy(xpath = "//div//descendant::ed-option-wrapper//ed-option[@value='self']")
    private WebElement selfPaySelect;

    // ==================== Tab Selection ====================
    @FindBy(xpath = "//sl-tab-group//sl-tab[1]")
    private WebElement chargeTabButton;

    @FindBy(xpath = "//table//tbody//tr//td")
    private WebElement claimsTableRow;

    @FindBy(xpath = "//section[@id='tour-guide-billing-encounter-step1']//descendant::ng-select[@placeholder='Select Ordering Provider']")
    private WebElement orderingProvider;

    // ==================== Misc ====================
    @FindBy(xpath = "//img[contains(@src, 'loader.svg')]")
    private WebElement loaderIcon;

    @FindBy(xpath = "//*[@id='tour-guide-billing-Step3']/form/div/app-filter-panel-head/sl-dropdown")
    private WebElement filterButton;

    @FindBy(xpath = "//*[@id=\"tour-guide-billing-Step3\"]/form/div/div/input")
    private WebElement filterInputField;

    // Encounter Dropdown
    @FindBy(xpath = "//app-encounter-selection/sl-dropdown")
    private WebElement encounterSelectionDropdown;

// New Encounter Option
    @FindBy(xpath = "//div/sl-button-group/sl-button[2]")
    private WebElement newEncounterSelectionOption;

// Provider Dropdown
    @FindBy(className = "ng-select")
    private WebElement providerDropdown;

// Location Dropdown
    @FindBy(xpath = "//ng-select//div[@class='ng-select-container']//span[@class='ng-arrow-wrapper']")
    private WebElement locationDropdown;

// Location Option (Placeholder - will still randomize in method)
    @FindBy(xpath = "(//*[@formcontrolname='location']/descendant::div[@role='option'])")
    private WebElement locationOptions;

// Service Dropdown (2nd select)
    @FindBy(xpath = "/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[1]/div[2]/app-encounter-selection[1]/sl-dropdown[1]/main[1]/div[2]/div[1]/div[2]/div[2]/ng-select[1]/div[1]/div[1]/div[2]/input[1]")
    private WebElement serviceDropdown;

// Date Input Field
    @FindBy(xpath = "/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[1]/div[2]/app-encounter-selection[1]/sl-dropdown[1]/main[1]/div[2]/div[1]/div[2]/div[3]/input[1]")
    private WebElement encounterDateInput;

// Create Encounter Button
    @FindBy(xpath = "/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[1]/div[2]/app-encounter-selection/sl-dropdown/main/div[2]/div/div[3]/sl-button")
    private WebElement createEncounterButton;

// Encounter Number Display
    @FindBy(xpath = "/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[1]/div[2]/app-encounter-selection/sl-dropdown/div/div")
    private WebElement encounterNumberDiv;

    @FindBy(xpath = "/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[4]/div[1]/div[2]/div/div[1]/div")
    private WebElement icdSuggestionOption;

    @FindBy(xpath = "/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[4]/div[2]/div[1]/div[2]/div/div/div")
    private WebElement cptSuggestionOption;

    
    
    // ==================== Getters for Waits & Verifications ====================
    public WebElement getBillingPageHeaderText() {
        return billingPageHeaderText;
    }

    public WebElement getNewChargeButton() {
        return newChargeButton;
    }

    public WebElement getClaimsTableRow() {
        return claimsTableRow;
    }

    public WebElement getChargeTabButton() {
        return chargeTabButton;
    }

    public WebElement getNewChargeText() {
        return newChargeText;
    }

    public WebElement getPrintButton() {
        return printButton;
    }

    public WebElement getCrossButton() {
        return crossButton;
    }

    public WebElement getPatientNameDropdown() {
        return patientNameDropdown;
    }

    public WebElement getPatientNameInput() { 
        return patientNameInput;
    }

    public WebElement getLoaderIcon() {
        return loaderIcon;
    }

    public WebElement getDashboardElement() {
        return dashboardElement;
    }

    public WebElement getBillingIconElement() {
        return billingIcon;
    }

    // ==================== Actions (Methods) ====================
    public void clickBillingButton() {
        billingButton.click();
    }

    public void clickBillingIcon() {
        billingIcon.click();
    }

    public void clickNewChargeButton() {
        newChargeButton.click();
    }

    public void selectOrderingProvider() {
        orderingProvider.click();
    }

    public String getTextBillingPageHeader() {
        return billingPageHeaderText.getText();
    }

    public boolean isDashboardDisplayed() {
        return dashboardElement.isDisplayed();
    }

    public void enterPatientName(String patientName) {
        patientNameInput.sendKeys(patientName);
    }

    public String getPatientNameValue() {
        return patientNameInput.getAttribute("value");
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

    public void enterAmount(String amount) {
        amountInput.sendKeys(amount);
    }

    public void clickSubmitButton() {
        submitButton.click();
    }

    public void clickGenerateClaimButton() {
        generateClaimButton.click();
    }

    public void clickTransmitButton() {
        transmitButton.click();
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

    public void selectPatientFromDropdown() {
        patientNameDropdown.click();
    }
    // Inside BillingGenerateClaims class (near other methods)

    public WebElement waitForBillingPageHeader(WebDriver driver) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
    return wait.until(ExpectedConditions.visibilityOf(billingPageHeaderText));
    }

    public WebElement waitForNewChargeButton(WebDriver driver) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
    return wait.until(ExpectedConditions.visibilityOf(newChargeButton));
    }

    public WebElement waitForClaimsTableRow(WebDriver driver) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
    return wait.until(ExpectedConditions.visibilityOf(claimsTableRow));
    }

    public WebElement waitForChargeTabButton(WebDriver driver) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
    return wait.until(ExpectedConditions.visibilityOf(chargeTabButton));
    }
    public void clickChargeTabIfNotSelected(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    
        // Wait until Charge Tab is visible and clickable
        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(chargeTabButton));
    
        String ariaSelected = button.getAttribute("aria-selected");
    
        if (!"true".equals(ariaSelected)) {
            button.click();
            System.out.println("Charge Tab clicked successfully!");
        } else {
            System.out.println("Charge Tab is already selected, no need to click.");
        }
    }
    
    public WebElement waitForNewChargeText(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
        return wait.until(ExpectedConditions.visibilityOf(newChargeText));
    }
    // Getter Methods for New Elements
    public WebElement getFilterButton() {
    return filterButton;
    }

    public WebElement getFilterInputField() {
    return filterInputField;
    }
    // Getters
public WebElement getEncounterSelectionDropdown() { return encounterSelectionDropdown; }
public WebElement getNewEncounterSelectionOption() { return newEncounterSelectionOption; }
public WebElement getProviderDropdown() { return providerDropdown; }
public WebElement getLocationDropdown() { return locationDropdown; }
public WebElement getServiceDropdown() { return serviceDropdown; }
public WebElement getEncounterDateInput() { return encounterDateInput; }
public WebElement getCreateEncounterButton() { return createEncounterButton; }
public WebElement getEncounterNumberDiv() { return encounterNumberDiv; }
// Getters
public WebElement getIcdSuggestionOption() {
    return icdSuggestionOption;
}

public WebElement getCptSuggestionOption() {
    return cptSuggestionOption;
}
public WebElement getCptInput() {
    return cptInput;
}
public WebElement getIcdInput() {
    return icdInput;
}
    public By getClaimRowXPath(String encounterNumber) {
    return By.xpath("//table//tbody//tr//p[text()=' " + encounterNumber + " ']");
}

public WebElement getSubmitButton() {
    return submitButton;
}

public WebElement getAmountInput() {
    return amountInput;
}

public void clickSubmitAndWaitForLoaderToDisappear(WebDriver driver) {
    submitButton.click();
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    wait.until(ExpectedConditions.invisibilityOf(loaderIcon));
}
}
