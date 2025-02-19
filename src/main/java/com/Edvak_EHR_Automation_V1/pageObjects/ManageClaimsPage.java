package com.Edvak_EHR_Automation_V1.pageObjects;

import org.openqa.selenium.By;

public class ManageClaimsPage {

    public static final By MANAGE_CLAIMS_TAB = By.xpath("//sl-tab-group//sl-tab[2]");
    public static final By LOADER_ICON = By.xpath("//img[contains(@src, 'loader.svg')]");
    public static final By TRANSMIT_BUTTON = By.xpath("//app-claims-list/ed-col/section/form/div/sl-button");
    public static final By BILLING_ICON = By.xpath("//nav/a[5]/span[1]/sl-icon");
}
    
