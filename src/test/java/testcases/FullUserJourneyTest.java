package testcases;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import static base.BaseTest.OR;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.microsoft.playwright.Page;
import org.apache.log4j.Logger;
import pages.AdminPage;
import pages.ExpandPanelPage;
import pages.CreateNewFilePage;
import com.microsoft.playwright.Frame;
import config.ConfigReader;
import com.aventstack.extentreports.ExtentTest;
import pages.CompleteLoginAndPanelPage;
import pages.CompleteLoginPage;
import pages.TopMenuPage;
import pages.ExpandAllPanelsInAllTabsPage;
import pages.FileSelectionPage;
import pages.PanelExpandCollapsePage;

/**
 * FullUserJourneyTest class contains tests for the full user journey.
 */
public class FullUserJourneyTest extends BaseTest {
    private static final Logger log = Logger.getLogger(FullUserJourneyTest.class);

    // --- Admin menu navigation ---
    @Test(priority = 1)
    public void adminMenuNavigation() {
        try {
            log.info("Starting admin menu navigation test");
            safeExtentLog("Starting admin menu navigation test");

                     
            // 2. Wait for the page to be fully loaded after login
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.LOAD);
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED);
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
            
            // 3. Initialize AdminPage
            AdminPage adminPage = new AdminPage(page);
            log.info("AdminPage initialized");
            
            // 4. Click the Admin menu to expand it
            adminPage.clickAdminMenu(getExtentTest());
            
            // 5. Add a small delay to ensure the menu is fully expanded
            page.waitForTimeout(1000);
            
            // 6. Verify admin menu is visible
            boolean isMenuVisible = adminPage.isAdminMenuVisible();
            log.info("Is admin menu visible: " + isMenuVisible);
            
            if (!isMenuVisible) {
                safeExtentFail("Admin menu is not visible after clicking");
                return;
            }
            
            // 7. Get admin menu title
            String menuTitle = adminPage.getAdminMenuTitle();
            log.info("Admin menu title: " + menuTitle);
            
            // 8. Soft assertions for basic menu verification
            org.testng.asserts.SoftAssert softAssert = new org.testng.asserts.SoftAssert();
            softAssert.assertTrue(isMenuVisible, "Admin menu should be visible after clicking");
            softAssert.assertEquals(menuTitle, "Admin", 
                String.format("Admin menu title should be 'Admin' | Actual: %s, Expected: Admin", menuTitle));
            
            // 9. Log the result of assertions
            if (isMenuVisible && "Admin".equals(menuTitle)) {
                safeExtentPass("Admin menu is visible and has the correct title");
                
                // Navigation through admin items
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
                
                for (String[] item : adminItems) {
                    String itemSelector = item[0];
                    String itemType = item[1];
                    String itemName = "";
                    
                    try {
                        // Extract item name safely
                        if (itemSelector != null && itemSelector.contains("@title='") && itemSelector.contains("']")) {
                            itemName = itemSelector.substring("//a[@title='".length(), itemSelector.indexOf("']"));
                        } else {
                            log.warn("Invalid item selector format: " + itemSelector);
                            continue;
                        }
                        
                        log.info("Processing admin item: " + itemName);
                        safeExtentLog("Processing admin item: " + itemName);
                        
                        // Validate selector before use
                        if (itemSelector == null || itemSelector.trim().isEmpty()) {
                            throw new IllegalArgumentException("Item selector cannot be null or empty");
                        }
                        
                        // Execute the admin item action
                        adminPage.handleAdminItem(itemSelector, itemType, getExtentTest());
                        
                        safeExtentLog("Successfully processed: " + itemName);
                        
                        // Small delay between items to avoid overwhelming the UI
                        page.waitForTimeout(500);
                        
                    } catch (Exception e) {
                        String errorMsg = "Failed to process admin item: " + itemName + ". Error: " + e.getMessage();
                        safeExtentFail(errorMsg);
                        log.error(errorMsg, e);
                        
                        // Take a screenshot on failure
                        try {
                            String screenshotPath = "screenshots/error_" + itemName.replace(" ", "_") + "_" + System.currentTimeMillis() + ".png";
                            Path path = Paths.get(screenshotPath);
                            Files.createDirectories(path.getParent());
                            Files.write(path, page.screenshot());
                            safeExtentLog("Screenshot saved: " + screenshotPath);
                        } catch (Exception screenshotEx) {
                            log.error("Failed to take screenshot: " + screenshotEx.getMessage());
                        }
                    }
                }
            } else {
                safeExtentFail("Admin menu verification failed");
            }
            
