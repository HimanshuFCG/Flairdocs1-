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

    protected static ExtentReports extent;
    protected static ExtentSparkReporter spark;
    protected ExtentTest test;

    static {
        extent = new ExtentReports();
        spark = new ExtentSparkReporter("reports/ExtentReport.html");
        extent.attachReporter(spark);
    }

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
    
        // Load config and OR properties
        try {
            fis = new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\java\\config\\config.properties");
            OR.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        try {
            fis = new FileInputStream("./src/test/resources/properties/log4j.properties");
            PropertyConfigurator.configure(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    
        try {
            OR.load(fis);
            log.info("OR.properties file loaded");
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        // Get the screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();
        log.info("Screen size detected: " + width + " x " + height);

        playwright = Playwright.create();
        String browserName = ConfigReader.get("browser", "chrome");

        if (browserName.equalsIgnoreCase("chrome") || browserName.equalsIgnoreCase("edge")) {
            browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                    .setHeadless(false)
                    .setArgs(Arrays.asList("--start-maximized"))
            );
            context = browser.newContext(
                new Browser.NewContextOptions()
                    .setViewportSize(null)
                    .setRecordVideoDir(java.nio.file.Paths.get("videos"))
            );
            log.info("Launched " + browserName + " maximized with viewportSize=null");
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
    }
    

    @BeforeMethod
    public void setUp(Method method) {
        test = extent.createTest(method.getName());
    }

    @AfterMethod
    public void tearDown() {
        if (page != null) page.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
        test.info("Browser closed");
        log.info("Browser closed");
    }

    public void click(String locatorKey){
        try {
            page.locator(OR.getProperty(locatorKey)).click();
            log.info("Clicking on an element: " + locatorKey);
            ExtentListeners.test.info("Clicking on an element: " + locatorKey);
        } catch (Throwable t) {
            log.error("Error while clicking on an element: " + t.getMessage());
            ExtentListeners.test.fail("Clicking on an element: " + locatorKey);
            Assert.fail(t.getMessage());
        }
    }

    public void type (String locatorKey, String value){
        try {
            page.locator(OR.getProperty(locatorKey)).fill(value);
            log.info("Typing in an Element: " + locatorKey+ " and entered the value as :" + value);
            ExtentListeners.test.info("Clicking on an Element :" +locatorKey);
        } catch (Throwable t) {
            log.error("Error while Typing in an Element: " + t.getMessage());
            ExtentListeners.test.fail("Error while Typing in an Element: " + t.getMessage());
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
        if (test != null) {
            test.info(message);
        }
    }

    @AfterSuite
    public void flushExtent() {
        if (extent != null) {
            extent.flush();
            log.info("Extent report flushed");
        }
    }

    public void login() {
        String baseUrl = ConfigReader.get("baseUrl");
        page.navigate(baseUrl, new Page.NavigateOptions()
                .setWaitUntil(com.microsoft.playwright.options.WaitUntilState.DOMCONTENTLOADED)
                .setTimeout(60000));
        page.waitForSelector("#LoginFlairdocs_UserName");
        page.fill("#LoginFlairdocs_UserName", ConfigReader.get("username"));
        page.fill("#LoginFlairdocs_Password", ConfigReader.get("password"));
        page.waitForSelector("#LoginFlairdocs_LoginButton", new Page.WaitForSelectorOptions().setTimeout(60000).setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
        page.click("#LoginFlairdocs_LoginButton", new Page.ClickOptions().setForce(true));
        page.waitForLoadState();
        page.waitForTimeout(5000);
        page.waitForSelector(".header-image-container[title='Flairdocs']", new Page.WaitForSelectorOptions().setTimeout(10000));
        page.waitForTimeout(2000);
        log.info("Login successful");
    }

}