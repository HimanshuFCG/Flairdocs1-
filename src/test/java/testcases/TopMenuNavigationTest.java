package testcases;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import org.testng.annotations.Test;
import org.apache.log4j.Logger;
import pages.TopMenuPage;
import config.ConfigReader;
import com.microsoft.playwright.Page;

public class TopMenuNavigationTest extends BaseTest {
    private static final Logger log = Logger.getLogger(TopMenuNavigationTest.class);

    @Test
    public void clickTopMenuItems() {
        ExtentTest test = extent.createTest("Top Menu Navigation Test");
        try {
            login();
            test.info("Logged in using BaseTest.login()");
            log.info("Logged in using BaseTest.login()");

            TopMenuPage topMenu = new TopMenuPage(page);

            // Click 'Search'
            topMenu.clickSearch();
            test.info("Clicked 'Search' menu item");
            log.info("Clicked 'Search' menu item");
            page.waitForTimeout(5000);

            // Click 'My Work Queue'
            topMenu.clickMyWorkQueue();
            test.info("Clicked 'My Work Queue' menu item");
            log.info("Clicked 'My Work Queue' menu item");
            page.waitForTimeout(5000);

            // Click 'My Profile'
            topMenu.clickMyProfile();
            test.info("Clicked 'My Profile' menu item");
            log.info("Clicked 'My Profile' menu item");
            page.waitForTimeout(5000);

            // Click 'PM Inquiry' (new window)
            Page pmInquiryPage = page.context().waitForPage(() -> {
                topMenu.clickPMInquiry();
            });
            test.info("Clicked 'PM Inquiry' menu item and new window opened");
            log.info("Clicked 'PM Inquiry' menu item and new window opened");
            int width = Integer.parseInt(ConfigReader.get("viewportWidth", "1581"));
            int height = Integer.parseInt(ConfigReader.get("viewportHeight", "864"));
            pmInquiryPage.setViewportSize(width, height);
            pmInquiryPage.waitForTimeout(2000);
            pmInquiryPage.close(); // Close new window

            // Now just continue with the original page (main tab)
            topMenu.clickNotifications();
            test.info("Clicked 'Notifications' menu item");
            log.info("Clicked 'Notifications' menu item");
            page.waitForTimeout(5000);

            // Click 'Doc Mgmt' (new window)
            Page docMgmtPage = page.context().waitForPage(() -> {
                topMenu.clickDocMgmt();
            });
            test.info("Clicked 'Doc Mgmt' menu item and new window opened");
            log.info("Clicked 'Doc Mgmt' menu item and new window opened");
            docMgmtPage.setViewportSize(width, height);
            docMgmtPage.waitForTimeout(2000);
            docMgmtPage.close();
            page.waitForTimeout(5000);

            // Click 'GIS' (new window)
            Page gisPage = page.context().waitForPage(() -> {
                topMenu.clickGIS();
            });
            test.info("Clicked 'GIS' menu item and new window opened");
            log.info("Clicked 'GIS' menu item and new window opened");
            gisPage.setViewportSize(width, height);
            gisPage.waitForTimeout(2000);
            gisPage.close();
            page.waitForTimeout(5000);

            // Click 'Dashboard' (same window)
            topMenu.clickDashboard();
            test.info("Clicked 'Dashboard' menu item");
            log.info("Clicked 'Dashboard' menu item");
            page.setViewportSize(width, height);
            page.waitForTimeout(2000);

            test.pass("Successfully clicked all top menu items.");
            log.info("Successfully clicked all top menu items.");
        } catch (Exception e) {
            test.fail("Test failed: " + e.getMessage());
            log.error("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 