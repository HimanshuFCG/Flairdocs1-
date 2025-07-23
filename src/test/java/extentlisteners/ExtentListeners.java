package extentlisteners;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.markuputils.*;

import base.BaseTest;
import utilities.MonitoringMail;
import utilities.TestConfig;
import utilities.screenshotUtils.ScreenshotUtils;

import org.testng.*;
import org.testng.annotations.*;
import org.testng.xml.XmlSuite;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import org.apache.log4j.Logger;

public class ExtentListeners implements ITestListener, ISuiteListener {

	static Date d = new Date();
	static String fileName = "Extent_" + d.toString().replace(":", "_").replace(" ", "_") + ".html";

	private static ExtentReports extent;
	public static ExtentTest test;
	public static ThreadLocal<ExtentTest> testReport = new ThreadLocal<>();
	private static final Logger log = Logger.getLogger(ExtentListeners.class);
	
	public static ExtentTest getExtent() {
		
		return testReport.get();
	}

	@Override
	public void onStart(ISuite suite) {
		// Initialize ExtentReports singleton for the suite
		extent = ExtentManager.createInstance(fileName);
		log.info("ExtentReports initialized in onStart(ISuite)");
	}

	@Override
	public void onFinish(ISuite suite) {
		if (extent != null) {
			extent.flush();
			log.info("Extent report flushed on suite finish");
		}
		// Email logic (unchanged)
		MonitoringMail mail = new MonitoringMail();
		String messageBody = null;
		try {
			messageBody = "http://" + java.net.InetAddress.getLocalHost().getHostAddress()
					+ ":8080/job/PlaywrightProject/Extent_20Reports/";
		} catch (java.net.UnknownHostException e) {
			log.error("UnknownHostException while getting local host address", e);
		}
		try {
			mail.sendMail(utilities.TestConfig.server, utilities.TestConfig.from, utilities.TestConfig.to, utilities.TestConfig.subject, messageBody);
		} catch (javax.mail.internet.AddressException e) {
			log.error("AddressException while sending mail", e);
		} catch (javax.mail.MessagingException e) {
			log.error("MessagingException while sending mail", e);
		}
	}

	@Override
	public void onTestStart(ITestResult result) {
		test = extent.createTest(
			result.getTestClass().getName() + "     @TestCase : " + result.getMethod().getMethodName()
		);
		testReport.set(test);
		
		// Connect BaseTest.test to this ExtentTest instance
		if (result.getInstance() instanceof base.BaseTest) {
			((base.BaseTest) result.getInstance()).test = test;
		}
	}

	@Override
	public void onTestSuccess(ITestResult result) {

		String methodName = result.getMethod().getMethodName();
		String logText = "<b>" + "TEST CASE:- " + methodName.toUpperCase() + " PASSED" + "</b>";
		Markup m = MarkupHelper.createLabel(logText, ExtentColor.GREEN);
		getExtent().pass(m);

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

		if (screenshotPath != null) {
			getExtent().fail("<b><font color=red>Screenshot of failure</font></b><br>",
				MediaEntityBuilder.createScreenCaptureFromPath(screenshotPath).build());
		}

		Markup m = MarkupHelper.createLabel(logText, ExtentColor.RED);
		getExtent().log(Status.FAIL, m);
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		String methodName = result.getMethod().getMethodName();
		String logText = "<b>" + "Test Case:- " + methodName + " Skipped" + "</b>";
		Markup m = MarkupHelper.createLabel(logText, ExtentColor.YELLOW);
		getExtent().skip(m);

	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		// Not used
	}

	@Override
	public void onStart(ITestContext context) {
		// Not used
	}

	@Override
	public void onFinish(ITestContext context) {
		// Not used
	}
}