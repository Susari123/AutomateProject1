package com.Edvak_EHR_Automation_V1.pageObjects;

import java.time.Duration;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
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
    // New locators for Insurance Balance and Unapplied Amount
    private By insuranceBalanceAmount = By.xpath("//section[2]/ed-row[3]/strong");
    private By unappliedAmountValue = By.xpath("//ed-col/div[2]/strong");
    private By applyAmountInput = By.xpath("//app-apply-insurance-balance/ed-col/div[3]/input");
    private By adjustTransfterinsu = By.xpath("//sl-tooltip/sl-button[contains(text(),'Adjust / Transfer Balance')]");
    private By InsuranceAdj = By.xpath("//ed-drawer-body/div[1]/ed-row[2]/div[1]/div/input");
    private By adjustmentTypeDropdown = By.xpath("//ed-drawer-body/div[1]/ed-row[2]/div[2]/div/ng-select/div/span");
    private By adjustmentOption02Contractual = By.xpath("//span[contains(text(), ' 02 - Contractual Adjustment 1 ')]");
    private By transferAmount = By.xpath("//ed-drawer-body/div[2]/ed-row/div[2]/div/input");
    private By SaveButton = By.xpath("//ed-drawer-footer//sl-button[contains(text(), 'Save ')]");
    private By applybutton = By.xpath("//sl-button[normalize-space(text())='Apply']");
    private By patientBalanceAmount = By.xpath("//ed-drawer-body/section[3]//span[contains(text(),'Patient Balance')]/following-sibling::strong");
    private By patientAdjust = By.xpath("//section[3]//sl-button[contains(text(), 'Adjust')]");
    private By selectPatientPayment = By.xpath("//sl-button[contains(text(), 'Select Patient Payment ')]");
    private By waitLoader = By.xpath("//ed-drawer-body/ng-contaniner/div/div[1]/h6");


    public void clickPatientPayment() {
        driver.findElement(selectPatientPayment).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
        // Wait for skeleton loader to disappear, and loader to appear
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[contains(@class, 'skeleton')]")));
        // wait.until(ExpectedConditions.visibilityOfElementLocated(waitLoader));
    
        try {
            // Check if 'No payments found' message is present in the primary table
            boolean noPaymentsFoundPresent = driver.findElements(By.xpath("//div[contains(text(), ' No payments found ')]")).size() > 0;
    
            if (!noPaymentsFoundPresent) {
                // ✅ Case 1: Primary Table - Data present, hover and click
                WebElement hoverArea = driver.findElement(By.xpath("//app-payment-list-table/div/table/tbody/tr[1]/td[5]/div"));
                hoverAndClickButton(hoverArea, "//app-payment-list-table/div/table/tbody/tr[1]/td[5]/div/sl-button");
    
            } else {
                // ✅ Case 2: Drawer Table - No data in primary table, fallback to drawer table
                System.out.println("'No payments found' message is displayed. Clicking from drawer table...");
    
                // Assuming ed-drawer-body/div[3] is already visible when 'No payments found' is shown.
                WebElement hoverAreaDrawer = driver.findElement(By.xpath("//ed-drawer-body/div[3]//tbody//tr[1]/td[5]/div"));
                hoverAndClickButton(hoverAreaDrawer, "//ed-drawer-body/div[3]//tbody//tr[1]/td[5]/div/sl-button");
            }
        } catch (NoSuchElementException e) {
            System.out.println("Element not found: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }  
    public void applyLesserAmount(double patientAppliedAmount) {
        // Locators
        By paymentBalanceLocator = By.xpath("//section[3]/div/div/app-apply-patient-balance/ed-col/div[2]/strong");
        By amountInputLocator = By.xpath("//div/div/app-apply-patient-balance/ed-col/div[3]/input");
    
        // Get Payment Balance as Double (Remove all non-numeric characters except digits and ".")
        String balanceText = driver.findElement(paymentBalanceLocator).getText().replaceAll("[^\\d.]", "");
        double paymentBalance = Double.parseDouble(balanceText);
    
        // Determine the Lesser Amount
        double amountToApply = Math.min(paymentBalance, patientAppliedAmount);
    
        // Apply the Amount
        WebElement inputElement = driver.findElement(amountInputLocator);
        inputElement.clear();
        inputElement.sendKeys(String.valueOf(amountToApply));
        driver.findElement(applybutton).click();
    }
  
    private void hoverAndClickButton(WebElement hoverArea, String buttonXpath) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", hoverArea);
        System.out.println("Scrolled hover area into view.");
    
        Actions actions = new Actions(driver);
        actions.moveToElement(hoverArea).perform();
        System.out.println("Hovered over the hover area.");
    
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement targetButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(buttonXpath)));
    
        targetButton.click();
        System.out.println("Clicked the target button.");
    }
    
    public void clickpatientAdjust(){
        driver.findElement(patientAdjust).click();
    }
    public double getPatientBalanceAmount() {
    String text = driver.findElement(patientBalanceAmount).getText().replaceAll("[^\\d.]", "");
    return Double.parseDouble(text);
    }

    public void clickApplyButton(){
        driver.findElement(applybutton).click();
    }
    public void clickSaveButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement saveBtn = wait.until(ExpectedConditions.elementToBeClickable(SaveButton));
        saveBtn.click();
        System.out.println("Save button clicked.");
    }
    public void selectAdjustmentType() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    
        // Click dropdown to open options
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(adjustmentTypeDropdown));
        dropdown.click();
        Thread.sleep(4000);
        // Select the desired option
        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(adjustmentOption02Contractual));
        option.click();
    
        System.out.println("Adjustment Type '02 - Contractual Adjustment 1' selected.");
    }
    public void clickadjustTransfterinsu(){
        driver.findElement(adjustTransfterinsu).click();
    }
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
public void handlePatientPayment(String encounterNumber) {
    try {
        WebElement firstButton = driver.findElement(By.xpath("//html/body/app-root/div/div[2]/app-patient-payments/div/div[2]/div[1]/div/table/tbody/tr/td/div/sl-tooltip/sl-button"));
        if (firstButton.isDisplayed()) {
            firstButton.click();
           
        }
    } catch (NoSuchElementException e) {
        try {
            WebElement secondButton = driver.findElement(By.xpath("//app-patient-payments/div/div[2]/div[1]/div[2]/sl-tooltip/sl-button"));
            secondButton.click();
            
        } catch (NoSuchElementException secondException) {
            
        }
    }

    WebElement searchClaim = driver.findElement(By.xpath("//input[@formcontrolname='searchClaim']"));
    searchClaim.sendKeys(encounterNumber);
    
}
public double getInsuranceBalanceAmount() {
    WebElement element = driver.findElement(insuranceBalanceAmount);
    String text = element.getText().trim().replaceAll("[^\\d.]", "");
    return Double.parseDouble(text);
}

public double getUnappliedAmountValue() {
    WebElement element = driver.findElement(unappliedAmountValue);
    String text = element.getText().trim().replaceAll("[^\\d.]", "");
    return Double.parseDouble(text);
}

public void enterAmountToApply(double amount) {
    WebElement inputField = driver.findElement(applyAmountInput);
    inputField.clear();
    inputField.sendKeys(String.valueOf(amount));
}

public void enterAmountToAajust(double amount) {
    WebElement inputField = driver.findElement(InsuranceAdj);
    inputField.sendKeys(String.valueOf(amount));
}
public void enterTransferAmount(double amount) {
    WebElement inputField = driver.findElement(transferAmount);
    inputField.clear(); // Clear the input before entering a new value
    inputField.sendKeys(String.valueOf(amount));
    System.out.println("Transfer Amount Entered: " + amount);
}
}
