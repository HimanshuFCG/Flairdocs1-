package testcases;

import base.BaseTest;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;

import org.testng.annotations.Test;
import org.testng.annotations.AfterClass;
import com.aventstack.extentreports.ExtentTest;
import org.apache.log4j.Logger;
import config.ConfigReader;


public class Admin extends BaseTest {
    private static final Logger log = Logger.getLogger(Admin.class);
    
    // Selectors
    private static final String ADMIN_BUTTON = "//span[@class='rmText rmExpandDown' and text()='Admin']";
    private static final String APPLICATION_LOGS = "//a[@title='Application Logs']//span[@class='rmText' and text()='Application Logs']";
    private static final String CLOSE_ICON = "//a[@class='rwCloseButton' and @title='Close']";  
    private static final String APPLICATION_ROLES = "//a[@title='Application Roles and Work Group']//span[@class='rmText' and text()='Application Roles and Work Groups']";
    private static final String USER_MANAGEMENT = "//a[@title='User Management']//span[@class='rmText' and text()='User Management']";
    private static final String Work_Group_Office_Information = "//a[@title='Work Group Office Information']//span[@class='rmText' and text()='Work Group Office Information']";
    private static final String Contracting_Company_Details = "//a[@title='Contracting Company Details']//span[@class='rmText' and text()='Contracting Company Details']";
    private static final String Contracting_Price_Agreements = "//a[@title='Contracting Price Agreements']//span[@class='rmText' and text()='Contracting Price Agreements']";  
    private static final String Dropdowns = "//a[@title='Dropdowns']//span[@class='rmText' and text()='Dropdowns']";
    private static final String Configure_Distribution_Lists = "//a[@title='Configure Distribution Lists']//span[@class='rmText' and text()='Configure Distribution Lists']";
    private static final String Estimation_Cost_Factors = "//a[@title='Estimation Cost Factors']//span[@class='rmText' and text()='Estimation Cost Factors']";
    private static final String Expected_Work_Duration = "//a[@title='Expected Work Duration']//span[@class='rmText' and text()='Expected Work Duration']";
    private static final String Delete_Projects_and_Files = "//a[@title='Delete Projects and Files']//span[@class='rmText' and text()='Delete Projects and Files']";
    private static final String Move_File_Panel = "//a[@title='Move File Panel']//span[@class='rmText' and text()='Move File Panel']";
    private static final String Canned_Report = "//a[@title='Canned Report']//span[@class='rmText' and text()='Canned Report']";
    private static final String Bulk_Upload_Documents = "//a[@title='Bulk Upload Documents']//span[@class='rmText' and text()='Bulk Upload Documents']";
    private static final String Bulk_Generate_Documents = "//a[@title='Bulk Generate Documents']//span[@class='rmText' and text()='Bulk Generate Documents']";
    private static final String Document_Metadata_Attributes = "//a[@title='Document Metadata Attributes']//span[@class='rmText' and text()='Document Metadata Attributes']";
    private static final String Document_Packages = "//a[@title='Document Packages']//span[@class='rmText' and text()='Document Packages']";
    private static final String Document_Type_Configuration = "//a[@title='Document Type Configuration']//span[@class='rmText' and text()='Document Type Configuration']";
    private static final String Template_and_Clause_Maintenance = "//a[@title='Template and Clause Maintenance']//span[@class='rmText' and text()='Template and Clause Maintenance']";
    private static final String Civil_Certification_Board_Approval = "//a[@title='Civil Certification / Board Approval']//span[@class='rmText' and text()='Civil Certification / Board Approval']";
    private static final String Production_Plans = "//a[@title='Production Plans']//span[@class='rmText' and text()='Production Plans']";
    private static final String QA_Data = "//a[@title='QA Data']//span[@class='rmText' and text()='QA Data']";
    private static final String Help_Content = "//a[@title='Help Content']//span[@class='rmText' and text()='Help Content']";
    private static final String FlairBOT_Library = "//a[@title='FlairBOT Library']//span[@class='rmText' and text()='FlairBOT Library']";
    private static final String Workflow_Activity = "//a[@title='Workflow Activity']//span[@class='rmText' and text()='Workflow Activity']";
    private static final String Notifications_Configuration = "//a[@title='Notifications Configuration']//span[@class='rmText' and text()='Notifications Configuration']";
    private static final String Workflow_Designer = "//a[@title='Workflow Designer']//span[@class='rmText' and text()='Workflow Designer']";
    private static final String Environment_Copy = "//a[@title='Environment Copy']//span[@class='rmText' and text()='Environment Copy']";
    private static final String Application_Configuration = "//a[@title='Application Configuration']//span[@class='rmText' and text()='Application Configuration']";
    private static final String Checklist_Configuration = "//a[@title='Checklist Configuration']//span[@class='rmText' and text()='Checklist Configuration']";
    

    




