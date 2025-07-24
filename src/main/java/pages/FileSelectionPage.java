package pages;

import com.microsoft.playwright.Page;
import com.aventstack.extentreports.ExtentTest;
import org.apache.log4j.Logger;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.LoadState;


public class FileSelectionPage {
    private static final Logger log = Logger.getLogger(FileSelectionPage.class);
    private Page page;

    // TODO: Update this selector to match your actual file table row locator
    private static final String FILE_ROW_SELECTOR = "//tr[contains(@id, 'ctl00_Main_FileSnapShot1_GridSnapShotTracts_ctl00__0')]/td[text()='001']";

    public FileSelectionPage(Page page) {
        this.page = page;
    }

    private static final String DOMAIN_DROPDOWN_XPATH = "//span[@class='rtbText' and text()='Domain:']";
    private static final String PROJECT_DROPDOWN_INPUT = "#ctl00_Main_ProjectSnapShotDetails_ddlProjSnapShotSearchNum_Input";
    private static final String PROJECT_LIST_ITEM = "#ctl00_Main_ProjectSnapShotDetails_ddlProjSnapShotSearchNum_listbox li.rcbItem";
    private static final String GO_TO_PROJECT_DETAILS_XPATH = "//input[@id='ctl00_Main_ProjectSnapShotDetails_btnProjeSnapShotOpen']";
   


     public void selectDomain2(String domain2, ExtentTest test) {
        Locator domainDropdown = page.locator(DOMAIN_DROPDOWN_XPATH);
        domainDropdown.waitFor(new Locator.WaitForOptions().setTimeout(15000).setState(WaitForSelectorState.VISIBLE));
        domainDropdown.click();
        test.info("Clicked 'Domain:' dropdown");

        String domainOptionXpath = "//span[@class='rtbText' and text()='" + domain2 + "']";
        Locator domainOption = page.locator(domainOptionXpath);
        domainOption.waitFor(new Locator.WaitForOptions().setTimeout(15000).setState(WaitForSelectorState.VISIBLE));
        domainOption.click();
        test.info("Selected '" + domain2 + "' from domain dropdown");
    }

    public void selectProject2(String project2, ExtentTest test) {
        // Try clicking the input first, fallback to arrow if needed
        try {
            page.click(PROJECT_DROPDOWN_INPUT);
            page.click(PROJECT_DROPDOWN_INPUT);
            test.info("Clicked project dropdown input");
        } catch (Exception e) {
            test.warning("Failed to click input, trying arrow: " + e.getMessage());
            page.click(PROJECT_DROPDOWN_INPUT);
            test.info("Clicked project dropdown arrow");
        }
        
        // Wait for the dropdown items to be visible
        page.waitForSelector(PROJECT_LIST_ITEM, new Page.WaitForSelectorOptions().setTimeout(60000).setState(WaitForSelectorState.VISIBLE));
        Locator item = page.locator(PROJECT_LIST_ITEM, new Page.LocatorOptions().setHasText(project2));
        int count = item.count();
        if (count == 0) {
            test.fail("No project found with name: " + project2);
            throw new RuntimeException("No project found with name: " + project2);
        } else if (count > 1) {
            test.warning("Multiple projects found with name: " + project2 + ". Clicking the first one.");
        }
        item.waitFor(new Locator.WaitForOptions().setTimeout(5000).setState(WaitForSelectorState.VISIBLE));
        page.waitForTimeout(500);
        item.first().click();
        test.info("Selected project: " + project2);
        page.waitForTimeout(5000);
    }
    public void clickFirstFileInTable(ExtentTest test) {
        try {
            page.waitForSelector(FILE_ROW_SELECTOR);
            page.locator(FILE_ROW_SELECTOR).first().click();
            test.info("Clicked the first file in the file table.");
            log.info("Clicked the first file in the file table.");
        } catch (Exception e) {
            test.fail("Failed to click the first file: " + e.getMessage());
            log.error("Failed to click the first file: " + e.getMessage());
            throw e;
        }
    }

    // Optional: Click a tab by its visible text after file selection
    public void clickTab(String tabName, ExtentTest test) {
        String tabSelector = "//a[normalize-space(text())='" + tabName + "']";
        try {
            page.waitForSelector(tabSelector);
            page.click(tabSelector);
            test.info("Clicked tab: " + tabName);
            log.info("Clicked tab: " + tabName);
        } catch (Exception e) {
            test.fail("Failed to click tab '" + tabName + "': " + e.getMessage());
            log.error("Failed to click tab '" + tabName + "': " + e.getMessage());
            throw e;
        }
    }

    public void clickTabByName(String tabName, ExtentTest test) {
        String tabXpath = "//a[.//span[@class='rtsTxt' and normalize-space(text())='" + tabName + "']]";
        try {
            Locator tab = page.locator(tabXpath);
            tab.waitFor(new Locator.WaitForOptions().setTimeout(30000).setState(WaitForSelectorState.VISIBLE));
            tab.click();
            test.info("Clicked tab: " + tabName);
            log.info("Clicked tab: " + tabName);
        } catch (Exception e) {
            test.fail("Failed to click tab '" + tabName + "': " + e.getMessage());
            log.error("Failed to click tab '" + tabName + "': " + e.getMessage());
            throw e;
        }
    }

    public void goToProjectDetails(ExtentTest test) {
        page.waitForSelector(GO_TO_PROJECT_DETAILS_XPATH);
        page.click(GO_TO_PROJECT_DETAILS_XPATH);
        test.info("Clicked 'Go to Project Details' button");
        log.info("Clicked 'Go to Project Details' button");
        
        // Wait for page to load completely after navigation
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(3000); // Additional wait for UI to stabilize
        
        // Wait for the tab container to be visible in project details context
        try {
            page.waitForSelector("//span[@class='rtsTxt']", new Page.WaitForSelectorOptions().setTimeout(15000).setState(WaitForSelectorState.VISIBLE));
            test.info("Project details page loaded and tabs are visible");
            log.info("Project details page loaded and tabs are visible");
        } catch (Exception e) {
            test.warning("Tab container not immediately visible after project details navigation: " + e.getMessage());
            log.warn("Tab container not immediately visible after project details navigation: " + e.getMessage());
        }
    }
} 