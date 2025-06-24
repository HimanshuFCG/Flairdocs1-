package testcases;

import base.BaseTest;
import config.ConfigReader;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;


public class ExpandPanel extends BaseTest {

    @Test
    public void completeLoginAndTabSelection() {
        ExtentTest test = extent.createTest("Expand Panel");
        Playwright playwright = null;
        Browser browser = null;
        Page page = null;
        
        try {
            // Launch Playwright
            playwright = Playwright.create();
            test.info("Playwright launched");

            // Launch browser
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(false)
                    .setSlowMo(1000));
            test.info("Browser launched");

            // Read viewport size from config.properties (fix property keys to match config file)
            int width = Integer.parseInt(ConfigReader.get("viewportWidth", "1280"));
            int height = Integer.parseInt(ConfigReader.get("viewportHeight", "800"));
            test.info("Viewport size set to: " + width + "x" + height);

            // Create new page with configurable viewport size
            page = browser.newPage(new Browser.NewPageOptions().setViewportSize(width, height));
            test.info("New page created with viewport size");

            // Navigate to login page
            String baseUrl = ConfigReader.get("baseUrl");
            page.navigate(baseUrl, new Page.NavigateOptions()
                    .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                    .setTimeout(60000));
            test.info("Navigated to login page: " + page.title());
            test.info("Page URL: " + page.url());

            // Wait for login form
            page.waitForSelector("#LoginFlairdocs_UserName");
            test.info("Login form is visible");

            // Enter username
            String username = ConfigReader.get("username");
            page.fill("#LoginFlairdocs_UserName", username);
            test.info("Entered username");

            // Enter password
            String password = ConfigReader.get("password");
            page.fill("#LoginFlairdocs_Password", password);
            test.info("Entered password");

            // Wait for the login button to be visible and enabled
            page.waitForSelector("#LoginFlairdocs_LoginButton", 
                new Page.WaitForSelectorOptions().setTimeout(20000).setState(WaitForSelectorState.VISIBLE));
            test.info("Login button is visible");

            // Click the login button (force if needed)
            page.click("#LoginFlairdocs_LoginButton", new Page.ClickOptions().setForce(true));
            test.info("Clicked login button");

            // Wait for page to load after login
            page.waitForLoadState();
            test.info("After login - Page title: " + page.title());

            // Wait for page to fully load
            page.waitForTimeout(5000);

            // Wait for Flairdocs header
            page.waitForSelector(".header-image-container[title='Flairdocs']",
                    new Page.WaitForSelectorOptions().setTimeout(10000));
            test.info("Flairdocs header is visible");

            // Find and click project dropdown
            String dropdownArrowSelector = "#ctl00_Main_ProjectSnapShotDetails_ddlProjSnapShotSearchNum_Arrow";
            page.waitForSelector(dropdownArrowSelector);
            page.click(dropdownArrowSelector);
            test.info("Clicked project dropdown");

            // Wait for dropdown to open
            page.waitForTimeout(2000);

            // Select 11th item (Barrel Spring)
            String listItemSelector = "#ctl00_Main_ProjectSnapShotDetails_ddlProjSnapShotSearchNum_listbox > li:nth-child(11)";
            page.locator(listItemSelector).nth(10).hover();
            page.waitForTimeout(500); // Optional: pause for realism
            page.locator(listItemSelector).nth(10).click();
            test.info("Selected Barrel Spring project (with hover)");

            // Wait for the 'Go to Project Details' button to be visible
            String goToProjectDetailsXPath = "//input[@id='ctl00_Main_ProjectSnapShotDetails_btnProjeSnapShotOpen']";
            page.waitForSelector(goToProjectDetailsXPath);
            // Click the 'Go to Project Details' button
            page.click(goToProjectDetailsXPath);
            test.info("Clicked 'Go to Project Details' button");
            page.waitForTimeout(5000);


            String expandbutton = "//*[@id='ctl00_Main_DynamicContent1_ibPlus']";
            page.waitForSelector(expandbutton);
            page.click(expandbutton);
            test.info("Clicked 'Expand' button");
            page.waitForTimeout(5000);
            
            page.evaluate("window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'})");
            page.waitForTimeout(1000);

            String collapsebutton = "//*[@id='ctl00_Main_DynamicContent1_ibMinus']";
            page.waitForSelector(collapsebutton);
            page.click(collapsebutton);
            test.info("Clicked 'Collapse' button");
            page.waitForTimeout(5000);

        } catch (Exception e) {
            test.fail("Test failed: " + e.getMessage());
            System.out.println("Test failed: " + e.getMessage());
            e.printStackTrace();
            
        }
    }
    @AfterSuite
    public void tearDownSuite() {
        if (extent != null) {
            extent.flush();
        }
    }
}



