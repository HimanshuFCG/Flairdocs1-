package pages;

import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;

public class ExpandPanelPage {
    private final Page page;

    public ExpandPanelPage(Page page) {
        this.page = page;
    }

    // Centralized locators
    private static final String DOMAIN_DROPDOWN_XPATH = "//span[@class='rtbText' and text()='Domain:']";
    private static final String PROJECT_DROPDOWN_ARROW = "#ctl00_Main_ProjectSnapShotDetails_ddlProjSnapShotSearchNum_Arrow";
    private static final String PROJECT_LIST_ITEM = "#ctl00_Main_ProjectSnapShotDetails_ddlProjSnapShotSearchNum_listbox li.rcbItem";
    private static final String GO_TO_PROJECT_DETAILS_XPATH = "//input[@id='ctl00_Main_ProjectSnapShotDetails_btnProjeSnapShotOpen']";

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
        Locator dropdownArrow = page.locator(PROJECT_DROPDOWN_ARROW);
        dropdownArrow.waitFor(new Locator.WaitForOptions().setTimeout(10000).setState(WaitForSelectorState.ATTACHED));
        dropdownArrow.click();
        test.info("Clicked project dropdown");

        retry(() -> {
            Locator item = page.locator(PROJECT_LIST_ITEM, new Page.LocatorOptions().setHasText(project));
            item.first().waitFor(new Locator.WaitForOptions().setTimeout(10000).setState(WaitForSelectorState.VISIBLE));
            item.first().click();
            test.info("Selected project: " + project);
        }, 3, test, "Selecting project: " + project);
    }

    public void goToProjectDetails(ExtentTest test) {
        Locator goToDetails = page.locator(GO_TO_PROJECT_DETAILS_XPATH);
        goToDetails.waitFor(new Locator.WaitForOptions().setTimeout(10000).setState(WaitForSelectorState.VISIBLE));
        goToDetails.click();
        test.info("Clicked 'Go to Project Details' button");
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    public void expandPanel(ExtentTest test) {
        String expandButtonXpath = "//*[@id='ctl00_Main_DynamicContent1_ibPlus']";
        Locator expandButton = page.locator(expandButtonXpath);
        expandButton.waitFor(new Locator.WaitForOptions().setTimeout(10000).setState(WaitForSelectorState.VISIBLE));
        expandButton.click();
        test.info("Clicked 'Expand' button");

        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.evaluate("window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'})");
        test.info("Scrolled to bottom after expanding");
    }

    public void collapsePanel(ExtentTest test) {
        String collapseButtonXpath = "//*[@id='ctl00_Main_DynamicContent1_ibMinus']";
        Locator collapseButton = page.locator(collapseButtonXpath);
        collapseButton.waitFor(new Locator.WaitForOptions().setTimeout(10000).setState(WaitForSelectorState.VISIBLE));
        page.waitForTimeout(800); 
        collapseButton.click();
        test.info("Clicked 'Collapse' button");

        
        page.waitForTimeout(1200);
        page.evaluate("window.scrollTo({top: document.body.scrollHeight, behavior: 'smooth'})");
        test.info("Scrolled to bottom after collapsing");

    }

    // Generic retry utility
    private void retry(Runnable action, int attempts, ExtentTest test, String actionDescription) {
        for (int i = 0; i < attempts; i++) {
            try {
                action.run();
                return;
            } catch (Exception e) {
                if (i == attempts - 1) {
                    test.fail("Failed after retries: " + actionDescription);
                    throw e;
                }
                test.warning("Retrying (" + (i + 1) + "/" + attempts + ") for: " + actionDescription);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }
    }
}
