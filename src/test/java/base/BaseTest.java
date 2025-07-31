package base;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import java.util.Properties;
import java.io.FileInputStream;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeSuite;
import java.io.IOException;
import com.microsoft.playwright.BrowserType;
import org.testng.annotations.AfterMethod;  
import org.testng.Assert;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import extentlisteners.ExtentListeners;
import extentlisteners.ExtentManager;

import org.testng.annotations.BeforeMethod;
import java.lang.reflect.Method;
import java.nio.file.Paths;

import com.aventstack.extentreports.ExtentTest;
import org.testng.annotations.AfterSuite;
import config.ConfigReader;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Arrays;
import com.microsoft.playwright.*;
import org.testng.annotations.*;
import org.testng.annotations.Listeners;






public class BaseTest {

    // Playwright components
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;
    

    // Thread-safe variables
    protected static ThreadLocal<Page> threadPage = new ThreadLocal<>();
    protected static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    // Logging
    private static final Logger log = Logger.getLogger(BaseTest.class);
    public   static Properties OR = new Properties();
    private static FileInputStream fis;
    protected ExtentTest extentTest;
    
    // Add this method to your BaseTest class
    public void setPage(Page page) {
        this.page = page;
        threadPage.set(page);
    }

    @BeforeSuite
    public void setup() {
        // Initialize ExtentReports
        ExtentManager.getInstance();
        PropertyConfigurator.configure("log4j.properties");
        log.info("Test Execution Started");

        // Load OR.properties (Object Repository)
        try {
            fis = new FileInputStream("./src/test/resources/properties/OR.properties");
            OR.load(fis);
            log.info("OR.properties file loaded");
        } catch (IOException e) {
            log.error("Error loading OR.properties", e);
        }

        // Get screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();
        log.info("Screen size detected: " + width + " x " + height);

        // Initialize Playwright
        playwright = Playwright.create();
        String browserName = ConfigReader.get("browser", "chrome");

        // Browser launch configuration
       
        if (browserName.equalsIgnoreCase("chrome") || browserName.equalsIgnoreCase("edge")) {
            browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                    .setHeadless(false) // Always headed for now
                    .setArgs(Arrays.asList("--start-maximized"))
            );
            context = browser.newContext(
                new Browser.NewContextOptions()
                    .setViewportSize(null) // Use your old approach
                    .setRecordVideoDir(java.nio.file.Paths.get("videos"))
            );
            log.info("Launched " + browserName + " maximized");
        } else if (browserName.equalsIgnoreCase("firefox")) {
            browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
            context = browser.newContext(
                new Browser.NewContextOptions()
                    .setViewportSize(width, height)
                    .setRecordVideoDir(java.nio.file.Paths.get("videos"))
            );
        } else if (browserName.equalsIgnoreCase("webkit")) {
            browser = playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
            context = browser.newContext(
                new Browser.NewContextOptions()
                    .setViewportSize(width, height)
                    .setRecordVideoDir(java.nio.file.Paths.get("videos"))
            );
        } else {
            browser = getBrowser(browserName);
            context = browser.newContext(
                new Browser.NewContextOptions()
                    .setViewportSize(width, height)
                    )
            ;
        }
        page = context.newPage();
        setPage(page); // Set the ThreadLocal for listener access
        // Perform login ONCE for the suite
        login();
        safeExtentPass("Login successful");
    }

    @BeforeMethod
    public void beforeMethod(Method method) {
        // Initialize ExtentTest for the test method
        String testName = method.getDeclaringClass().getSimpleName() + "." + method.getName();
        extentTest = ExtentManager.getInstance().createTest(testName);
        test.set(extentTest);
        log.info("Starting test: " + testName);
       safeExtentLog("Starting test: " + testName);
    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        
        try {
            // Log test status
            if (extentTest != null) {
                if (result.getStatus() == ITestResult.FAILURE) {
                    String errorMsg = "Test failed: " + result.getThrowable().getMessage();
                    extentTest.fail(errorMsg);
                    log.error(errorMsg);
                    
                    // Take screenshot on failure
                    try {
                        String screenshotPath = takeScreenshot(result.getName());
                        extentTest.addScreenCaptureFromPath(screenshotPath);
                        log.info("Screenshot captured: " + screenshotPath);
                    } catch (Exception e) {
                        log.error("Failed to capture screenshot", e);
                    }
                } else if (result.getStatus() == ITestResult.SUCCESS) {
                    extentTest.pass("Test passed");
                }
            }
            
            
            
        } catch (Exception e) {
            log.error("Error during test cleanup: " + e.getMessage(), e);
        } finally {
            // Don't close the page and context here, let them be managed by @AfterSuite
            // Just clean up the ThreadLocal variables
            test.remove();
            threadPage.remove();
        }
    }
    @AfterSuite
    public void tearDownSuite() {
        // Close browser and Playwright
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
        
        // Flush Extent Reports
        if (ExtentManager.getInstance() != null) {
            ExtentManager.getInstance().flush();
            log.info("Extent report has been generated");
        }
        log.info("Browser and Playwright closed after suite");
    }

    // Helper methods
    public static Page getPage() {
        return threadPage.get();
    }

    protected void logInfo(String message) {
        log.info(message);
        ExtentTest extentTest = test.get();
        if (extentTest != null) {
            extentTest.info(message);
        }
    }

    protected void logError(String message, Throwable throwable) {
        log.error(message, throwable);
        ExtentTest extentTest = test.get();
        if (extentTest != null) {
            extentTest.fail(message + ": " + throwable.getMessage());
        }
    }

    private String takeScreenshot(String testName) throws Exception {
        String screenshotPath = "screenshots/" + testName + "_" + System.currentTimeMillis() + ".png";
        page.screenshot(new Page.ScreenshotOptions()
            .setPath(Paths.get(screenshotPath))
            .setFullPage(true));
        return screenshotPath;
    }

    
    public void click(String locatorKey){
        try {
            page.locator(OR.getProperty(locatorKey)).click();
            log.info("Clicking on an element: " + locatorKey);
            safeExtentLog("Clicking on an element: " + locatorKey);
        } catch (Throwable t) {
            log.error("Error while clicking on an element: " + t.getMessage());
            safeExtentFail("Error while clicking on an element: " + t.getMessage());
            Assert.fail(t.getMessage());
        }
    }

    public void type (String locatorKey, String value){
        try {
            page.locator(OR.getProperty(locatorKey)).fill(value);
            log.info("Typing in an Element: " + locatorKey+ " and entered the value as :" + value);
            safeExtentLog("Typing in an Element :" +locatorKey);
        } catch (Throwable t) {
            log.error("Error while Typing in an Element: " + t.getMessage());
            safeExtentFail("Error while Typing in an Element: " + t.getMessage());
            Assert.fail(t.getMessage());
        }
    }


  public Browser getBrowser(String browserName){
    switch (browserName) {
        case "chrome":
        log.info("Launching Chrome Browser");
            return playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setChannel("chrome"));
            
        case "firefox":
            log.info("Launching Firefox Browser");
            return playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(false));
            
        case "edge":
            log.info("Launching Edge Browser");
            return playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setChannel("msedge"));
            
         case "webkit":
            log.info("Launching Webkit Browser");
            return playwright.webkit().launch(new BrowserType.LaunchOptions().setHeadless(false));
          case"headless":
            log.info("Launching Headless Browser");
            return playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        default:
        throw new IllegalArgumentException("Invalid browser name: " + browserName); //whenever wrong browser name is passed
    }
}

    public void navigate(Browser browser,String url) //for new windows that open in new tab
    {
        this.browser = browser;
        // Read viewport size from config.properties
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("src/main/java/config/config.properties"));
        } catch (Exception e) {
            System.out.println("Could not load config.properties, using default viewport size.");
        }
        String viewportWidth = props.getProperty("viewport.width");
        String viewportHeight = props.getProperty("viewport.height");
        safeExtentLog("Navigating to URL: " + url);
        log.info("Navigating to URL: " + url);
        int width, height;
        if (viewportWidth != null && !viewportWidth.isEmpty() &&
            viewportHeight != null && !viewportHeight.isEmpty()) {
            width = Integer.parseInt(viewportWidth);
            height = Integer.parseInt(viewportHeight);
        } else {
            width = 1280;
            height = 800;
        }
        // Create new page with configurable viewport size
        page = browser.newPage(new Browser.NewPageOptions().setViewportSize(width, height));
        setPage(page); // Set the ThreadLocal for listener access
        page.navigate(url);
        log.info("Navigated to the URL: " + url);
        safeExtentLog("Navigated to the URL: " + url);
    }
    

    protected void logStep(String message) {
        log.info(message);
        safeExtentLog(message);
    }

    public void login() {
       
       try {
        String baseUrl = ConfigReader.get("baseUrl");
        log.info("Navigating to base URL: " + baseUrl);
        safeExtentLog("Navigating to base URL: " + baseUrl);
    
        // Go to login page
        page.navigate(baseUrl, new Page.NavigateOptions()
            .setWaitUntil(com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED)
            .setTimeout(60000));
            log.info("Page title: " + page.title());
            safeExtentLog("Page title: " + page.title());
        // Wait for username field to be visible
        try{
        page.waitForSelector(OR.getProperty("login.username"),
            new Page.WaitForSelectorOptions().setTimeout(10000));
        log.info("Login page loaded: username field visible.");
        safeExtentLog("Login page loaded: username field visible.");
        }catch (Exception e) {
            log.error("Error while waiting for username field to be visible: " + e.getMessage());
            safeExtentFail("Error while waiting for username field to be visible: " + e.getMessage());
            Assert.fail(e.getMessage());
        }
        // Fill credentials
        page.fill(OR.getProperty("login.username"), ConfigReader.get("username"));
        page.fill(OR.getProperty("login.password"), ConfigReader.get("password"));
        log.info("Filled username and password.");
        safeExtentLog("Filled username and password.");
    
        // Wait for login button to be visible and enabled
        try{
        page.waitForSelector(OR.getProperty("login.button"),
            new Page.WaitForSelectorOptions().setTimeout(10000)
                   .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
        log.info("Login button is visible.");
        safeExtentLog("Login button is visible.");
                }catch (Exception e) {
            log.error("Error while waiting for login button to be visible: " + e.getMessage());
            safeExtentFail("Error while waiting for login button to be visible: " + e.getMessage());
            Assert.fail(e.getMessage());
        }
    
        // Perform the click and wait for navigation triggered by login
        page.waitForNavigation(
            new Page.WaitForNavigationOptions()
                .setTimeout(30000)
                .setWaitUntil(com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED),
            () -> {
                page.click(OR.getProperty("login.button"));
                log.info("Clicked login button.");
                safeExtentLog("Clicked login button.");
            }
        );
    
        log.info("Navigation after login completed.");
        safeExtentLog("Navigation after login completed.");
    
        // Wait for a selector that is ONLY available after login succeeds
        String postLoginSelector = OR.getProperty("login.success.selector");
        page.waitForSelector(postLoginSelector,
            new Page.WaitForSelectorOptions().setTimeout(20000)
                   .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
        log.info("Successfully logged in; found post-login selector: " + postLoginSelector);
        safeExtentLog("Successfully logged in; found post-login selector: " + postLoginSelector);   
           }catch (Exception e) {
            log.error("Error while logging in: " + e.getMessage());
            safeExtentFail("Error while logging in: " + e.getMessage());
            Assert.fail(e.getMessage());
        }   
    }
    
    
    public ExtentTest getExtentTest() {
        // Get the ExtentTest from ThreadLocal
        ExtentTest extentTest = test.get();
        if (extentTest == null) {
            log.warn("ExtentTest is null in ThreadLocal - creating new instance");
            ExtentReports extent = ExtentManager.getInstance();
            if (extent != null) {
                String testName = this.getClass().getSimpleName() + "_" + Thread.currentThread().getId();
                extentTest = extent.createTest(testName);
                test.set(extentTest);  // Store in ThreadLocal
                log.info("Created new ExtentTest instance: " + testName);
            } else {
                log.error("Cannot create ExtentTest - ExtentReports is not initialized");
            }
        }
        return extentTest;

    }
    public void safeExtentLog(String message) {
        ExtentTest extentTest = getExtentTest();
        if (extentTest != null) {
            extentTest.info(message);
            log.info("Successfully logged to ExtentTest: " + message);
        } else {
            log.warn("Cannot log to ExtentTest (null): " + message);
        }
    }

    /**
     * Safe extent pass method
     */
    public void safeExtentPass(String message) {
        ExtentTest extentTest = getExtentTest();
        if (extentTest != null) {
            extentTest.pass(message);
            log.info("Successfully logged PASS to ExtentTest: " + message);
        } else {
            log.warn("Cannot log pass to ExtentTest (null): " + message);
        }
    }

    /**
     * Safe extent fail method
     */
    public void safeExtentFail(String message) {
        ExtentTest extentTest = getExtentTest();
        if (extentTest != null) {
            extentTest.fail(message);
            log.info("Successfully logged FAIL to ExtentTest: " + message);
        } else {
            log.error("Cannot log fail to ExtentTest (null): " + message);
        }
    }

    public static void setExtentTest(ExtentTest extentTest) {
        test.set(extentTest);

    }


   
   
}