package com.Edvak_EHR_Automation_V1.utilities;

import java.time.Duration;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.Edvak_EHR_Automation_V1.pageObjects.LoginPage;


public class LoginUtils {
    private static final Logger logger = LogManager.getLogger(LoginUtils.class); // ✅ Correct Log4j Usage

    // Reusable login method
    public static void loginToApplication(WebDriver driver, String baseURL, String username, String password) {
        try {
            LoginPage lp = new LoginPage(driver);
            logger.info("🔑 Logging into the application...");

            driver.get(baseURL);
            driver.manage().window().maximize();

            lp.setUserName(username);
            lp.setPassword(password);

            WebElement loginButton = driver.findElement(By.xpath("/html/body/app-root/div/div/app-login/section/div/div/form/div[3]/sl-button"))
                    .getShadowRoot().findElement(By.cssSelector("button"));
            new Actions(driver).moveToElement(loginButton).click().build().perform();

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//nav/a[5]/span[1]/sl-icon")));

            logger.info("✅ Login successful.");
        } catch (Exception e) {
            logger.error("🚨 Login failed: ", e);
        }
    }
}
