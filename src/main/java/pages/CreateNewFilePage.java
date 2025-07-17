package pages;

import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class CreateNewFilePage {
    private final Page page;

    // Centralized locators
    private static final String DOMAIN_DROPDOWN_XPATH = "//span[@class='rtbText' and text()='Domain:']";
    private static final String PROJECT_DROPDOWN_ARROW = "#ctl00_Main_ProjectSnapShotDetails_ddlProjSnapShotSearchNum_Arrow";
    private static final String PROJECT_LIST_ITEM = "#ctl00_Main_ProjectSnapShotDetails_ddlProjSnapShotSearchNum_listbox li.rcbItem";
    private static final String CREATE_NEW_FILE_BUTTON = "#ctl00_Main_FileSnapShot1_BtnCreateProperty";
    private static final String IFRAME_LOCATOR = "iframe[name='CreateFilewindow']";
    private static final String ROW_ID_INPUT = "#txtPropertyNumber";
    private static final String EROW_ID_INPUT = "#txtErowId";
    private static final String SAVE_AND_CLOSE_BUTTON = "#btnCreateProperty";
    private static final String TABLE_SELECTOR = "#ctl00_Main_FileSnapShot1_GridSnapShotTracts_ctl00";

    public CreateNewFilePage(Page page) {
        this.page = page;
    }

    public void selectDomain(String domain, ExtentTest test) {
        Locator domainDropdown = page.locator(DOMAIN_DROPDOWN_XPATH);
        domainDropdown.waitFor(new Locator.WaitForOptions().setTimeout(15000).setState(WaitForSelectorState.VISIBLE));
        domainDropdown.click();
        test.info("Clicked 'Domain:' dropdown");

        String domainOptionXpath = "//span[@class='rtbText' and text()='" + domain + "']";
        Locator domainOption = page.locator(domainOptionXpath);
        domainOption.waitFor(new Locator.WaitForOptions().setTimeout(15000).setState(WaitForSelectorState.VISIBLE));
        domainOption.click();
        test.info("Selected '" + domain + "' from domain dropdown");
    }

    public void selectProject(String project, ExtentTest test) {
        page.click(PROJECT_DROPDOWN_ARROW);
        // Wait for the dropdown items to be visible
        page.waitForSelector(PROJECT_LIST_ITEM, new Page.WaitForSelectorOptions().setTimeout(60000).setState(WaitForSelectorState.VISIBLE));
        Locator item = page.locator(PROJECT_LIST_ITEM, new Page.LocatorOptions().setHasText(project));
        int count = item.count();
        if (count == 0) {
            test.fail("No project found with name: " + project);
            throw new RuntimeException("No project found with name: " + project);
        } else if (count > 1) {
            test.warning("Multiple projects found with name: " + project + ". Clicking the first one.");
        }
        item.waitFor(new Locator.WaitForOptions().setTimeout(5000).setState(WaitForSelectorState.VISIBLE));
        page.waitForTimeout(500);
        item.first().click();
        test.info("Selected project: " + project);
        page.waitForTimeout(5000);
    }

    public void clickCreateNewFile(ExtentTest test) {
        page.waitForSelector(CREATE_NEW_FILE_BUTTON);
        page.click(CREATE_NEW_FILE_BUTTON);
        test.info("Clicked Create New File button");
    }

    public Frame switchToCreateFileIframe(ExtentTest test) {
        Locator iframeLocator = page.locator(IFRAME_LOCATOR);
        iframeLocator.waitFor(new Locator.WaitForOptions().setTimeout(30000));
        Frame frame = iframeLocator.elementHandle().contentFrame();
        if (frame == null) {
            throw new RuntimeException("Iframe 'CreateFilewindow' not found or not loaded!");
        }
        test.info("Iframe found and accessed");
        return frame;
    }

    public void fillCreateFileForm(Frame frame, String rowId, String erowId, ExtentTest test) {
        frame.fill(ROW_ID_INPUT, rowId);
        test.info("Filled ROW ID: " + rowId);
        frame.fill(EROW_ID_INPUT, erowId);
        test.info("Filled EROW ID: " + erowId);
        frame.click(SAVE_AND_CLOSE_BUTTON);
        test.info("Clicked 'Save & Close' button");
    }

    public boolean isRowPresent(String expectedRowId, ExtentTest test) {
        page.waitForSelector(TABLE_SELECTOR, new Page.WaitForSelectorOptions().setTimeout(10000).setState(WaitForSelectorState.VISIBLE));
        page.waitForTimeout(2000);
        Locator rows = page.locator(TABLE_SELECTOR + " > tbody > tr");
        for (int i = 0; i < rows.count(); i++) {
            Locator row = rows.nth(i);
            String rowId = row.locator("td").nth(2).innerText().trim();
            test.info("Row " + i + " ROW ID: " + rowId);
            if (rowId.equals(expectedRowId)) {
                test.info("Found row with ROW ID: " + rowId);
                return true;
            }
        }
        test.info("Could not find row with ROW ID: " + expectedRowId);
        return false;
    }
} 