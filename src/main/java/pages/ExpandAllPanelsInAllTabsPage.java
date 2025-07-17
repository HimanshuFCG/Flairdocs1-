package pages;

import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.apache.log4j.Logger;


public class ExpandAllPanelsInAllTabsPage {
    private static final Logger log = Logger.getLogger(ExpandAllPanelsInAllTabsPage.class);
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
    private static final String DOMAIN_DROPDOWN_XPATH = "//span[@class='rtbText' and text()='Domain:']";
    private static final String PROJECT_DROPDOWN_ARROW = "#ctl00_Main_ProjectSnapShotDetails_ddlProjSnapShotSearchNum_Arrow";
    private static final String PROJECT_LIST_ITEM = "#ctl00_Main_ProjectSnapShotDetails_ddlProjSnapShotSearchNum_listbox li.rcbItem";
    private static final String GO_TO_PROJECT_DETAILS_XPATH = "//input[@id='ctl00_Main_ProjectSnapShotDetails_btnProjeSnapShotOpen']";

    private final Page page;

    public ExpandAllPanelsInAllTabsPage(Page page) {
        this.page = page;
    }

    public void selectDomain(String domain, ExtentTest test) {
        Locator domainDropdown = page.locator(DOMAIN_DROPDOWN_XPATH);
        domainDropdown.waitFor(new Locator.WaitForOptions().setTimeout(15000).setState(WaitForSelectorState.VISIBLE));
        domainDropdown.click();
        test.info("Clicked 'Domain:' dropdown");
        log.info("Clicked 'Domain:' dropdown");

        String domainOptionXpath = "//span[@class='rtbText' and text()='" + domain + "']";
        Locator domainOption = page.locator(domainOptionXpath);
        domainOption.waitFor(new Locator.WaitForOptions().setTimeout(15000).setState(WaitForSelectorState.VISIBLE));
        domainOption.click();
        test.info("Selected '" + domain + "' from domain dropdown");
        log.info("Selected '" + domain + "' from domain dropdown");
    }

    public void selectProject(String project, ExtentTest test) {
        page.waitForSelector(PROJECT_DROPDOWN_ARROW);
        page.click(PROJECT_DROPDOWN_ARROW);
        test.info("Clicked project dropdown");
        log.info("Clicked project dropdown");
        page.waitForTimeout(2000);
        page.waitForSelector(PROJECT_LIST_ITEM, new Page.WaitForSelectorOptions().setTimeout(60000).setState(WaitForSelectorState.VISIBLE));
        Locator item = page.locator(PROJECT_LIST_ITEM, new Page.LocatorOptions().setHasText(project));
        int count = item.count();
        if (count == 0) {
            test.fail("No project found with name: " + project);
            log.error("No project found with name: " + project);
            throw new RuntimeException("No project found with name: " + project);
        } else if (count > 1) {
            test.warning("Multiple projects found with name: " + project + ". Clicking the first one.");
            log.warn("Multiple projects found with name: " + project + ". Clicking the first one.");
        }
        item.waitFor(new Locator.WaitForOptions().setTimeout(5000).setState(WaitForSelectorState.VISIBLE));
        page.waitForTimeout(500);
        item.first().click();
        test.info("Selected project: " + project);
        log.info("Selected project: " + project);
        page.waitForTimeout(5000);
    }

    public void goToProjectDetails(ExtentTest test) {
        page.waitForSelector(GO_TO_PROJECT_DETAILS_XPATH);
        page.click(GO_TO_PROJECT_DETAILS_XPATH);
        test.info("Clicked 'Go to Project Details' button");
        log.info("Clicked 'Go to Project Details' button");
        page.waitForTimeout(5000);
    }

    public void expandAndCollapseAllPanelsInAllTabs(ExtentTest test) {
        for (String tabName : TAB_NAMES) {
            String tabXPath = String.format(TAB_XPATH_TEMPLATE, tabName);
            page.waitForSelector(tabXPath, new Page.WaitForSelectorOptions().setTimeout(60000).setState(WaitForSelectorState.VISIBLE));
            page.click(tabXPath);
            test.info("Clicked tab: " + tabName);
            log.info("Clicked tab: " + tabName);
            page.waitForTimeout(3000);
            page.waitForSelector(".loading", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(60000));
            int expandCount = page.locator(EXPAND_XPATH).count();
            test.info("Expand button count in tab " + tabName + ": " + expandCount);
            log.info("Expand button count in tab " + tabName + ": " + expandCount);
            for (int i = 0; i < expandCount; i++) {
                Locator expandBtn = page.locator(EXPAND_XPATH).nth(i);
                if (expandBtn.isVisible() && expandBtn.isEnabled()) {
                    expandBtn.click();
                    test.info("Expanded panel " + (i + 1) + " in tab: " + tabName);
                    log.info("Expanded panel " + (i + 1) + " in tab: " + tabName);
                    page.waitForTimeout(5000);
                } else {
                    test.info("Expand button " + (i + 1) + " not visible or not enabled in tab: " + tabName);
                    log.info("Expand button " + (i + 1) + " not visible or not enabled in tab: " + tabName);
                }
            }
            page.waitForTimeout(2000);
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
                    test.info("Collapse button " + (i + 1) + " not visible or not enabled in tab: " + tabName);
                    log.info("Collapse button " + (i + 1) + " not visible or not enabled in tab: " + tabName);
                }
            }
            page.waitForTimeout(2000);
        }
        test.info("Successfully expanded and collapsed all panels in all tabs.");
        log.info("Successfully expanded and collapsed all panels in all tabs.");
    }
} 