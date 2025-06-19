package testcases;

import base.BaseTest;

import com.microsoft.playwright.Browser;

import org.testng.annotations.Test;

public class LoginTest extends BaseTest {
    
    @Test
    public void loginTest() {
        Browser browser = getBrowser("chrome");
        navigate(browser,"https://demo.flairdocs.com/DOTV2/Login.aspx");
        
    }
}
