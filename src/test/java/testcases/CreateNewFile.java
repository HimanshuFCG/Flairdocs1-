package testcases;
import org.testng.annotations.Test;
import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import config.ConfigReader;
import com.microsoft.playwright.options.WaitUntilState;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.LoadState;
import base.BaseTest;
import com.microsoft.playwright.Locator;
import org.testng.annotations.AfterSuite;
import org.apache.log4j.Logger;

public class CreateNewFile extends BaseTest {
    private static final Logger log = Logger.getLogger(CreateNewFile.class);

    @Test
    public void loginAndOpenClosePanels() {
        ExtentTest test = extent.createTest("Create New File");
        Playwright playwright = null;
        Browser browser = null;
        Page page = null;
        try {
            // --- Playwright setup and login ---
            playwright = Playwright.create();
            log.info("Playwright launched");
            test.info("Playwright launched");
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(1000));
            log.info("Browser launched");
            test.info("Browser launched");

            int width = Integer.parseInt(ConfigReader.get("viewportWidth", "1280"));
            int height = Integer.parseInt(ConfigReader.get("viewportHeight", "800"));

            log.info("Viewport size set to: " + width + "x" + height);
            test.info("Viewport size set to: " + width + "x" + height);

            page = browser.newPage(new Browser.NewPageOptions().setViewportSize(width, height));
            log.info("New page created with viewport size");
            test.info("New page created with viewport size");

            String baseUrl = ConfigReader.get("baseUrl");
            page.navigate(baseUrl, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED).setTimeout(60000));
            log.info("Navigated to login page: " + page.title());
            test.info("Navigated to login page: " + page.title());

            page.waitForSelector("#LoginFlairdocs_UserName");
            log.info("Login form is visible");
            test.info("Login form is visible");
            String username = ConfigReader.get("username");
            page.fill("#LoginFlairdocs_UserName", username);
            log.info("Entered username");
            test.info("Entered username");

            String password = ConfigReader.get("password");
            page.fill("#LoginFlairdocs_Password", password);
            log.info("Entered password");
            test.info("Entered password");

            page.waitForSelector("#LoginFlairdocs_LoginButton", new Page.WaitForSelectorOptions().setTimeout(20000).setState(WaitForSelectorState.VISIBLE));
            log.info("Login button is visible");
            test.info("Login button is visible");
            page.click("#LoginFlairdocs_LoginButton", new Page.ClickOptions().setForce(true));
            log.info("Clicked login button");
            test.info("Clicked login button");
            page.waitForLoadState();
            
            log.info("After login - Page title: " + page.title());
            test.info("After login - Page title: " + page.title());
            page.waitForTimeout(5000);
            page.waitForSelector(".header-image-container[title='Flairdocs']", new Page.WaitForSelectorOptions().setTimeout(10000));
            log.info("Flairdocs header is visible");
            test.info("Flairdocs header is visible");
            page.waitForLoadState(LoadState.NETWORKIDLE);
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

            // --- Project selection ---
            String dropdownArrowSelector = "#ctl00_Main_ProjectSnapShotDetails_ddlProjSnapShotSearchNum_Arrow";
            page.waitForSelector(dropdownArrowSelector);
            page.click(dropdownArrowSelector);
            log.info("Clicked project dropdown");
            test.info("Clicked project dropdown");
            page.waitForTimeout(2000);
            String listItemSelector = "#ctl00_Main_ProjectSnapShotDetails_ddlProjSnapShotSearchNum_listbox li.rcbItem";
           
            Locator item = page.locator(listItemSelector, new Page.LocatorOptions().setHasText("Barrel Spring"));
            item.waitFor(new Locator.WaitForOptions().setTimeout(5000).setState(WaitForSelectorState.VISIBLE));
            page.waitForTimeout(500);
            item.click();
            log.info("Selected Barrel Spring project (by visible text, with hover)");
            test.info("Selected Barrel Spring project (by visible text, with hover)");
            page.waitForTimeout(5000);

            // --- Click 'Create New File' button ---
            String createNewFileButton = "#ctl00_Main_FileSnapShot1_BtnCreateProperty";
            page.waitForSelector(createNewFileButton);
            page.click(createNewFileButton);
            log.info("Clicked Create New File button");
            test.info("Clicked Create New File button");

            // --- Wait for iframe to be attached ---
            Locator iframeLocator = page.locator("iframe[name='CreateFilewindow']");
            iframeLocator.waitFor(new Locator.WaitForOptions().setTimeout(10000));
            // --- Get frame handle ---
            Frame frame = iframeLocator.elementHandle().contentFrame();
            if (frame == null) {
                throw new RuntimeException("Iframe 'CreateFilewindow' not found or not loaded!");
            }
            log.info("Iframe found and accessed");
            test.info("Iframe found and accessed");

            // --- Fill fields inside iframe ---
            frame.fill("#txtPropertyNumber", "TEST_ROWID");
            log.info("Filled ROW ID");
            test.info("Filled ROW ID");
            frame.fill("#txtErowId", "TEST_EROWID");
            log.info("Filled EROW ID");
            test.info("Filled EROW ID");
            frame.click("#btnCreateProperty");
            log.info("Clicked 'Save & Close' button");
            test.info("Clicked 'Save & Close' button");
            page.waitForTimeout(10000);
            
            // Wait for the table to be present and visible
            String tableSelector = "#ctl00_Main_FileSnapShot1_GridSnapShotTracts_ctl00";
            page.waitForSelector(tableSelector, new Page.WaitForSelectorOptions().setTimeout(10000).setState(WaitForSelectorState.VISIBLE));

            // Optionally, wait for a short time to ensure the table is updated
            page.waitForTimeout(2000);

            // Find all rows in the table body
            Locator rows = page.locator(tableSelector + " > tbody > tr");
            boolean found = false;
            String expectedRowId = "TEST_ROWID"; // Change this to the value you actually entered

            for (int i = 0; i < rows.count(); i++) {
                Locator row = rows.nth(i);
                // Get the third cell (ROW ID)
                String rowId = row.locator("td").nth(2).innerText().trim();
                log.info("Row " + i + " ROW ID: " + rowId);
                test.info("Row " + i + " ROW ID: " + rowId);
                if (rowId.equals(expectedRowId)) {
                    log.info("Found row with ROW ID: " + rowId);
                    test.info("Found row with ROW ID: " + rowId);
                    found = true;
                    break;
                }
            }
            if (!found) {
                log.info("Could not find row with ROW ID: " + expectedRowId);
                test.fail("Could not find row with ROW ID: " + expectedRowId);
            }
            
        } catch (Exception e) {
            log.error("Test failed: " + e.getMessage());
            test.fail("Test failed: " + e.getMessage());
            System.out.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (page != null) page.close();
            if (browser != null) browser.close();
            if (playwright != null) playwright.close();
        }
    }

    @AfterSuite
    public void tearDownSuite() {
        if (extent != null) {
            extent.flush();
        }
    }
}
