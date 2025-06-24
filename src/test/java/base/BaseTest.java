package base;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;
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
import extentlisteners.ExtentListeners;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class BaseTest {

    private Playwright playwright;
    public Browser browser;
    public Page page;

    private static Properties OR = new Properties();
    private static FileInputStream fis;
    public Logger log = Logger.getLogger(this.getClass());

    public static ThreadLocal<Page> threadPage = new ThreadLocal<>();

    protected static ExtentReports extent;
    protected static ExtentSparkReporter spark;

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
        try {
            fis = new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\java\\config\\config.properties");
            OR.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        log.info("Test Execution Started !!!");

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
    playwright = Playwright.create();
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

    public void navigate(Browser browser,String url){
        this.browser = browser;
        // Read viewport size from config.properties
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("src/main/java/config/config.properties"));
        } catch (Exception e) {
            System.out.println("Could not load config.properties, using default viewport size.");
        }
        int width = Integer.parseInt(props.getProperty("viewport.width", "1280"));
        int height = Integer.parseInt(props.getProperty("viewport.height", "800"));

        // Create new page with configurable viewport size
        page = browser.newPage(new Browser.NewPageOptions().setViewportSize(width, height));
        setPage(page); // Set the ThreadLocal for listener access
        page.navigate(url);
        log.info("Navigated to the URL: " + url);
    }
    
    // @AfterMethod
    // public void quit(){
    //     browser.close();
    //     log.info("Browser closed");
    //     playwright.close();
    //     log.info("Playwright closed");
    //     playwright.close();
    //     log.info("Playwright closed");
    // }

}




