package pages;

import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class CompleteLoginPage {
    private final Page page;

    // Centralized locators
    private static final String USERNAME_INPUT = "#LoginFlairdocs_UserName";
    private static final String PASSWORD_INPUT = "#LoginFlairdocs_Password";
    private static final String LOGIN_BUTTON = "#LoginFlairdocs_LoginButton";
    private static final String DOMAIN_DROPDOWN_XPATH = "//span[@class='rtbText' and text()='Domain:']";
    private static final String PROJECT_DROPDOWN_ARROW = "#ctl00_Main_ProjectSnapShotDetails_ddlProjSnapShotSearchNum_Arrow";
    private static final String PROJECT_LIST_ITEM = "#ctl00_Main_ProjectSnapShotDetails_ddlProjSnapShotSearchNum_listbox li.rcbItem";
    private static final String GO_TO_PROJECT_DETAILS_XPATH = "//input[@id='ctl00_Main_ProjectSnapShotDetails_btnProjeSnapShotOpen']";
    private static final String TAB_XPATH_TEMPLATE = "//span[@class='rtsTxt' and text()='%s']";

    public CompleteLoginPage(Page page) { this.page = page; }

    public void login(String username, String password, ExtentTest test) {
        page.waitForSelector(USERNAME_INPUT);
        page.fill(USERNAME_INPUT, username);
        test.info("Entered username");
        page.fill(PASSWORD_INPUT, password);
        test.info("Entered password");
        page.waitForSelector(LOGIN_BUTTON, new Page.WaitForSelectorOptions().setTimeout(20000).setState(WaitForSelectorState.VISIBLE));
        test.info("Login button is visible");
        page.click(LOGIN_BUTTON, new Page.ClickOptions().setForce(true));
        test.info("Clicked login button");
        page.waitForLoadState();
        test.info("After login - Page title: " + page.title());
        page.waitForTimeout(5000);
        page.waitForSelector(".header-image-container[title='Flairdocs']", new Page.WaitForSelectorOptions().setTimeout(10000));
        test.info("Flairdocs header is visible");
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

    public void goToProjectDetails(ExtentTest test) {
        page.waitForSelector(GO_TO_PROJECT_DETAILS_XPATH);
        page.click(GO_TO_PROJECT_DETAILS_XPATH);
        test.info("Clicked 'Go to Project Details' button");
        page.waitForTimeout(5000);
    }

    public void clickTab(String tabName, ExtentTest test) {
        String tabXPath = String.format(TAB_XPATH_TEMPLATE, tabName);
        page.waitForSelector(tabXPath);
        page.click(tabXPath);
        test.info("Clicked '" + tabName + "' tab");
        page.waitForTimeout(5000);
    }
} 