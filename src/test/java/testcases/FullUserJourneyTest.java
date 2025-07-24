package testcases;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.nio.file.Paths;
import com.microsoft.playwright.Page;
import org.apache.log4j.Logger;
import pages.AdminPage;
import pages.ExpandPanelPage;
import pages.CreateNewFilePage;
import com.microsoft.playwright.Frame;
import config.ConfigReader;
import com.aventstack.extentreports.ExtentTest;
import utilities.PanelActions;
import pages.CompleteLoginAndPanelPage;
import pages.CompleteLoginPage;
import pages.TopMenuPage;
import pages.ExpandAllPanelsInAllTabsPage;
import com.microsoft.playwright.Page;   
import pages.FileSelectionPage;
import pages.PanelExpandCollapsePage;

public class FullUserJourneyTest extends BaseTest {
    private static final Logger log = Logger.getLogger(FullUserJourneyTest.class);

    // --- Admin menu navigation ---
    @Test(priority = 1)
    public void adminMenuNavigation() {
        // Initialize ExtentTest for this test method
        ExtentTest extentTest = getExtentTest();
        if (extentTest == null) {
            log.error("Failed to initialize ExtentTest for adminMenuNavigation");
            Assert.fail("ExtentTest initialization failed");
        }
        
        try {
            log.info("Starting admin menu navigation test");
            safeExtentLog("Starting admin menu navigation test");
            
            AdminPage adminPage = new AdminPage(page);
            log.info("AdminPage initialized");
            
            // Navigate through all admin menu items
            String[][] adminItems = {
                {"//a[@title='Application Logs']//span[@class='rmText' and text()='Application Logs']", "popup"},
                {"//a[@title='Application Roles and Work Group']//span[@class='rmText' and text()='Application Roles and Work Groups']", "navigation"},
                {"//a[@title='User Management']//span[@class='rmText' and text()='User Management']", "navigation"},
                {"//a[@title='Work Group Office Information']//span[@class='rmText' and text()='Work Group Office Information']", "navigation"},
                {"//a[@title='Contracting Company Details']//span[@class='rmText' and text()='Contracting Company Details']", "navigation"},
                {"//a[@title='Contracting Price Agreements']//span[@class='rmText' and text()='Contracting Price Agreements']", "navigation"},
                {"//a[@title='Dropdowns']//span[@class='rmText' and text()='Dropdowns']", "navigation"},
                {"//a[@title='Configure Distribution Lists']//span[@class='rmText' and text()='Configure Distribution Lists']", "navigation"},
                {"//a[@title='Estimation Cost Factors']//span[@class='rmText' and text()='Estimation Cost Factors']", "navigation"},
                {"//a[@title='Expected Work Duration']//span[@class='rmText' and text()='Expected Work Duration']", "navigation"},
                {"//a[@title='Delete Projects and Files']//span[@class='rmText' and text()='Delete Project/File']", "navigation"},
                {"//a[@title='Move File Panel']//span[@class='rmText' and text()='Move File Panel']", "navigation"},
                {"//a[@title='Cannned Report']//span[@class='rmText' and text()='Canned Report']", "navigation"},
                {"//a[@title='Bulk Upload Documents']//span[@class='rmText' and text()='Bulk Upload Documents']", "navigation"},
                {"//a[@title='Bulk Generate Documents']//span[@class='rmText' and text()='Bulk Generate Documents']", "navigation"},
                {"//a[@title='Document Metadata Attributes']//span[@class='rmText' and text()='Document Metadata Attributes']", "navigation"},
                {"//a[@title='Document Packages']//span[@class='rmText' and text()='Document Packages']", "navigation"},
                {"//a[@title='Document Type Configuration']//span[@class='rmText' and text()='Document Type Configuration']", "navigation"},
                {"//a[@title='Template and Clause Maintenance']//span[@class='rmText' and text()='Template and Clause Maintenance']", "navigation"},
                {"//a[@title='Civil Certification / Board Approval']//span[@class='rmText' and text()='Civil Certification / Board Approval']", "navigation"},
                {"//a[@title='Production Plans']//span[@class='rmText' and text()='Production Plans']", "navigation"},
                {"//a[@title='QA Data']//span[@class='rmText' and text()='QA Data']", "navigation"},
                {"//a[@title='Help Content']//span[@class='rmText' and text()='Help Content']", "navigation"},
                {"//a[@title='FlairBOT Library']//span[@class='rmText' and text()='FlairBOT Library']", "navigation"},
                {"//a[@title='Workflow Activity']//span[@class='rmText' and text()='Workflow Activity']", "navigation"},
                {"//a[@title='Notifications Configuration']//span[@class='rmText' and text()='Notifications Configuration']", "navigation"},
                {"//a[@title='Workflow Designer']//span[@class='rmText' and text()='Workflow Designer']", "navigation"},
                {"//a[@title='Environment Copy']//span[@class='rmText' and text()='Environment Copy']", "navigation"},
                {"//a[@title='Application Configuration']//span[@class='rmText' and text()='Application Configuration']", "navigation"},
                {"//a[@title='Checklist Configuration']//span[@class='rmText' and text()='Checklist Configuration']", "navigation"}
            };
            // Get ExtentTest once to avoid multiple lookups
            ExtentTest testInstance = getExtentTest();
            
            for (int i = 0; i < adminItems.length; i++) {
                String itemSelector = adminItems[i][0];
                String itemType = adminItems[i][1];
                String itemName = itemSelector.substring("//a[@title='".length(), itemSelector.indexOf("']"));
                
                try {
                    log.info("Processing admin item: " + itemName);
                    
                    // Log the start of processing this item
                    safeExtentLog("Processing admin item: " + itemName);
                    
                    // Execute the admin item action
                    adminPage.handleAdminItem(itemSelector, itemType, testInstance);
                    
                    // Log successful processing
                    safeExtentLog("✓ Successfully processed: " + itemName);
                    log.info("Successfully processed: " + itemName);
                    
                    // Small delay between items to avoid overwhelming the UI
                    page.waitForTimeout(500);
                    
                } catch (Exception e) {
                    String errorMsg = "❌ Failed to process admin item: " + itemName + ". Error: " + e.getMessage();
                    log.error(errorMsg, e);
                    safeExtentFail(errorMsg);
                    
                    // Take a screenshot on failure
                    try {
                        String screenshotPath = "screenshots/error_" + itemName.replace(" ", "_") + "_" + System.currentTimeMillis() + ".png";
                        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)));
                        safeExtentLog("Screenshot saved: " + screenshotPath);
                    } catch (Exception screenshotEx) {
                        log.error("Failed to take screenshot: " + screenshotEx.getMessage());
                    }
                }
            }
            
            // Final test status
            test.info("✅ Successfully completed admin menu navigation test");
            log.info("Successfully completed admin menu navigation test");
            
        } catch (Exception e) {
            String errorMsg = "❌ Test failed with exception: " + e.getMessage();
            log.error(errorMsg, e);
            safeExtentFail(errorMsg);
            
            // Take a screenshot on failure
            try {
                String screenshotPath = "screenshots/test_failure_" + System.currentTimeMillis() + ".png";
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)));
                log.info("Screenshot saved: " + screenshotPath);
            } catch (Exception screenshotEx) {
                log.error("Failed to take screenshot: " + screenshotEx.getMessage());
            }
            
            // Rethrow to fail the test
            throw e;
        }
    }

    // --- Expand Panel ---
    @Test(priority = 2) // TODO: Set priority as needed
    public void panelExpansion() {
        try {
            String domain = ConfigReader.get("domain");
            String project = ConfigReader.get("project");
            ExpandPanelPage expandPanelPage = new ExpandPanelPage(page);
            log.info("ExpandPanelPage initialized");
            test.info("ExpandPanelPage initialized");
            expandPanelPage.selectDomain(domain, test);
            log.info("Domain selected: " + domain);
            test.info("Domain selected: " + domain);
            expandPanelPage.selectProject(project, test);
            log.info("Project selected: " + project);
            test.info("Project selected: " + project);
            expandPanelPage.goToProjectDetails(test);
            log.info("Navigated to project details");
            test.info("Navigated to project details");
            expandPanelPage.expandPanel(test);
            log.info("Panel expanded");
            test.info("Panel expanded");
            expandPanelPage.collapsePanel(test);
            log.info("Panel collapsed");
            test.info("Panel collapsed");
        } catch (Exception e) {
            log.error("Test failed: " + e.getMessage(), e);
            test.fail("Test failed: " + e.getMessage());
            throw new RuntimeException("Test failed due to exception: ", e);
        }
    }

    // --- Create New File ---
    @Test (priority = 3) // TODO: Set priority as needed
    public void createNewFileAndCheckRow() {
        try {
            CreateNewFilePage createNewFilePage = new CreateNewFilePage(page);
            String domain = ConfigReader.get("domain");
            String project = ConfigReader.get("project");
            String rowId = ConfigReader.get("rowId");
            String erowId = ConfigReader.get("erowId");
            createNewFilePage.selectDomain(domain, test);
            createNewFilePage.selectProject(project, test);
            createNewFilePage.clickCreateNewFile(test);
            Frame frame = createNewFilePage.switchToCreateFileIframe(test);
            createNewFilePage.fillCreateFileForm(frame, rowId, erowId, test);
            page.waitForTimeout(10000);
            boolean found = createNewFilePage.isRowPresent(rowId, test);
            if (!found) {
                log.info("Could not find row with ROW ID: " + rowId);
                test.fail("Could not find row with ROW ID: " + rowId);
            }
        } catch (Exception e) {
            log.error("Test failed: " + e.getMessage());
            test.fail("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Complete Login and Tab Selection ---
    @Test (priority = 4)
    public void completeLoginAndTabSelection() {
        try {
            String domain = ConfigReader.get("domain");
            String project = ConfigReader.get("project");
            CompleteLoginPage completeLoginPage = new CompleteLoginPage(page);
            completeLoginPage.selectDomain(domain, test);
            completeLoginPage.selectProject(project, test);
            completeLoginPage.goToProjectDetails(test);
            String[] tabNames = {
                "Assignments",
                "Liaison Files",
                "Estimates",
                "Contracting",
                "Authorization Management",
                "Issue Management",
                "Financials",
                "Expropriation",
                "Checklist",
                "Utility Coordination",
                "Railroad Coordination"
            };
            for (String tab : tabNames) {
                completeLoginPage.clickTab(tab, test);
            }
            test.pass("Successfully completed login and tab selection flow.");
        } catch (Exception e) {
            log.error("Test failed: " + e.getMessage());
            test.fail("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Top Menu Navigation ---
    @Test (priority = 5)// TODO: Set priority as needed
    public void clickTopMenuItems() {
        try {
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
            docMgmtPage.waitForTimeout(2000);
            docMgmtPage.close();
            page.waitForTimeout(5000);
            // Click 'GIS' (new window)
            Page gisPage = page.context().waitForPage(() -> {
                topMenu.clickGIS();
            });
            test.info("Clicked 'GIS' menu item and new window opened");
            log.info("Clicked 'GIS' menu item and new window opened");
            gisPage.waitForTimeout(2000);
            gisPage.close();
            page.waitForTimeout(5000);
            // Click 'Dashboard' (same window)
            topMenu.clickDashboard();
            test.info("Clicked 'Dashboard' menu item");
            log.info("Clicked 'Dashboard' menu item");
            page.waitForTimeout(2000);
            test.pass("Successfully clicked all top menu items.");
            log.info("Successfully clicked all top menu items.");
        } catch (Exception e) {
            test.fail("Test failed: " + e.getMessage());
            log.error("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Complete Login and Panel Test ---
    @Test (priority = 6, enabled=false)// TODO: Set priority as needed
    public void loginAndOpenClosePanels() {
        try {
            String domain = config.ConfigReader.get("domain");
            String project = config.ConfigReader.get("project");
            CompleteLoginAndPanelPage completePage = new CompleteLoginAndPanelPage(page);
            test.info("Page object created");
            log.info("Page object created");
            completePage.selectDomain(domain, test);
            test.info("Domain selected");
            log.info("Domain selected");
            completePage.selectProject(project, test);
            test.info("Project selected");
            log.info("Project selected");
            completePage.goToProjectDetails(test);
            test.info("Navigated to project details");
            log.info("Navigated to project details");

            String[] panelTitles = {
                "Project Information", "Project Status Log", "Right of Way Maps", "External Agreement",
                "Authorization Summary", "Sales Book", "Certification", "Import", "All Project Information Documents"
            };

            utilities.PanelActions panelActions = new utilities.PanelActions(page);

            for (String panelTitle : panelTitles) {
                try {
                    panelActions.openPanel(panelTitle);
                    page.waitForTimeout(1000); // Optional: wait for UI stability
                    panelActions.closePanel(panelTitle);
                    page.waitForTimeout(500);  // Optional: wait for UI stability
                    test.info("Successfully opened and closed panel: " + panelTitle);
                    log.info("Successfully opened and closed panel: " + panelTitle);
                } catch (Exception e) {
                    test.fail("Error handling panel: " + panelTitle + " - " + e.getMessage());
                    log.error("Error handling panel: " + panelTitle + " - " + e.getMessage());
                }
            }
            test.pass("Panels opened and closed successfully.");
            log.info("Panels opened and closed successfully.");
        } catch (Exception e) {
            test.fail("Failed to expand/collapse panels in all tabs: " + e.getMessage());
            log.error("Failed to expand/collapse panels in all tabs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Expand All Panels In All Tabs ---
    @Test (priority = 7)// TODO: Set priority as needed
    public void expandAndCollapseAllPanelsInAllTabs() {
        try {
            String domain = config.ConfigReader.get("domain");
            String project = config.ConfigReader.get("project");
            ExpandAllPanelsInAllTabsPage expandPage = new ExpandAllPanelsInAllTabsPage(page);
            test.info("Page object created");
            log.info("Page object created");
            expandPage.selectDomain(domain, test);
            test.info("Domain selected");
            log.info("Domain selected");
            expandPage.selectProject(project, test);
            test.info("Project selected");
            log.info("Project selected");
            expandPage.goToProjectDetails(test);
            test.info("Navigated to project details");
            log.info("Navigated to project details");
            expandPage.expandAndCollapseAllPanelsInAllTabs(test);
            test.pass("Successfully expanded and collapsed all panels in all tabs.");
            log.info("Successfully expanded and collapsed all panels in all tabs.");
        } catch (Exception e) {
            test.fail("Test failed: " + e.getMessage());
            log.error("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test(priority = 8)
    public void switchToDomain2AndSelectFirstFile() {
        try {
            String domain2 = config.ConfigReader.get("domain2");
            String project2 = config.ConfigReader.get("project2");
            FileSelectionPage fileSelectionPage = new FileSelectionPage(page);

            fileSelectionPage.selectDomain2(domain2, test);
            fileSelectionPage.selectProject2(project2, test);
            // Optionally, add a wait here if the file table takes time to load
            fileSelectionPage.clickFirstFileInTable(test);

            // Click all required file tabs in sequence
            String[] tabNames = {
                "File Information",
                "Assignments",
                "Lease/Rental/Permit",
                "Legal Description",
                "Appraisal",
                "Surplus",
                "Marketing",
                "Sale/Closing",
                "Property Diary",
                "Financials",
                "Checklist"
            };
            for (String tab : tabNames) {
                fileSelectionPage.clickTabByName(tab, test);
            }

            // Go to project details
            fileSelectionPage.goToProjectDetails(test);

            // Click 'Project Information' and 'Checklist' tabs in project context
            fileSelectionPage.clickTabByName("Project Information", test);
            fileSelectionPage.clickTabByName("Checklist", test);

            test.pass("Successfully switched to domain2/project2, clicked the first file, navigated all file tabs, went to project details, and navigated required project tabs.");
            log.info("Successfully switched to domain2/project2, clicked the first file, navigated all file tabs, went to project details, and navigated required project tabs.");
        } catch (Exception e) {
            test.fail("Failed to switch domain2/project2 and select file: " + e.getMessage());
            log.error("Failed to switch domain2/project2 and select file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test(priority = 9)
    public void expandAndCollapsePanelsInAllTabsAfterFileSelection() {
        try {
            String domain2 = config.ConfigReader.get("domain2");
            String project2 = config.ConfigReader.get("project2");
            PanelExpandCollapsePage panelPage = new PanelExpandCollapsePage(page);

            panelPage.selectDomain2(domain2, test);
            panelPage.selectProject2(project2, test);
            panelPage.clickFirstFileInTable(test);

            String[] tabNames = {
                "File Information",
                "Assignments",
                "Lease/Rental/Permit",
                "Legal Description",
                "Appraisal",
                "Surplus",
                "Marketing",
                "Sale/Closing",
                "Property Diary",
                "Financials",
                "Checklist"
            };
            String TAB_XPATH_TEMPLATE = "//a[.//span[@class='rtsTxt' and normalize-space(text())='%s']]";
            String EXPAND_XPATH = "//*[@id='ctl00_Main_DynamicContent1_ibPlus']";
            String COLLAPSE_XPATH = "//*[@id='ctl00_Main_DynamicContent1_ibMinus']";

            for (String tabName : tabNames) {
                String tabXPath = String.format(TAB_XPATH_TEMPLATE, tabName);
                page.waitForSelector(tabXPath, new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(60000).setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
                page.click(tabXPath);
                test.info("Clicked tab: " + tabName);
                log.info("Clicked tab: " + tabName);

                // Wait for panels to load
                page.waitForTimeout(3000);
                // Wait for overlays/loaders to disappear before expanding
                page.waitForSelector(".loading", new com.microsoft.playwright.Page.WaitForSelectorOptions().setState(com.microsoft.playwright.options.WaitForSelectorState.HIDDEN).setTimeout(60000));

                int expandCount = page.locator(EXPAND_XPATH).count();
                log.info("Expand button count in tab " + tabName + ": " + expandCount);
                if (expandCount == 0) {
                    test.info("No expand buttons found in tab: " + tabName);
                }
                for (int i = 0; i < expandCount; i++) {
                    com.microsoft.playwright.Locator expandBtn = page.locator(EXPAND_XPATH).nth(i);
                    if (expandBtn.isVisible() && expandBtn.isEnabled()) {
                        expandBtn.click();
                        test.info("Expanded panel " + (i + 1) + " in tab: " + tabName);
                        log.info("Expanded panel " + (i + 1) + " in tab: " + tabName);
                        page.waitForTimeout(5000);
                    } else {
                        test.fail("Expand button " + (i + 1) + " not visible or not enabled in tab: " + tabName);
                        log.info("Expand button " + (i + 1) + " not visible or not enabled in tab: " + tabName);
                    }
                }

                page.waitForTimeout(2000);
                // Wait for overlays/loaders to disappear before collapsing
                page.waitForSelector(".loading", new com.microsoft.playwright.Page.WaitForSelectorOptions().setState(com.microsoft.playwright.options.WaitForSelectorState.HIDDEN).setTimeout(60000));

                int collapseCount = page.locator(COLLAPSE_XPATH).count();
                for (int i = 0; i < collapseCount; i++) {
                    com.microsoft.playwright.Locator collapseBtn = page.locator(COLLAPSE_XPATH).nth(i);
                    if (collapseBtn.isVisible() && collapseBtn.isEnabled()) {
                        collapseBtn.click();
                        test.info("Collapsed panel " + (i + 1) + " in tab: " + tabName);
                        log.info("Collapsed panel " + (i + 1) + " in tab: " + tabName);
                        page.waitForTimeout(1000);
                    } else {
                        test.fail("Collapse button " + (i + 1) + " not visible or not enabled in tab: " + tabName);
                        log.info("Collapse button " + (i + 1) + " not visible or not enabled in tab: " + tabName);
                    }
                }

                page.waitForTimeout(2000);
            }
            test.pass("Successfully expanded and collapsed all panels in all tabs.");
            log.info("Successfully expanded and collapsed all panels in all tabs.");
        } catch (Exception e) {
            test.fail("Failed to expand/collapse panels in all tabs: " + e.getMessage());
            log.error("Failed to expand/collapse panels in all tabs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test(priority = 10)
    public void openAndCloseCreateNewFilePopup() {
        try {
            CreateNewFilePage createNewFilePage = new CreateNewFilePage(page);
            String domain2 = config.ConfigReader.get("domain2");
            String project2 = config.ConfigReader.get("project2");
            createNewFilePage.selectDomain(domain2, test);
            createNewFilePage.selectProject(project2, test);
            createNewFilePage.clickCreateNewFile(test);
            // Wait for popup (iframe) to appear
            com.microsoft.playwright.Locator iframeLocator = page.locator("iframe[name='CreateFilewindow']");
            iframeLocator.waitFor(new com.microsoft.playwright.Locator.WaitForOptions().setTimeout(10000).setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
            test.info("Create New File popup appeared");
            log.info("Create New File popup appeared");
            page.waitForTimeout(3000); // Wait for 3 seconds to simulate user viewing the popup
            // Close the popup by clicking the close button in the iframe's parent (usually a modal close button)
            String CLOSE_POPUP_XPATH = "//a[contains(@class,'rwCloseButton') and @title='Close']";
            page.waitForSelector(CLOSE_POPUP_XPATH, new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(10000).setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
            page.click(CLOSE_POPUP_XPATH);
            test.pass("Successfully opened and closed the Create New File popup.");
            log.info("Successfully opened and closed the Create New File popup.");
        } catch (Exception e) {
            test.fail("Failed to open/close Create New File popup: " + e.getMessage());
            log.error("Failed to open/close Create New File popup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test(priority = 11)
    public void openAndCloseCreateNewProjectPopup() {
        try {
            String domain2 = config.ConfigReader.get("domain2");
            pages.CreateNewProjectPage createNewProjectPage = new pages.CreateNewProjectPage(page);
            createNewProjectPage.selectDomain(domain2, test);
            createNewProjectPage.clickCreateNewProject(test);
            createNewProjectPage.waitForPopup(test);
            page.waitForTimeout(3000); // Wait for 3 seconds to simulate user viewing the popup
            // Switch to the correct iframe and click the Save & Close button using the provided XPath
            com.microsoft.playwright.Frame popupFrame = page.frame("CreateProjectWindow");
            if (popupFrame == null) {
                throw new RuntimeException("Create Project iframe not found");
            }
            String SAVE_AND_CLOSE_XPATH = "//*[@id='btnCreateProject']";
            popupFrame.waitForSelector(SAVE_AND_CLOSE_XPATH, new com.microsoft.playwright.Frame.WaitForSelectorOptions().setTimeout(10000).setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
            popupFrame.click(SAVE_AND_CLOSE_XPATH);
            test.pass("Successfully opened and closed the Create New Project popup by clicking Save & Close.");
            log.info("Successfully opened and closed the Create New Project popup by clicking Save & Close.");
        } catch (Exception e) {
            test.fail("Failed to open/close Create New Project popup: " + e.getMessage());
            log.error("Failed to open/close Create New Project popup: " + e.getMessage());
            e.printStackTrace();
        }
    }
}