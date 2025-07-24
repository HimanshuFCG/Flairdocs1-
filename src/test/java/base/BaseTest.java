package base;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeSuite;
import java.io.IOException;
import com.microsoft.playwright.BrowserType;
import org.testng.annotations.AfterMethod;  
import org.testng.Assert;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import extentlisteners.ExtentListeners;
import org.testng.annotations.BeforeMethod;
import java.lang.reflect.Method;
import com.aventstack.extentreports.ExtentTest;
import org.testng.annotations.AfterSuite;
import config.ConfigReader;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Arrays;

public class BaseTest {

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    private static Properties OR = new Properties();
    private static FileInputStream fis;
    public Logger log = Logger.getLogger(this.getClass());

    public static ThreadLocal<Page> threadPage = new ThreadLocal<>();


    public ExtentTest test; // Made public for ExtentListeners access


    public static void setPage(Page p) {
        threadPage.set(p);
    }

    public static Page getPage() {
        return threadPage.get();
    }

    @BeforeSuite
    public void setup() {
        PropertyConfigurator.configure("log4j.properties");
        log.info("Test Execution Started");

        // Load OR.properties (Object Repository) for selectors
        try {
            fis = new FileInputStream("./src/test/resources/properties/OR.properties");
            OR.load(fis);
            log.info("OR.properties file loaded");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fis = new FileInputStream("./src/test/resources/properties/log4j.properties");
            PropertyConfigurator.configure(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Get the screen size (from your old BaseTest)
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();
        log.info("Screen size detected: " + width + " x " + height);

        // COMMENTED OUT: Docker detection logic
        // String envDocker = System.getenv("DOCKER");
        // java.io.File dockerEnv = new java.io.File("/.dockerenv");
        // boolean headless = (envDocker != null || dockerEnv.exists());

        playwright = Playwright.create();
        String browserName = ConfigReader.get("browser", "chrome");

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
    }

  
    @AfterSuite
    public void tearDownSuite() {
        if (page != null) page.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    
        log.info("Browser and Playwright closed after suite");
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
    }
    

    protected void logStep(String message) {
        log.info(message);
        safeExtentLog(message);
    }

    public void login() {
        String baseUrl = ConfigReader.get("baseUrl");
        page.navigate(baseUrl, new Page.NavigateOptions()
                .setWaitUntil(com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED)
                .setTimeout(60000));
        page.waitForSelector(OR.getProperty("login.username"));
        page.fill(OR.getProperty("login.username"), ConfigReader.get("username"));
        page.fill(OR.getProperty("login.password"), ConfigReader.get("password"));
        page.waitForSelector(OR.getProperty("login.button"), new Page.WaitForSelectorOptions().setTimeout(60000).setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
        page.click(OR.getProperty("login.button"), new Page.ClickOptions().setForce(true));
        page.waitForLoadState();
        page.waitForTimeout(5000);
        page.waitForSelector(OR.getProperty("login.success.selector"), new Page.WaitForSelectorOptions().setTimeout(10000));
        page.waitForTimeout(2000);
        log.info("Login successful");
    }

    /**
     * Safely get the ExtentTest instance, with fallback to ExtentListeners
     */
    /**
     * Gets the ExtentTest instance, initializing it if necessary
     */
    public ExtentTest getExtentTest() {
        // First check if we already have a test instance
        if (test != null) {
            return test;
        }
        
        // Try to get from ThreadLocal in ExtentListeners
        test = extentlisteners.ExtentListeners.getExtent();
        
        // If still null, create a new ExtentTest directly
        if (test == null) {
            log.warn("ExtentTest is null in both BaseTest and ExtentListeners - creating new instance");
            ExtentReports extent = extentlisteners.ExtentManager.getInstance();
            if (extent != null) {
                String testName = this.getClass().getSimpleName() + "_" + Thread.currentThread().getId();
                test = extent.createTest(testName);
                extentlisteners.ExtentListeners.testReport.set(test);
                log.info("Created new ExtentTest instance: " + testName);
            } else {
                log.error("Cannot create ExtentTest - ExtentReports is not initialized");
            }
        } else {
            log.info("Retrieved ExtentTest from ExtentListeners ThreadLocal");
        }
        
        return test;
    }

    /**
     * Safe logging method that handles null ExtentTest
     */
    public void safeExtentLog(String message) {
        ExtentTest extentTest = getExtentTest();
        if (extentTest != null) {
            extentTest.info(message);
            log.debug("Successfully logged to ExtentTest: " + message);
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
            log.debug("Successfully logged PASS to ExtentTest: " + message);
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
            log.debug("Successfully logged FAIL to ExtentTest: " + message);
        } else {
            log.error("Cannot log fail to ExtentTest (null): " + message);
        }
    }
}