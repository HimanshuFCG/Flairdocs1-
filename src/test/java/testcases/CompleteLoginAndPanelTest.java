package testcases;

import base.BaseTest;
import config.ConfigReader;

import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page.WaitForSelectorOptions;

import utilities.PanelActions;
import java.nio.file.Paths;

public class CompleteLoginAndPanelTest extends BaseTest {

    @Test
    public void loginAndOpenClosePanels() {
        ExtentTest test = extent.createTest("Complete Login And Panel Test");
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

            // Read viewport size from config.properties
            int width = Integer.parseInt(ConfigReader.get("viewportWidth", "1280"));
            int height = Integer.parseInt(ConfigReader.get("viewportHeight", "800"));
            test.info("Viewport size set to: " + width + "x" + height);

            // Create new page with configurable viewport size
            page = browser.newPage(new Browser.NewPageOptions().setViewportSize(width, height));
            this.page = page; // assign to BaseTest field for helpers
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
            // Wait for the page to load completely before proceeding
            page.waitForLoadState(LoadState.NETWORKIDLE);
            page.waitForTimeout(2000); // Extra wait to ensure all dynamic content is loaded

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

            // Wait for the new page to load completely before interacting with panels
            page.waitForLoadState();
            test.info("Page loaded after 'Go to Project Details' click. New page title: " + page.title());
            page.waitForTimeout(5000);


            String ProjectInformationXPath = "//span[@class='rtsTxt' and text()='Project Information']";
            page.waitForSelector(ProjectInformationXPath);
            page.click(ProjectInformationXPath);
            test.info("Clicked 'Project Information' panel");

                     

          
            String[] panelTitles = {
                "Project Information", "Project Status Log", "Right of Way Maps", "External Agreement","Authorization Summary","Sales Book","Certification","Import","All Project Information Documents"
            };

            PanelActions panelActions = new PanelActions(page);

            for (String panelTitle : panelTitles) {
                try {
                    panelActions.openPanel(panelTitle);
                    // Wait, loader, etc. can be handled inside openPanel
                    page.waitForTimeout(1000);
                    panelActions.closePanel(panelTitle);
                    page.waitForTimeout(500);
                    System.out.println("Successfully opened and closed panel: " + panelTitle);
                } catch (Exception e) {
                    System.out.println("Error handling panel: " + panelTitle + " - " + e.getMessage());
                }
            }
            test.pass("Panels opened and closed successfully.");
        } catch (Exception e) {
            test.fail("Test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (page != null) page.close();
            if (browser != null) browser.close();
            if (playwright != null) playwright.close();
        }
    }

    public void openPanelByXPath(String xpath) {
        Locator panel = page.locator(xpath);
        if (panel.count() == 0) {
            System.out.println("Panel not found for XPath: " + xpath);
            return;
        }
        panel.first().scrollIntoViewIfNeeded();
        panel.first().click(new Locator.ClickOptions().setForce(true));
        // Wait for panel to open (class no longer contains 'panelclosed')
        page.waitForFunction("el => !el.className.includes('panelclosed')", panel.first());
        // Wait for loader to disappear (if any)
        page.waitForSelector(".loading", new WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(20000));
        System.out.println("Panel opened and loader disappeared!");
    }

    public void openPanelByTitle(String panelTitle) {
        if ("Project Information".equals(panelTitle)) {
            Locator collapseBtn = page.locator("#ctl00_Main_DynamicContent1_projinfo_AcqProjectProfile_ColProjectprofile____img");
            collapseBtn.waitFor(new Locator.WaitForOptions().setTimeout(5000));
            collapseBtn.scrollIntoViewIfNeeded();
            collapseBtn.click(new Locator.ClickOptions().setForce(true));
            // Wait for the panel to be closed (class should include 'panelclosed')
            Locator closedPanel = page.locator("#ctl00_Main_DynamicContent1_projinfo_AcqProjectProfile_ColProjectprofile____title");
            closedPanel.waitFor(new Locator.WaitForOptions().setTimeout(10000));
            page.waitForFunction(
                "el => el.className.includes('panelclosed')",
                closedPanel.elementHandle(),
                new Page.WaitForFunctionOptions().setTimeout(10000)
            );
            log.info("Successfully closed panel: " + panelTitle);
            return;
        }
        Locator panel = page.locator("div.collapsible-panel-main-open-close", new Page.LocatorOptions().setHasText(panelTitle));
        if (panel.count() == 0) {
            System.out.println("Panel not found for title: " + panelTitle);
            return;
        }
        panel.first().scrollIntoViewIfNeeded();
        panel.first().click(new Locator.ClickOptions().setForce(true));
        // Wait for panel to open
        page.waitForFunction("el => !el.className.includes('panelclosed')", panel.first());
    }
}