    String[][] adminItems = {
        {APPLICATION_LOGS, "popup"},
        {APPLICATION_ROLES, "navigation"},
        {USER_MANAGEMENT, "navigation"},
        {Work_Group_Office_Information, "navigation"},
        {Contracting_Company_Details, "navigation"},
        {Contracting_Price_Agreements, "navigation"},
        {Dropdowns, "navigation"},
        {Configure_Distribution_Lists, "navigation"},
        {Estimation_Cost_Factors, "navigation"},
        {Expected_Work_Duration, "navigation"},
        {Delete_Projects_and_Files, "navigation"},
        {Move_File_Panel, "navigation"},
        {Canned_Report, "navigation"},
        {Bulk_Upload_Documents, "navigation"},
        {Bulk_Generate_Documents, "navigation"},
        {Document_Metadata_Attributes, "navigation"},
        {Document_Packages, "navigation"},
        {Document_Type_Configuration, "navigation"},
        {Template_and_Clause_Maintenance, "navigation"},
        {Civil_Certification_Board_Approval, "navigation"},
        {Production_Plans, "navigation"},
        {QA_Data, "navigation"},
        {Help_Content, "navigation"},
        {FlairBOT_Library, "navigation"},
        {Workflow_Activity, "navigation"},
        {Notifications_Configuration, "navigation"},
        {Workflow_Designer, "navigation"},
        {Environment_Copy, "navigation"},
        {Application_Configuration, "navigation"},
        {Checklist_Configuration, "navigation"}
        // Add more as needed
    };

    @Test
    public void setUpAndLogin() {
        ExtentTest test = extent.createTest("Admin Setup and Login");
        Playwright playwright = Playwright.create();
        test.info("Playwright launched");

        // Launch browser
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setSlowMo(1000));
        test.info("Browser launched");
        

        // Read viewport size from config.properties (fix property keys to match config file)
        int width = Integer.parseInt(ConfigReader.get("viewportWidth", "1280"));
        int height = Integer.parseInt(ConfigReader.get("viewportHeight", "800"));
        test.info("Viewport size set to: " + width + "x" + height);

        // Create new page with configurable viewport size
        page = browser.newPage(new Browser.NewPageOptions().setViewportSize(width, height));
        test.info("New page created with viewport size");

        // Navigate to login page
         String baseUrl = ConfigReader.get("baseUrl");
            page.navigate(baseUrl, new Page.NavigateOptions()
                    .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                    .setTimeout(80000));
            test.info("Navigated to login page: " + page.title());
            test.info("Page URL: " + page.url());


        // Wait for login form
        page.waitForSelector("#LoginFlairdocs_UserName");
        log.info("Login form is visible");
        test.info("Login form is visible");

        // Enter username and password
        String username = ConfigReader.get("username");
        String password = ConfigReader.get("password");
        page.fill("#LoginFlairdocs_UserName", username);
        page.fill("#LoginFlairdocs_Password", password);
        log.info("Entered username and password");
        test.info("Entered username and password");

        // Wait for the login button to be visible and enabled
        page.waitForSelector("#LoginFlairdocs_LoginButton", 
            new Page.WaitForSelectorOptions().setTimeout(20000).setState(WaitForSelectorState.VISIBLE));
        log.info("Login button is visible");
        test.info("Login button is visible");

        // Click the login button (force if needed)
        page.click("#LoginFlairdocs_LoginButton", new Page.ClickOptions().setForce(true));
        log.info("Clicked login button");
        test.info("Clicked login button");

        // Wait for page to load after login
        page.waitForLoadState();
        log.info("After login - Page title: " + page.title());
        test.info("After login - Page title: " + page.title());
        page.waitForTimeout(5000);
        page.waitForSelector(".header-image-container[title='Flairdocs']",
                new Page.WaitForSelectorOptions().setTimeout(10000));
        log.info("Flairdocs header is visible");
        test.info("Flairdocs header is visible");
      
  
        page.waitForSelector(ADMIN_BUTTON);
        page.click(ADMIN_BUTTON);
        test.info("Clicked Admin button");
        log.info("Clicked Admin button");
            
        
         for (int i = 0; i < adminItems.length; i++) {
                // Always open the Admin dropdown before each click
                page.waitForSelector(ADMIN_BUTTON);
                page.click(ADMIN_BUTTON);
                test.info("Clicked Admin button for item " + (i+1));
                String itemSelector = adminItems[i][0];
            String itemType     = adminItems[i][1];
            page.waitForSelector(itemSelector);
            page.click(itemSelector);
            test.info("Clicked dropdown item: " + itemSelector);
    
               
    
                page.waitForSelector(itemSelector);
                page.click(itemSelector);
                test.info("Clicked dropdown item: " + itemSelector);
                log.info("Clicked dropdown item: " + itemSelector);
    
                if ("popup".equals(itemType)) {
                    // Wait for popup and close it
                    page.waitForTimeout(4000);
                    page.waitForSelector(CLOSE_ICON);
                    page.click(CLOSE_ICON);
                    test.info("Closed popup for item " + (i+1));
                    log.info("Closed popup for item " + (i+1));
                } else if ("navigation".equals(itemType)) {
                    // Wait for navigation and go back
                    page.waitForTimeout(5000);
                    page.goBack();
                    test.info("Navigated back after visiting item " + (i+1));
                    log.info("Navigated back after visiting item " + (i+1));
                }
            }
        }

        // Special handling for the hover menu  
    
    
    @AfterClass
    public void tearDown() {
        if (page != null) page.close();
        if (browser != null) browser.close();
        log.info("Browser and page closed");
    }

}