            // Assert all soft assertions
            softAssert.assertAll();
            
        } catch (Exception e) {
            String errorMsg = "Test failed with exception: " + e.getMessage();
            safeExtentFail(errorMsg);
            log.error(errorMsg, e);
            
            // Take a screenshot on failure
            try {
                String screenshotPath = "screenshots/admin_menu_error_" + System.currentTimeMillis() + ".png";
                Path path = Paths.get(screenshotPath);
                Files.createDirectories(path.getParent());
                Files.write(path, page.screenshot());
                safeExtentLog("Screenshot saved: " + screenshotPath);
            } catch (Exception screenshotEx) {
                log.error("Failed to take screenshot: " + screenshotEx.getMessage());
            }
            
            throw new RuntimeException("Admin menu navigation test failed", e);
        }
    }

    // --- Expand Panel ---
    @Test(priority = 2)
    public void panelExpansion() {
        try {
            utils.SoftAssertionUtils softAssert = new utils.SoftAssertionUtils(extentTest,log);
            String domain = ConfigReader.get("domain");
            String project = ConfigReader.get("project");
            ExpandPanelPage expandPanelPage = new ExpandPanelPage(page);
            log.info("Domain selected: " + domain);
            safeExtentPass("Domain selected: " + domain);
            expandPanelPage.selectProject(project, extentTest);
            log.info("Project selected: " + project);
            safeExtentPass("Project selected: " + project);
            expandPanelPage.goToProjectDetails(extentTest);
            log.info("Navigated to project details");
            safeExtentPass("Navigated to project details");
            expandPanelPage.expandPanel(extentTest);

            // Verify initial panel state is collapsed
            softAssert.assertTrue(expandPanelPage.isPanelCollapsed(), 
                "Panel should be initially collapsed");

            // Expand the panel and verify state
            expandPanelPage.expandPanel(extentTest);
            softAssert.assertTrue(expandPanelPage.isPanelExpanded(), 
                "Panel should be expanded after clicking expand button");
            
            // Collapse the panel and verify state
            expandPanelPage.collapsePanel(extentTest);
            softAssert.assertTrue(expandPanelPage.isPanelCollapsed(), 
                "Panel should be collapsed after clicking collapse button");

            // Run all assertions
            softAssert.assertAll();
            
            log.info("Panel expansion test completed successfully");
            extentTest.pass("Panel expansion test completed successfully");
        } catch (Exception e) {
            log.error("Test failed: " + e.getMessage(), e);
            extentTest.fail("Test failed: " + e.getMessage());
            throw new RuntimeException("Test failed due to exception: ", e);
        }
    }

    // --- Create New File ---
    @Test (priority = 3) // TODO: Set priority as needed
    public void createNewFileAndCheckRow() {
    
        
        try {
            utils.SoftAssertionUtils softAssert = new utils.SoftAssertionUtils(extentTest, log);
            CreateNewFilePage createNewFilePage = new CreateNewFilePage(page);
            String domain = ConfigReader.get("domain");
            String project = ConfigReader.get("project");
            String rowId = ConfigReader.get("rowId");
            String erowId = ConfigReader.get("erowId");
            createNewFilePage.selectDomain(domain, extentTest);
            createNewFilePage.selectProject(project, extentTest);
            createNewFilePage.clickCreateNewFile(extentTest);
            Frame frame = createNewFilePage.switchToCreateFileIframe(extentTest);
            softAssert.assertTrue(createNewFilePage.fillCreateFileForminfo(frame, rowId, extentTest), "Created file form successfully" );
            page.waitForTimeout(10000);
            boolean found = createNewFilePage.isRowPresent(rowId, extentTest);
            
            if (!found) {
                log.info("Could not find row with ROW ID: " + rowId);
                extentTest.fail("Could not find row with ROW ID: " + rowId);
            }
        } catch (Exception e) {
            log.error("Test failed: " + e.getMessage());
            safeExtentFail("Test failed: " + e.getMessage());
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
            log.info("Starting domain selection: " + domain);
            completeLoginPage.selectDomain(domain, extentTest);
            log.info("Domain selected: " + domain);
            log.info("Starting project selection: " + project);
            completeLoginPage.selectProject(project, extentTest);
            log.info("Project selected: " + project);
            log.info("Navigating to project details");
            completeLoginPage.goToProjectDetails(extentTest);
            log.info("Navigated to project details");
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
                completeLoginPage.clickTab(tab, extentTest);
            }
            extentTest.pass("Successfully completed login and tab selection flow.");
        } catch (Exception e) {
            log.error("Test failed: " + e.getMessage());
            safeExtentFail("Test failed: " + e.getMessage());
            e.printStackTrace();    
        }
    }

    // --- Top Menu Navigation ---
    @Test (priority = 5)
    public void clickTopMenuItems() {
        try {
            TopMenuPage topMenu = new TopMenuPage(page);
            // Click 'Search'
            topMenu.clickSearch();
            log.info("Clicked 'Search' menu item");
            page.waitForTimeout(5000);
            // Click 'My Work Queue'
            topMenu.clickMyWorkQueue();
            log.info("Clicked 'My Work Queue' menu item");
            page.waitForTimeout(5000);
            // Click 'My Profile'
            topMenu.clickMyProfile();
            log.info("Clicked 'My Profile' menu item");
            page.waitForTimeout(5000);
            // Click 'PM Inquiry' (new window)
            Page pmInquiryPage = page.context().waitForPage(() -> {
                topMenu.clickPMInquiry();
            });
            log.info("Clicked 'PM Inquiry' menu item and new window opened");
            pmInquiryPage.waitForTimeout(2000);
            pmInquiryPage.close(); // Close new window
            // Now just continue with the original page (main tab)
            topMenu.clickNotifications();
            log.info("Clicked 'Notifications' menu item");
            page.waitForTimeout(5000);
            // Click 'Doc Mgmt' (new window)
            Page docMgmtPage = page.context().waitForPage(() -> {
                topMenu.clickDocMgmt();
            });
            log.info("Clicked 'Doc Mgmt' menu item and new window opened");
            docMgmtPage.waitForTimeout(2000);
            docMgmtPage.close();
            page.waitForTimeout(5000);
            // Click 'GIS' (new window)
            Page gisPage = page.context().waitForPage(() -> {
                topMenu.clickGIS();
            });
            log.info("Clicked 'GIS' menu item and new window opened");
            gisPage.waitForTimeout(2000);
            gisPage.close();
            page.waitForTimeout(5000);
            // Click 'Dashboard' (same window)
            topMenu.clickDashboard();
            log.info("Clicked 'Dashboard' menu item");
            page.waitForTimeout(2000);
            log.info("Successfully clicked all top menu items.");
        } catch (Exception e) {
            safeExtentFail("Test failed: " + e.getMessage());
            log.error("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Complete Login and Panel Test ---
    @Test (priority = 6, enabled=false)// TODO: Set priority as needed
    public void loginAndOpenClosePanels() {
        ExtentTest extentTest = getExtentTest();
        if (extentTest == null) {
            log.error("Failed to initialize ExtentTest for adminMenuNavigation");
            Assert.fail("ExtentTest initialization failed");
        }
        try {
            String domain = config.ConfigReader.get("domain");
            String project = config.ConfigReader.get("project");
            CompleteLoginAndPanelPage completePage = new CompleteLoginAndPanelPage(page);
            extentTest.info("Page object created");
            log.info("Page object created");
            completePage.selectDomain(domain, extentTest);
            extentTest.info("Domain selected");
            log.info("Domain selected");
            completePage.selectProject(project, extentTest);
            extentTest.info("Project selected");
            log.info("Project selected");
            completePage.goToProjectDetails(extentTest);
            extentTest.info("Navigated to project details");
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
                    extentTest.info("Successfully opened and closed panel: " + panelTitle);
                    log.info("Successfully opened and closed panel: " + panelTitle);
                } catch (Exception e) {
                    safeExtentFail("Error handling panel: " + panelTitle + " - " + e.getMessage());
                    log.error("Error handling panel: " + panelTitle + " - " + e.getMessage());
                }
            }
            log.info("Panels opened and closed successfully.");
        } catch (Exception e) {
            log.error("Failed to expand/collapse panels in all tabs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Expand All Panels In All Tabs ---
    @Test (priority = 7)// TODO: Set priority as needed
    public void expandAndCollapseAllPanelsInAllTabs() {
        ExtentTest extentTest = getExtentTest();
        if (extentTest == null) {
            log.error("Failed to initialize ExtentTest for expandAndCollapseAllPanelsInAllTabs");
            Assert.fail("ExtentTest initialization failed");
        }
        try {
            String domain = config.ConfigReader.get("domain");
            String project = config.ConfigReader.get("project");
            ExpandAllPanelsInAllTabsPage expandPage = new ExpandAllPanelsInAllTabsPage(page);
            extentTest.info("Page object created");
            log.info("Page object created");
            expandPage.selectDomain(domain, extentTest);
            extentTest.info("Domain selected");
            log.info("Domain selected");
            expandPage.selectProject(project, extentTest);
            extentTest.info("Project selected");
            log.info("Project selected");
            expandPage.goToProjectDetails(extentTest);
            extentTest.info("Navigated to project details");
            log.info("Navigated to project details");
            expandPage.expandAndCollapseAllPanelsInAllTabs(extentTest);
            extentTest.pass("Successfully expanded and collapsed all panels in all tabs.");
            log.info("Successfully expanded and collapsed all panels in all tabs.");
        } catch (Exception e) {
            safeExtentFail("Test failed: " + e.getMessage());
            log.error("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test(priority = 8)
    public void switchToDomain2AndSelectFirstFile() {
        ExtentTest extentTest = getExtentTest();
        if (extentTest == null) {
            log.error("Failed to initialize ExtentTest for switchToDomain2AndSelectFirstFile");
            Assert.fail("ExtentTest initialization failed");
        }
        try {
            String domain2 = config.ConfigReader.get("domain2");
            String project2 = config.ConfigReader.get("project2");
            FileSelectionPage fileSelectionPage = new FileSelectionPage(page);

            fileSelectionPage.selectDomain2(domain2, extentTest);
            extentTest.info("Domain2 selected");
            log.info("Domain2 selected");
            fileSelectionPage.selectProject2(project2, extentTest);
            extentTest.info("Project2 selected");
            log.info("Project2 selected");
            // Optionally, add a wait here if the file table takes time to load
            fileSelectionPage.clickFirstFileInTable(extentTest);

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
                fileSelectionPage.clickTabByName(tab, extentTest);
                extentTest.info("Tab " + tab + " clicked");
                log.info("Tab " + tab + " clicked");
            }

            // Go to project details    
            fileSelectionPage.goToProjectDetails(extentTest);
            extentTest.info("Navigated to project details");
            log.info("Navigated to project details");

            // Click 'Project Information' and 'Checklist' tabs in project context
            fileSelectionPage.clickTabByName("Project Information", extentTest);
            fileSelectionPage.clickTabByName("Checklist", extentTest);
            extentTest.info("Project Information and Checklist tabs clicked");
            log.info("Project Information and Checklist tabs clicked");

            extentTest.pass("Successfully switched to domain2/project2, clicked the first file, navigated all file tabs, went to project details, and navigated required project tabs.");
            log.info("Successfully switched to domain2/project2, clicked the first file, navigated all file tabs, went to project details, and navigated required project tabs.");
        } catch (Exception e) {
            extentTest.fail("Failed to switch domain2/project2 and select file: " + e.getMessage());
            log.error("Failed to switch domain2/project2 and select file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test(priority = 9)
    public void expandAndCollapsePanelsInAllTabsAfterFileSelection() {
        ExtentTest extentTest = getExtentTest();
        if (extentTest == null) {
            log.error("Failed to initialize ExtentTest for expandAndCollapsePanelsInAllTabsAfterFileSelection");
            Assert.fail("ExtentTest initialization failed");
        }
        try {
            String domain2 = config.ConfigReader.get("domain2");
            String project2 = config.ConfigReader.get("project2");
            PanelExpandCollapsePage panelPage = new PanelExpandCollapsePage(page);

            panelPage.selectDomain2(domain2, extentTest);
            panelPage.selectProject2(project2, extentTest);
            panelPage.clickFirstFileInTable(extentTest);

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
                extentTest.info("Clicked tab: " + tabName);
                log.info("Clicked tab: " + tabName);

                // Wait for panels to load
                page.waitForTimeout(3000);
                // Wait for overlays/loaders to disappear before expanding
                page.waitForSelector(".loading", new com.microsoft.playwright.Page.WaitForSelectorOptions().setState(com.microsoft.playwright.options.WaitForSelectorState.HIDDEN).setTimeout(60000));

                int expandCount = page.locator(EXPAND_XPATH).count();
                log.info("Expand button count in tab " + tabName + ": " + expandCount);
                if (expandCount == 0) {
                    extentTest.info("No expand buttons found in tab: " + tabName);
                }
                for (int i = 0; i < expandCount; i++) {
                    com.microsoft.playwright.Locator expandBtn = page.locator(EXPAND_XPATH).nth(i);
                    if (expandBtn.isVisible() && expandBtn.isEnabled()) {
                        expandBtn.click();
                        extentTest.info("Expanded panel " + (i + 1) + " in tab: " + tabName);
                        log.info("Expanded panel " + (i + 1) + " in tab: " + tabName);
                        page.waitForTimeout(5000);
                    } else {
                        extentTest.fail("Expand button " + (i + 1) + " not visible or not enabled in tab: " + tabName);
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
                        extentTest.info("Collapsed panel " + (i + 1) + " in tab: " + tabName);
                        log.info("Collapsed panel " + (i + 1) + " in tab: " + tabName);
                        page.waitForTimeout(1000);
                    } else {
                        extentTest.fail("Collapse button " + (i + 1) + " not visible or not enabled in tab: " + tabName);
                        log.info("Collapse button " + (i + 1) + " not visible or not enabled in tab: " + tabName);
                    }
                }

                page.waitForTimeout(2000);
            }
            extentTest.pass("Successfully expanded and collapsed all panels in all tabs.");
            log.info("Successfully expanded and collapsed all panels in all tabs.");
        } catch (Exception e) {
            extentTest.fail("Failed to expand/collapse panels in all tabs: " + e.getMessage());
            log.error("Failed to expand/collapse panels in all tabs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test(priority = 10)
    public void openAndCloseCreateNewFilePopup() {
        ExtentTest extentTest = getExtentTest();
        if (extentTest == null) {
            log.error("Failed to initialize ExtentTest for openAndCloseCreateNewFilePopup");
            Assert.fail("ExtentTest initialization failed");
        }
        try {
            utils.SoftAssertionUtils softAssert = new utils.SoftAssertionUtils(extentTest, log);
            CreateNewFilePage createNewFilePage = new CreateNewFilePage(page);
            String domain2 = config.ConfigReader.get("domain2");
            String project2 = config.ConfigReader.get("project2");
            createNewFilePage.selectDomain(domain2, extentTest);
            createNewFilePage.selectProject(project2, extentTest);
            createNewFilePage.clickCreateNewFile(extentTest);
            // Example soft assertion (replace with real check)
            softAssert.assertTrue(page.isVisible("iframe[name='CreateFilewindow']"), "Create New File popup iframe should be visible");
            // Wait for popup (iframe) to appear
            com.microsoft.playwright.Locator iframeLocator = page.locator("iframe[name='CreateFilewindow']");
            iframeLocator.waitFor(new com.microsoft.playwright.Locator.WaitForOptions().setTimeout(10000).setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
            extentTest.info("Create New File popup appeared");
            log.info("Create New File popup appeared");
            page.waitForTimeout(3000); // Wait for 3 seconds to simulate user viewing the popup
            // Close the popup by clicking the close button in the iframe's parent (usually a modal close button)
            String CLOSE_POPUP_XPATH = "//a[contains(@class,'rwCloseButton') and @title='Close']";
            page.waitForSelector(CLOSE_POPUP_XPATH, new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(10000).setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
            page.click(CLOSE_POPUP_XPATH);
            extentTest.pass("Successfully opened and closed the Create New File popup.");
            log.info("Successfully opened and closed the Create New File popup.");
            softAssert.assertAll();
        } catch (Exception e) {
            extentTest.fail("Failed to open/close Create New File popup: " + e.getMessage());
            log.error("Failed to open/close Create New File popup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test(priority = 11)
    public void openAndCloseCreateNewProjectPopup() {
        ExtentTest extentTest = getExtentTest();
        if (extentTest == null) {
            log.error("Failed to initialize ExtentTest for openAndCloseCreateNewProjectPopup");
            Assert.fail("ExtentTest initialization failed");
        }
        try {
            utils.SoftAssertionUtils softAssert = new utils.SoftAssertionUtils(extentTest, log);
            String domain2 = config.ConfigReader.get("domain2");
            pages.CreateNewProjectPage createNewProjectPage = new pages.CreateNewProjectPage(page);
            createNewProjectPage.selectDomain(domain2, extentTest);
            createNewProjectPage.clickCreateNewProject(extentTest);
            createNewProjectPage.waitForPopup(extentTest);
            // Example soft assertion (replace with real check)
            softAssert.assertTrue(page.isVisible("iframe[name='CreateProjectWindow']"), "Create New Project popup iframe should be visible");
            page.waitForTimeout(3000); // Wait for 3 seconds to simulate user viewing the popup
            // Switch to the correct iframe and click the Save & Close button using the provided XPath
            com.microsoft.playwright.Frame popupFrame = page.frame("CreateProjectWindow");
            if (popupFrame == null) {
                throw new RuntimeException("Create Project iframe not found");
            }
            String SAVE_AND_CLOSE_XPATH = "//*[@id='btnCreateProject']";
            popupFrame.waitForSelector(SAVE_AND_CLOSE_XPATH, new com.microsoft.playwright.Frame.WaitForSelectorOptions().setTimeout(10000).setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
            popupFrame.click(SAVE_AND_CLOSE_XPATH);
            extentTest.pass("Successfully opened and closed the Create New Project popup by clicking Save & Close.");
            log.info("Successfully opened and closed the Create New Project popup by clicking Save & Close.");
            softAssert.assertAll();
        } catch (Exception e) {
            extentTest.fail("Failed to open/close Create New Project popup: " + e.getMessage());
            log.error("Failed to open/close Create New Project popup: " + e.getMessage());
            e.printStackTrace();
        }
    }
}