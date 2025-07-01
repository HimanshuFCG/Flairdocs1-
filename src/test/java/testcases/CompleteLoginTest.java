package testcases;

import com.microsoft.playwright.*;
import org.testng.annotations.Test;
import org.testng.annotations.AfterMethod;
import org.testng.Assert;
import java.nio.file.Paths;
import com.microsoft.playwright.options.WaitUntilState;
import com.microsoft.playwright.options.WaitForSelectorState;
import utilities.screenshotUtils.ScreenshotUtils;
import java.io.FileInputStream;
import java.util.Properties;
import config.ConfigReader;
import com.aventstack.extentreports.ExtentTest;
import base.BaseTest;
import org.testng.annotations.AfterSuite;
import java.io.InputStream;

public class CompleteLoginTest extends BaseTest {

    private Browser browser;
    private Page page;

    @Test
    public void completeLoginAndTabSelection() {
        ExtentTest test = extent.createTest("Complete Login And Tab Selection");
        try {
            // Launch Playwright
            Playwright playwright = Playwright.create();
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


              // Click the 'Domain:' element to open the dropdown
              Locator domainDropdown = page.locator("//span[@class='rtbText' and text()='Domain:']");
              domainDropdown.waitFor(new Locator.WaitForOptions().setTimeout(15000).setState(WaitForSelectorState.VISIBLE));
              domainDropdown.click();
              test.info("Clicked 'Domain:' dropdown");
              log.info("Clicked 'Domain:' dropdown");
  
              // Wait for the 'Acquisition' option to appear and select it
              Locator acquisitionOption = page.locator("//span[@class='rtbText' and text()='Acquisition']");
              acquisitionOption.waitFor(new Locator.WaitForOptions().setTimeout(15000).setState(WaitForSelectorState.VISIBLE));
              acquisitionOption.click();
              test.info("Selected 'Acquisition' from domain dropdown");
              log.info("Selected 'Acquisition' from domain dropdown");

            // Find and click project dropdown
            String dropdownArrowSelector = "#ctl00_Main_ProjectSnapShotDetails_ddlProjSnapShotSearchNum_Arrow";
            page.waitForSelector(dropdownArrowSelector);
            page.click(dropdownArrowSelector);
            test.info("Clicked project dropdown");

            // Wait for dropdown to open
            page.waitForTimeout(2000);

            // Select 11th item (Barrel Spring)
            String listItemSelector = "#ctl00_Main_ProjectSnapShotDetails_ddlProjSnapShotSearchNum_listbox li.rcbItem";
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

            // Wait for the 'Assignments' tab to be visible and click it
            String assignmentsTabXPath = "//span[@class='rtsTxt' and text()='Assignments']";
            page.waitForSelector(assignmentsTabXPath);
            page.click(assignmentsTabXPath);
            test.info("Clicked 'Assignments' tab");

            // Wait for 5 seconds before clicking the next tab
            page.waitForTimeout(5000);

            // Wait for the 'Liaison Files' tab to be visible and click it
            String liaisonFilesTabXPath = "//span[@class='rtsTxt' and text()='Liaison Files']";
            page.waitForSelector(liaisonFilesTabXPath);
            page.click(liaisonFilesTabXPath);
            test.info("Clicked 'Liaison Files' tab");

            // Wait for 5 seconds before clicking the next tab
            page.waitForTimeout(5000);

            // Wait for the 'Estimates' tab to be visible and click it
            String estimatesTabXPath = "//span[@class='rtsTxt' and text()='Estimates']";
            page.waitForSelector(estimatesTabXPath);
            page.click(estimatesTabXPath);
            test.info("Clicked 'Estimates' tab");

            // Wait for 5 seconds before clicking the next tab
            page.waitForTimeout(5000);

            // Wait for the 'Contracting' tab to be visible and click it
            String contractingTabXPath = "//span[@class='rtsTxt' and text()='Contracting']";
            page.waitForSelector(contractingTabXPath);
            page.click(contractingTabXPath);
            test.info("Clicked 'Contracting' tab");

            // Wait for 5 seconds before clicking the next tab
            page.waitForTimeout(5000);

            // Wait for the 'Authorization Management' tab to be visible and click it
            String authorizationManagementTabXPath = "//span[@class='rtsTxt' and text()='Authorization Management']";
            page.waitForSelector(authorizationManagementTabXPath);
            page.click(authorizationManagementTabXPath);
            test.info("Clicked 'Authorization Management' tab");

            // Wait for 5 seconds before clicking the next tab
            page.waitForTimeout(5000);

            // Wait for the 'Issue Management' tab to be visible and click it
            String issueManagementTabXPath = "//span[@class='rtsTxt' and text()='Issue Management']";
            page.waitForSelector(issueManagementTabXPath);
            page.click(issueManagementTabXPath);
            test.info("Clicked 'Issue Management' tab");

            // Wait for 5 seconds before clicking the next tab
            page.waitForTimeout(5000);

            // Wait for the 'Financials' tab to be visible and click it
            String financialsTabXPath = "//span[@class='rtsTxt' and text()='Financials']";
            page.waitForSelector(financialsTabXPath);
            page.click(financialsTabXPath);
            test.info("Clicked 'Financials' tab");

            // Wait for 5 seconds before clicking the next tab
            page.waitForTimeout(5000);

            // Wait for the 'Checklist' tab to be visible and click it
            String checklistTabXPath = "//span[@class='rtsTxt' and text()='Checklist']";
            page.waitForSelector(checklistTabXPath);
            page.click(checklistTabXPath);
            test.info("Clicked 'Checklist' tab");

            // Wait for 5 seconds before clicking the next tab
            page.waitForTimeout(5000);

            // Wait for the 'Utility Coordination' tab to be visible and click it
            String utilityCoordinationTabXPath = "//span[@class='rtsTxt' and text()='Utility Coordination']";
            page.waitForSelector(utilityCoordinationTabXPath);
            page.click(utilityCoordinationTabXPath);
            test.info("Clicked 'Utility Coordination' tab");

            // Wait for 5 seconds before clicking the next tab
            page.waitForTimeout(5000);

            // Wait for the 'Railroad Coordination' tab to be visible and click it
            String railroadCoordinationTabXPath = "//span[@class='rtsTxt' and text()='Railroad Coordination']";
            page.waitForSelector(railroadCoordinationTabXPath);
            page.click(railroadCoordinationTabXPath);
            test.info("Clicked 'Railroad Coordination' tab");

            // Wait to see selection
            page.waitForTimeout(3000);

            // Take a screenshot using the reusable utility
            String screenshotPath = ScreenshotUtils.takeScreenshot(page, "CompleteLoginTest");
            test.info("Screenshot saved at: " + screenshotPath);

            test.pass("Login successful");
        } catch (Exception e) {
            test.fail("Test failed: " + e.getMessage());
            System.out.println("Test failed: " + e.getMessage());
            e.printStackTrace();
            Assert.fail("Test failed: " + e.getMessage());
        }
    }

    @AfterSuite
    public void tearDownSuite() {
        if (extent != null) {
            extent.flush();
        }
    }

    @Test
    public void testConfigResource() {
        InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties");
        System.out.println("Resource found? " + (input != null));
    }
} 