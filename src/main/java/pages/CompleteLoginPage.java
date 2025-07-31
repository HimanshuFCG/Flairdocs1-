package pages;

import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import utils.WaitHelper; // Added for centralized waits
import org.apache.log4j.Logger;

public class CompleteLoginPage {
    private static final Logger log = Logger.getLogger(CompleteLoginPage.class);
    private final Page page;
    public CompleteLoginPage(Page page) { this.page = page; }

    // Centralized locators

    private static final String DOMAIN_DROPDOWN_XPATH = "//span[@class='rtbText' and text()='Domain:']";
    private static final String PROJECT_DROPDOWN_INPUT = "#ctl00_Main_ProjectSnapShotDetails_ddlProjSnapShotSearchNum_Input";
    private static final String PROJECT_LIST_ITEM = "#ctl00_Main_ProjectSnapShotDetails_ddlProjSnapShotSearchNum_listbox li.rcbItem";
    private static final String GO_TO_PROJECT_DETAILS_XPATH = "//input[@id='ctl00_Main_ProjectSnapShotDetails_btnProjeSnapShotOpen']";
    private static final String TAB_XPATH_TEMPLATE = "//span[@class='rtsTxt' and text()='%s']";

   

    public void selectDomain(String domain, ExtentTest test) {
        Locator domainDropdown = WaitHelper.waitForVisibleAndEnabled(page, DOMAIN_DROPDOWN_XPATH, 15000);
        domainDropdown.click();
        test.info("Clicked 'Domain:' dropdown");

        String domainOptionXpath = "//span[@class='rtbText' and text()='" + domain + "']";
        Locator domainOption = WaitHelper.waitForVisibleAndEnabled(page, domainOptionXpath, 15000);
        domainOption.click();
        test.info("Selected '" + domain + "' from domain dropdown");
    }

    public void selectProject(String project, ExtentTest test) {
        try {
            String projectTrimmed = project.trim();
            page.click(PROJECT_DROPDOWN_INPUT);
            test.info("Clicked project dropdown input");
            page.fill(PROJECT_DROPDOWN_INPUT, projectTrimmed);
            test.info("Typed project name into dropdown: '" + projectTrimmed + "'");
    
            // Wait until at least one dropdown item appears (remove static waits)
            Locator dropdownListItems = page.locator(PROJECT_LIST_ITEM);
            dropdownListItems.first().waitFor(new Locator.WaitForOptions().setTimeout(10000));
            int itemCount = dropdownListItems.count();
            log.info("Dropdown item count after typing: " + itemCount);
            test.info("Dropdown item count after typing: " + itemCount);
    
            if (itemCount == 0) {
                test.fail("No project items found in dropdown for: " + projectTrimmed);
                throw new RuntimeException("No project items found for: " + projectTrimmed);
            }
    
            // Find best match: exact or fallback to partial match
            Locator exactItem = page.locator(PROJECT_LIST_ITEM + ":text-is('" + projectTrimmed + "')");
            Locator toClick = (exactItem.count() > 0) ? exactItem.first() : dropdownListItems.first();
    
            // Logging all items for debug
            for (int i = 0; i < itemCount; i++) {
                String itemText = dropdownListItems.nth(i).innerText().trim();
                log.info("Dropdown item " + i + ": " + itemText);
                test.info("Dropdown item " + i + ": " + itemText);
            }
    
            if (!toClick.isEnabled()) {
                test.fail("Project item is not enabled: " + projectTrimmed);
                throw new RuntimeException("Project item is not enabled: " + projectTrimmed);
            }
    
            toClick.hover();
            test.info("Hovered over project item: " + projectTrimmed);
            log.info("Hovered over project item: " + projectTrimmed);
            toClick.click();
            test.info("Clicked project item: " + projectTrimmed);
            log.info("Clicked project item: " + projectTrimmed);
    
        } catch (Exception e) {
            test.fail("Failed to select project: " + e.getMessage());
            throw new RuntimeException("Failed to select project: " + e.getMessage());
        }
    }
    

    public void goToProjectDetails(ExtentTest test) {
        Locator goToDetails = WaitHelper.waitForVisibleAndEnabled(page, GO_TO_PROJECT_DETAILS_XPATH, 10000);
        goToDetails.click();
        test.info("Clicked 'Go to Project Details' button");
        WaitHelper.waitForNetworkIdle(page, 20000);
    }

    public void clickTab(String tabName, ExtentTest test) {
        String tabXPath = String.format(TAB_XPATH_TEMPLATE, tabName);
        Locator tab = WaitHelper.waitForVisibleAndEnabled(page, tabXPath, 15000);
        tab.click();
        test.info("Clicked '" + tabName + "' tab");
        WaitHelper.waitForNetworkIdle(page, 10000);
    }
} 