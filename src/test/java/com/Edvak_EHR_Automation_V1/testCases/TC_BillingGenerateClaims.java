package com.Edvak_EHR_Automation_V1.testCases;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Edvak_EHR_Automation_V1.pageObjects.LoginPage;
import com.Edvak_EHR_Automation_V1.pageObjects.QuickRegisterPage;
import com.Edvak_EHR_Automation_V1.pageObjects.FacesheetAllergies;

public class TC_BillingGenerateClaims  extends BaseClass {
	@Test(priority=0)
	public void testQuickRegistration() throws InterruptedException {
		LoginPage lp= new LoginPage(driver);
		QuickRegisterPage qr = new QuickRegisterPage(driver);
		FacesheetAllergies fs = new FacesheetAllergies(driver);
		logger.info("********Test Starts Here********");
        logger.info("'testQuickRegistrationtWithValidData' test execution starts here:");
	    logger.info("Opening URL: "+baseURL);
        driver.get(baseURL);
        logger.info("Opened URL: "+baseURL);
        driver.manage().window().maximize();
		logger.info("Entering username in Username Text field");
		lp.setUserName("souravsusari311@gmail.com");
		logger.info("Entered Username in Username Text field");
		logger.info("Entering Password in password Text field");
		lp.setPassword("Admin@321");
		logger.info("Entered Password in password Text field");
		logger.info("Clicking on Login button");
		WebElement element = driver
				.findElement(By.xpath("/html/body/app-root/div/div/app-login/section/div/div/form/div[3]/sl-button"))
				.getShadowRoot().findElement(By.cssSelector("button"));
		Actions actions = new Actions(driver);
		actions.moveToElement(element).click().build().perform();
		logger.info("Clicked on Login button");
		
		
}
	@Test(priority=1, dataProvider="dataProviderTest", dependsOnMethods = {"testQuickRegistration"})
	void Test(Map<String, Object> data) throws InterruptedException {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(100));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/div[1]/app-header[1]/header[1]/div[1]/section[1]/div[2]/button[1]")));
		WebElement element1 = driver
				.findElement(By.xpath("//span[normalize-space()='attach_money']"));
		int retryCount = 0;
        boolean clicked = false;
        while (retryCount < 3 && !clicked) { // Retry for a maximum of 3 times
            try {
                // Click on the element
                element1.click();
                // If click is successful, set clicked flag to true
                clicked = true;
            } catch (org.openqa.selenium.ElementClickInterceptedException e) {
                // Handle ElementClickInterceptedException
                System.out.println("Attempt " + (retryCount + 1) + ": Element click intercepted. Retrying...");
                retryCount++;
            }
        }
        logger.info("Biiling Button is clicked");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id=\"sl-tab-panel-1\"]/app-coding-list/ed-col/section/div")));
        WebElement NewCharge= driver.findElement(By.xpath("//*[@id=\"sl-tab-panel-1\"]/app-coding-list/ed-col/section/form/div/sl-button"));
        NewCharge.click();
        if (isAlertPresent(driver)) {
      
            Alert alert = driver.switchTo().alert();
            alert.accept();
        }
        
        WebElement Patientname = driver.findElement(By.xpath("//input[@class='w-full form-input']"));

//        String patientName = "Rebill Test";

        Patientname.sendKeys((CharSequence[]) data.get(Patientname));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[1]/div[1]/div[1]/type-ahead[1]/div[1]/div[1]/div[1]")));

    
        Thread.sleep(400);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[1]/div[1]/div[1]/type-ahead[1]/div[1]/div[1]/div[1]")));
        WebElement patientName1 = driver.findElement(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[1]/div[1]/div[1]/type-ahead[1]/div[1]/div[1]/div[1]"));
        patientName1.click();
        
        WebElement Encounter = driver.findElement(By.xpath("//div[@class='border border-[#CBD5E1] flex form-select w-full bg-white']"));
        Encounter.click();
        logger.info("Encounter clicked ");
        
        WebElement newEncounter = driver.findElement(By.xpath("//div/sl-button-group/sl-button[2]"));
        newEncounter.click();
        Thread.sleep(2000);
    	
        WebElement ngSelect = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("ng-select")));
        ngSelect.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ng-dropdown-panel-items")));
        WebElement option = driver.findElement(By.xpath("//ng-dropdown-panel//div//div[2]//div[1]"));
        option.click();
        
        logger.info("provider name entered");
        WebElement ngSelect1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//ng-select//div[@class='ng-select-container']//span[@class='ng-arrow-wrapper']")));
        ngSelect1.click();
        logger.info("---------");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ng-dropdown-panel-items")));
        WebElement option1 = driver.findElement(By.xpath("//ng-dropdown-panel//div//div[2]//div[2]"));
        option1.click();
    	
        
        
        WebElement ngSelect2 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[1]/div[2]/app-encounter-selection[1]/sl-dropdown[1]/main[1]/div[2]/div[1]/div[2]/div[2]/ng-select[1]/div[1]/div[1]/div[2]/input[1]")));
        ngSelect2.click();
        logger.info("---------");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("ng-dropdown-panel-items")));
        WebElement option2 = driver.findElement(By.xpath("//ng-dropdown-panel//div//div[2]//div[1]"));
        option2.click();    
        
        WebElement ngSelect3 = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html[1]/body[1]/app-root[1]/div[1]/div[2]/app-right-side-bar[1]/ed-modal[1]/app-charge-entry[1]/main[1]/ed-drawer[1]/ed-drawer-body[1]/div[1]/div[2]/app-encounter-selection[1]/sl-dropdown[1]/main[1]/div[2]/div[1]/div[2]/div[3]/input[1]")));
        ngSelect3.sendKeys("date");
        
        WebElement createEncounter = driver.findElement(By.xpath("//div//div//div[3]//sl-button"));
        createEncounter.click();
        
