package extentlisteners;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.markuputils.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.IReporter;
import org.testng.xml.XmlSuite;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.testng.ISuite;

public class ExtentListeners implements ITestListener, IReporter {

    static Date d = new Date();
    static String fileName = "Extent_" + d.toString().replace(":", "_").replace(" ", "_") + ".html";

    private static ExtentReports extent;
    public static ExtentTest test;
    public static ThreadLocal<ExtentTest> testReport = new ThreadLocal<>();
    private static final Logger log = Logger.getLogger(ExtentListeners.class);

    /**
     * Gets the ExtentTest instance from ThreadLocal, or creates a new one if needed
     */
    public static ExtentTest getExtent() {
        ExtentTest test = testReport.get();
        if (test == null) {
            log.warn("ExtentTest is null in ThreadLocal, creating a new one");
            if (extent != null) {
                test = extent.createTest("DynamicTest_" + Thread.currentThread().getId());
                testReport.set(test);
            }
        }
        return test;
    }

    @Override
    public void onStart(ITestContext context) {
        log.info("Starting test context: " + context.getName());
        // Initialize ExtentReports if not already done
        if (extent == null) {
            extent = ExtentManager.getInstance();
            log.info("ExtentReports initialized in onStart");
        }
    }
    
    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        System.out.println("=== Starting report generation ===");
        System.out.println("Extent instance: " + (extent != null ? "exists" : "null"));
        
        // Final flush of the report
        if (extent != null) {
            try {
                System.out.println("Flushing Extent report...");
                extent.flush();
                System.out.println("Extent report flushed successfully");
                log.info("Extent report generated and flushed");
            } catch (Exception e) {
                System.err.println("Error while flushing Extent report: " + e.getMessage());
                log.error("Error while flushing Extent report", e);
            }
        } else {
            System.err.println("ExtentReports instance is null, cannot generate report");
            log.warn("ExtentReports instance is null, cannot generate report");
        }
        System.out.println("=== Report generation completed ===");
    }

    @Override
    public void onTestStart(ITestResult result) {
        log.info("=== ExtentListeners.onTestStart() CALLED ===");
        log.info("Test Method: " + result.getMethod().getMethodName());
        log.info("Test Class: " + result.getTestClass().getName());
        
        try {
            // Ensure ExtentReports is initialized
            if (extent == null) {
                log.warn("ExtentReports was null, initializing via ExtentManager");
                extent = ExtentManager.getInstance();
                log.info("ExtentReports initialized in onTestStart");
            }
            
            // Create test in report
            String testName = result.getTestClass().getName() + "_" + result.getMethod().getMethodName();
            test = extent.createTest(testName);
            
            // Set in ThreadLocal
            testReport.set(test);
            log.info("ExtentTest created and set in ThreadLocal: " + (test != null ? testName : "FAILED"));

            // Set in test instance if it's a BaseTest
            Object testInstance = result.getInstance();
            if (testInstance != null) {
                log.info("Test instance type: " + testInstance.getClass().getName());
                
                if (testInstance instanceof base.BaseTest) {
                    ((base.BaseTest) testInstance).test = test;
                    log.info("ExtentTest assigned to BaseTest.test field: SUCCESS");
                } else {
                    log.warn("Test instance is NOT an instance of BaseTest! Type: " + testInstance.getClass().getName());
                }
            } else {
                log.warn("Test instance is null in onTestStart");
            }
        } catch (Exception e) {
            log.error("Error in onTestStart: " + e.getMessage(), e);
            // Try to recover by ensuring we have at least a minimal ExtentTest
            if (extent != null) {
                test = extent.createTest("RecoveryTest_" + System.currentTimeMillis());
                testReport.set(test);
            }
        }
        
        log.info("=== ExtentListeners.onTestStart() COMPLETED ===");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String logText = "<b>" + "TEST CASE:- " + methodName.toUpperCase() + " PASSED" + "</b>";
        Markup m = MarkupHelper.createLabel(logText, ExtentColor.GREEN);
        ExtentTest extentTest = getExtent();
        if (extentTest != null) {
            extentTest.pass(m);
            log.info("Test passed: " + methodName);
        } else {
            log.error("ExtentTest is null in onTestSuccess for method: " + methodName);
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String screenshotPath = null;
        try {
            screenshotPath = utilities.screenshotUtils.ScreenshotUtils.takeScreenshot(base.BaseTest.getPage(), methodName);
        } catch (Exception e) {
            log.error("Exception while taking screenshot on failure", e);
        }

        String logText = "<b>" + "TEST CASE:- " + methodName.toUpperCase() + " FAILED" + "</b>";
        ExtentTest extentTest = getExtent();
        
        if (extentTest != null) {
            if (screenshotPath != null) {
                try {
                    extentTest.fail("<b><font color=red>Screenshot of failure</font></b><br>" +
                        "<a href='" + screenshotPath + "'>View Screenshot</a>");
                } catch (Exception e) {
                    log.error("Failed to add screenshot to report", e);
                }
            }

            // Log the failure
            Markup m = MarkupHelper.createLabel(logText, ExtentColor.RED);
            extentTest.log(Status.FAIL, m);
            
            // Log the exception if present
            if (result.getThrowable() != null) {
                extentTest.fail(result.getThrowable());
            }
            
            log.error("Test failed: " + methodName, result.getThrowable());
        } else {
            log.error("ExtentTest is null in onTestFailure for method: " + methodName);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String logText = "<b>" + "Test Case:-" + methodName + " Skipped" + "</b>";
        Markup m = MarkupHelper.createLabel(logText, ExtentColor.YELLOW);
        ExtentTest extentTest = getExtent();
        if (extentTest != null) {
            extentTest.skip(m);
            if (result.getThrowable() != null) {
                extentTest.skip(result.getThrowable());
            }
            log.warn("Test skipped: " + methodName);
        } else {
            log.error("ExtentTest is null in onTestSkipped for method: " + methodName);
        }
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // Not used
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("Finishing test context: " + context.getName());
        // Log suite results
        log.info("Passed tests: " + context.getPassedTests().size());
        log.info("Failed tests: " + context.getFailedTests().size());
        log.info("Skipped tests: " + context.getSkippedTests().size());
    }
}
