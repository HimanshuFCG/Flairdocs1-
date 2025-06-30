package testcases;

import base.BaseTest;
import config.ConfigReader;
import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.apache.log4j.Logger;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExpandAllPanelsInAllTabsTest extends BaseTest {
    private static final Logger log = Logger.getLogger(ExpandAllPanelsInAllTabsTest.class);
    private static final String[] TAB_NAMES = {
        "Project Information",
        "Assignments",
        "Liaison Files",
        "Estimates",
        "Contracting",
        "Authorization Management",
        "Issue Management",
        "Financials",
        "Checklist",
        "Utility Coordination",
        "Railroad Coordination"
    };

    private static final String TAB_XPATH_TEMPLATE = "//span[@class='rtsTxt' and text()='%s']";
    private static final String EXPAND_XPATH = "//*[@id='ctl00_Main_DynamicContent1_ibPlus']";
    private static final String COLLAPSE_XPATH = "//*[@id='ctl00_Main_DynamicContent1_ibMinus']";

    private Playwright playwright;
    private Browser browser;
    private Page page;

    @BeforeSuite
    public void setUpSuite() {
        
    }

    @Test
    public void expandAndCollapseAllPanelsInAllTabs() {
        ExtentTest test = extent.createTest("Expand and Collapse All Panels in All Tabs");
        Path videoPath = null;
        try {
            playwright = Playwright.create();
            test.info("Playwright launched");
            log.info("Playwright launched");

            browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(false)
                    .setSlowMo(1000));
            test.info("Browser launched");
            log.info("Browser launched");

            int width = Integer.parseInt(ConfigReader.get("viewportWidth", "1280"));
            int height = Integer.parseInt(ConfigReader.get("viewportHeight", "800"));
            test.info("Viewport size set to: " + width + "x" + height);
            log.info("Viewport size set to: " + width + "x" + height);

            BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                    .setViewportSize(width, height)
                    .setRecordVideoDir(Paths.get("videos")));
            page = context.newPage();
            test.info("New page created with viewport size");
            log.info("New page created with viewport size");

            String baseUrl = ConfigReader.get("baseUrl");
            page.navigate(baseUrl, new Page.NavigateOptions()
                    .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                    .setTimeout(60000));
            test.info("Navigated to login page: " + page.title());
            log.info("Navigated to login page: " + page.title());

            page.waitForSelector("#LoginFlairdocs_UserName");
            test.info("Login form is visible");
            log.info("Login form is visible");
            String username = ConfigReader.get("username");
            page.fill("#LoginFlairdocs_UserName", username);
            test.info("Entered username");
            log.info("Entered username");
            String password = ConfigReader.get("password");
            page.fill("#LoginFlairdocs_Password", password);
            test.info("Entered password");
            log.info("Entered password");
            page.waitForSelector("#LoginFlairdocs_LoginButton", new Page.WaitForSelectorOptions().setTimeout(60000).setState(WaitForSelectorState.VISIBLE));
            test.info("Login button is visible");
            log.info("Login button is visible");
            page.click("#LoginFlairdocs_LoginButton", new Page.ClickOptions().setForce(true));
            test.info("Clicked login button");
            log.info("Clicked login button");
            //page.waitForTimeout(10000);
            page.waitForLoadState();
            test.info("After login - Page title: " + page.title());
            log.info("After login - Page title: " + page.title());
            page.waitForTimeout(5000);
            page.waitForSelector(".header-image-container[title='Flairdocs']", new Page.WaitForSelectorOptions().setTimeout(10000));
            test.info("Flairdocs header is visible");
            log.info("Flairdocs header is visible");
            page.waitForTimeout(2000);

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

            // Project selection (Barrel Spring)
            String dropdownArrowSelector = "#ctl00_Main_ProjectSnapShotDetails_ddlProjSnapShotSearchNum_Arrow";
            page.waitForSelector(dropdownArrowSelector);
            page.click(dropdownArrowSelector);
            log.info("Clicked project dropdown");
            test.info("Clicked project dropdown");
            page.waitForTimeout(2000);
            String listItemSelector = "#ctl00_Main_ProjectSnapShotDetails_ddlProjSnapShotSearchNum_listbox li.rcbItem";
           
            Locator item = page.locator(listItemSelector, new Page.LocatorOptions().setHasText("Barrel Spring"));
            item.waitFor(new Locator.WaitForOptions().setTimeout(20000).setState(WaitForSelectorState.VISIBLE));
            page.waitForTimeout(5000);
            item.click();
            log.info("Selected Barrel Spring project (by visible text, with hover)");
            test.info("Selected Barrel Spring project (by visible text, with hover)");
            page.waitForTimeout(5000);

            // Iterate through all tabs
            for (String tabName : TAB_NAMES) {
                String tabXPath = String.format(TAB_XPATH_TEMPLATE, tabName);
                page.waitForSelector(tabXPath, new Page.WaitForSelectorOptions().setTimeout(60000).setState(WaitForSelectorState.VISIBLE));
                page.click(tabXPath);
                test.info("Clicked tab: " + tabName);
                log.info("Clicked tab: " + tabName);
                
                // Wait for panels to load
                page.waitForTimeout(3000); // Increased wait time

                // Wait for overlays/loaders to disappear before expanding
                page.waitForSelector(".loading", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(60000));

                // Check for expand buttons
                int expandCount = page.locator(EXPAND_XPATH).count();
                log.info("Expand button count in tab " + tabName + ": " + expandCount);
                if (expandCount == 0) {
                    test.warning("No expand buttons found in tab: " + tabName);
                }
                for (int i = 0; i < expandCount; i++) {
                    Locator expandBtn = page.locator(EXPAND_XPATH).nth(i);
                    if (expandBtn.isVisible() && expandBtn.isEnabled()) {
                        expandBtn.click();
                        test.info("Expanded panel " + (i + 1) + " in tab: " + tabName);
                        log.info("Expanded panel " + (i + 1) + " in tab: " + tabName);
                        page.waitForTimeout(5000);
                    } else {
                        test.fail("Expand button " + (i + 1) + " not visible or not enabled in tab: " + tabName);
                        log.info("Expand button " + (i + 1) + " not visible or not enabled in tab: " + tabName);
                    }
                }

                
                page.waitForTimeout(2000);

                // Wait for overlays/loaders to disappear before collapsing
                page.waitForSelector(".loading", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(60000));

                int collapseCount = page.locator(COLLAPSE_XPATH).count();
                for (int i = 0; i < collapseCount; i++) {
                    Locator collapseBtn = page.locator(COLLAPSE_XPATH).nth(i);
                    if (collapseBtn.isVisible() && collapseBtn.isEnabled()) {
                        collapseBtn.click();
                        test.info("Collapsed panel " + (i + 1) + " in tab: " + tabName);
                        log.info("Collapsed panel " + (i + 1) + " in tab: " + tabName);
                        page.waitForTimeout(1000);
                    } else {
                        test.fail("Collapse button " + (i + 1) + " not visible or not enabled in tab: " + tabName);
                        log.info("Collapse button " + (i + 1) + " not visible or not enabled in tab: " + tabName);
                    }
                }

                
                page.waitForTimeout(2000);
            }
            test.pass("Successfully expanded and collapsed all panels in all tabs.");
            log.info("Successfully expanded and collapsed all panels in all tabs.");

        } catch (Exception e) {
            if (extent != null) test.fail("Test failed: " + e.getMessage());
            System.out.println("Test failed: " + e.getMessage());
            log.error("Test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (page != null) {
                // Get the video path before closing the page
                if (page.video() != null) {
                    videoPath = page.video().path();
                }
                page.close();
            }
            if (browser != null) browser.close();
            if (playwright != null) playwright.close();

            // Attach video to report after closing page/browser
            if (videoPath != null && test != null) {
                String driveLink = "https://drive.google.com/file/d/1b2SA4jnelbM7o4I0wg7XekpvA2xQImfZ/view?usp=sharing";
                test.info("Test video: <a href='" + driveLink + "' target='_blank'>Watch Video on Google Drive</a>");
            }
        }
    }

    @AfterSuite
    public void tearDownSuite() {
        if (extent != null) {
            extent.flush();
        }
    }
} 