//        WebElement serviceLocation = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[1]/div[2]/app-encounter-selection/sl-dropdown/main/div[2]/div/div[1]/ng-select/div/div"));
//        serviceLocation.sendKeys(" 125TH ST. DENTAL GROUP, PLLC ");
        
        
        Thread.sleep(4000);
        WebElement icd = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[4]/div[1]/div[1]/div/input"));
        icd.sendKeys("cholera");
        Thread.sleep(2000);
        WebElement icd1 = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[4]/div[1]/div[2]/div/div[1]/div"));
        icd1.click();
        
        
        WebElement cpt = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[4]/div[2]/div[1]/div/div/input"));
        cpt.sendKeys("99202");
        Thread.sleep(2000);
        
        WebElement cpt1 = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-body/div[4]/div[2]/div[1]/div[2]/div/div/div"));
        cpt1.click();
        
        WebElement submit = driver.findElement(By.xpath("/html/body/app-root/div/div[2]/app-right-side-bar/ed-modal/app-charge-entry/main/ed-drawer/ed-drawer-footer/sl-button[3]"));
        submit.click();
        
        
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//sl-tab-panel//*[@id=\"tour-guide-billing-Step5\"]/tr[1]")));
        Thread.sleep(4000);
        WebElement GenerateClaim = driver.findElement(By.xpath("//table//tbody//tr[1]"));
        GenerateClaim.click();
        Thread.sleep(10000);
        
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div//ng-select[@class='ng-select ng-select-single ng-select-searchable ng-pristine ng-valid ng-select-bottom ng-touched']")));
//        WebElement Location= driver.findElement(By.xpath("//div//ng-select[@class='ng-select ng-select-single ng-select-searchable ng-pristine ng-valid ng-select-bottom ng-touched']"));
//        Location.click();
        WebElement amount= driver.findElement(By.xpath("//tbody//tr//td[8]//input"));
        amount.sendKeys("200");
        
        WebElement paperClaim = driver.findElement(By.xpath("//*[@id=\"tour-guide-billing-encounter-step1\"]/div[2]/div[1]/div[1]/sl-radio-group/div/sl-radio[2]"));
        paperClaim.click();
        
        WebElement cpt2 = driver.findElement(By.xpath("//*[@id=\"tour-guide-billing-encounter-step5\"]/sl-icon-button"));
        cpt2.click();

        WebElement cptcode = driver.findElement(By.xpath("//ed-drawer-body//ul//li[25]"));
        cptcode.click();
        WebElement close = driver.findElement(By.xpath("//ed-drawer-header//sl-icon-button"));
        close.click();
        
        WebElement modifiers = driver.findElement(By.xpath("//div//ed-select[@class='!bg-white border']"));
        modifiers.click();
        WebElement modifi = driver.findElement(By.xpath("//div//ed-select[@class='!bg-white border']//ed-option-wrapper//ed-option[2]"));
        modifi.click();
        
        WebElement GenerateClaim1 = driver.findElement(By.xpath("//*[@id=\"tour-guide-billing-encounter-step7\"]"));
        GenerateClaim1.click();
	}

	private void get(String string) {
		// TODO Auto-generated method stub
		
	}
	@DataProvider(name = "dataProviderTest")
	Object[][] dataProvider() {
		
		 Map<String, Object> dataSet1 = new HashMap<>();
	        dataSet1.put("date", "2024-08-15");
	        dataSet1.put("patientName", "Sourav Susari");
	        return new Object[][] {
	            { dataSet1 }
	        };
		
	}
	private boolean isAlertPresent(WebDriver driver) {
		// TODO Auto-generated method stub
		return false;
	}
}
