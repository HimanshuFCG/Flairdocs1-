package pages;

import com.aventstack.extentreports.ExtentTest;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.apache.log4j.Logger;

public class AdminPage {
    private final Page page;
    private static final Logger log = Logger.getLogger(AdminPage.class);

    // Centralized locators
    public static final String ADMIN_BUTTON = "//span[@class='rmText rmExpandDown' and text()='Admin']";
    public static final String CLOSE_ICON = "//a[@class='rwCloseButton' and @title='Close']";
    
    // Admin menu items as a 2D array [selector, type, displayName]
    public static final String[][] ADMIN_MENU_ITEMS = {
        {"//a[@title='Application Logs']//span[@class='rmText' and text()='Application Logs']", "popup", "Application Logs"},
        {"//a[@title='Application Roles and Work Group']//span[@class='rmText' and text()='Application Roles and Work Groups']", "navigation", "Application Roles and Work Groups"},
        {"//a[@title='User Management']//span[@class='rmText' and text()='User Management']", "navigation", "User Management"},
        {"//a[@title='Work Group Office Information']//span[@class='rmText' and text()='Work Group Office Information']", "navigation", "Work Group Office Information"},
        {"//a[@title='Contracting Company Details']//span[@class='rmText' and text()='Contracting Company Details']", "navigation", "Contracting Company Details"},
        {"//a[@title='Contracting Price Agreements']//span[@class='rmText' and text()='Contracting Price Agreements']", "navigation", "Contracting Price Agreements"},
        {"//a[@title='Dropdowns']//span[@class='rmText' and text()='Dropdowns']", "navigation", "Dropdowns"},
        {"//a[@title='Configure Distribution Lists']//span[@class='rmText' and text()='Configure Distribution Lists']", "navigation", "Configure Distribution Lists"},
        {"//a[@title='Estimation Cost Factors']//span[@class='rmText' and text()='Estimation Cost Factors']", "navigation", "Estimation Cost Factors"},
        {"//a[@title='Expected Work Duration']//span[@class='rmText' and text()='Expected Work Duration']", "navigation", "Expected Work Duration"},
        {"//a[@title='Delete Projects and Files']//span[@class='rmText' and text()='Delete Project/File']", "navigation", "Delete Projects and Files"},
        {"//a[@title='Move File Panel']//span[@class='rmText' and text()='Move File Panel']", "navigation", "Move File Panel"},
        {"//a[@title='Cannned Report']//span[@class='rmText' and text()='Canned Report']", "navigation", "Canned Report"},
        {"//a[@title='Bulk Upload Documents']//span[@class='rmText' and text()='Bulk Upload Documents']", "navigation", "Bulk Upload Documents"},
        {"//a[@title='Bulk Generate Documents']//span[@class='rmText' and text()='Bulk Generate Documents']", "navigation", "Bulk Generate Documents"},
        {"//a[@title='Document Metadata Attributes']//span[@class='rmText' and text()='Document Metadata Attributes']", "navigation", "Document Metadata Attributes"},
        {"//a[@title='Document Packages']//span[@class='rmText' and text()='Document Packages']", "navigation", "Document Packages"},
        {"//a[@title='Document Type Configuration']//span[@class='rmText' and text()='Document Type Configuration']", "navigation", "Document Type Configuration"},
        {"//a[@title='Template and Clause Maintenance']//span[@class='rmText' and text()='Template and Clause Maintenance']", "navigation", "Template and Clause Maintenance"},
        {"//a[@title='Civil Certification / Board Approval']//span[@class='rmText' and text()='Civil Certification / Board Approval']", "navigation", "Civil Certification / Board Approval"},
        {"//a[@title='Production Plans']//span[@class='rmText' and text()='Production Plans']", "navigation", "Production Plans"},
        {"//a[@title='QA Data']//span[@class='rmText' and text()='QA Data']", "navigation", "QA Data"},
        {"//a[@title='Help Content']//span[@class='rmText' and text()='Help Content']", "navigation", "Help Content"},
        {"//a[@title='FlairBOT Library']//span[@class='rmText' and text()='FlairBOT Library']", "navigation", "FlairBOT Library"},
        {"//a[@title='Workflow Activity']//span[@class='rmText' and text()='Workflow Activity']", "navigation", "Workflow Activity"},
        {"//a[@title='Notifications Configuration']//span[@class='rmText' and text()='Notifications Configuration']", "navigation", "Notifications Configuration"},
        {"//a[@title='Workflow Designer']//span[@class='rmText' and text()='Workflow Designer']", "navigation", "Workflow Designer"},
        {"//a[@title='Environment Copy']//span[@class='rmText' and text()='Environment Copy']", "navigation", "Environment Copy"},
        {"//a[@title='Application Configuration']//span[@class='rmText' and text()='Application Configuration']", "navigation", "Application Configuration"},
        {"//a[@title='Checklist Configuration']//span[@class='rmText' and text()='Checklist Configuration']", "navigation", "Checklist Configuration"}
    };

    public AdminPage(Page page) {
         this.page = page; }

    public boolean isAdminMenuVisible() {
        return page.isVisible(ADMIN_BUTTON);
    }

    /**
     * Returns the text of the Admin menu button (should be 'Admin').
     */
    public String getAdminMenuTitle() {
        if (page.isVisible(ADMIN_BUTTON)) {
            return page.textContent(ADMIN_BUTTON).trim();
        }
        return null;
    }

    public void clickAdminMenu(ExtentTest test) {
        page.waitForSelector(ADMIN_BUTTON);
        page.click(ADMIN_BUTTON);
        if (test != null) {
            test.info("Clicked Admin button");
        }
    }

    public void clickAdminItem(String itemSelector, ExtentTest test) {
        page.waitForSelector(itemSelector, new Page.WaitForSelectorOptions().setTimeout(20000).setState(WaitForSelectorState.VISIBLE));
        page.click(itemSelector);
        if (test != null) {
            test.info("Clicked admin menu item: " + itemSelector);
        }
    }

    public void closePopup(ExtentTest test) {
        page.waitForTimeout(2000);
        page.waitForSelector(CLOSE_ICON, new Page.WaitForSelectorOptions().setTimeout(10000).setState(WaitForSelectorState.VISIBLE));
        page.click(CLOSE_ICON);
        page.waitForTimeout(1000);
        page.waitForSelector(CLOSE_ICON, new Page.WaitForSelectorOptions().setTimeout(10000).setState(WaitForSelectorState.HIDDEN));
        if (test != null) {
            test.info("Closed popup");
        }
    }

    public void clickAdminAgain(ExtentTest test) {
        page.waitForTimeout(2000);
        page.waitForSelector(ADMIN_BUTTON, new Page.WaitForSelectorOptions().setTimeout(10000).setState(WaitForSelectorState.VISIBLE));
        page.click(ADMIN_BUTTON);
        if (test != null) {
            test.info("Clicked Admin button again to return to menu");
        }
    }

    public void handleAdminItem(String itemSelector, String itemType, ExtentTest test) {
        clickAdminMenu(test);
        clickAdminItem(itemSelector, test);
        if ("popup".equals(itemType)) {
            closePopup(test);
        } else if ("navigation".equals(itemType)) {
            clickAdminAgain(test);
        }
    }
} 