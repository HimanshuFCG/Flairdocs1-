package rough;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import pages.LoginPage;

public class LoginTest {
    public static void main(String[] args) {
    Playwright playwright = Playwright.create();
    Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setChannel("chrome").setHeadless(false));
    Page page = browser.newPage();
    page.navigate("https://demo.flairdocs.com/DOTV2/Login.aspx");
    LoginPage loginPage = new LoginPage(page);
    loginPage.login();
        
    }
}
