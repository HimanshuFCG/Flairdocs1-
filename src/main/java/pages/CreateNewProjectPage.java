package pages;

import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class CreateNewProjectPage {
    private final Page page;

    // Centralized locators
    private static final String DOMAIN_DROPDOWN_XPATH = "//span[@class='rtbText' and text()='Domain:']";
    private static final String CREATE_NEW_PROJECT_BUTTON = "#ctl00_Main_ProjectSnapShotDetails_btnCreateNewProject";
    private static final String IFRAME_LOCATOR = "iframe[name='CreateProjectWindow']";

    public CreateNewProjectPage(Page page) {
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


    public void clickCreateNewProject(ExtentTest test) {
        page.waitForSelector(CREATE_NEW_PROJECT_BUTTON);
        page.click(CREATE_NEW_PROJECT_BUTTON);
        test.info("Clicked Create New Project button");
    }

    public void waitForPopup(ExtentTest test) {
        Locator iframeLocator = page.locator(IFRAME_LOCATOR);
        iframeLocator.waitFor(new Locator.WaitForOptions().setTimeout(10000).setState(WaitForSelectorState.VISIBLE));
        test.info("Create New Project popup appeared");
    }
} 