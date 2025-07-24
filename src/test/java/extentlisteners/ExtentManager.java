package extentlisteners;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.microsoft.playwright.Page;

import base.BaseTest;

import java.io.IOException;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentManager {

	private static ExtentReports extent;
	public static String fileName;

	// Singleton getter for ExtentReports
	public static ExtentReports getInstance() {
		if (extent == null) {
			// Default file name if not set
			if (fileName == null) {
				Date d = new Date();
				fileName = "Extent_" + d.toString().replace(":", "_").replace(" ", "_") + ".html";
			}
			createInstance(fileName);
		}
		return extent;
	}

	public static ExtentReports createInstance(String fileNameParam) {
		fileName = fileNameParam;
		// Ensure the reports directory exists in the project root
		Path reportsDir = Paths.get(System.getProperty("user.dir"), "reports");
		try {
			if (!Files.exists(reportsDir)) {
				Files.createDirectories(reportsDir);
			}
		} catch (IOException e) {
			// Use logger if available, else print stack trace
			e.printStackTrace();
		}

		String reportFilePath = Paths.get(System.getProperty("user.dir"), "reports", fileName).toString();
		ExtentSparkReporter htmlReporter = new ExtentSparkReporter(reportFilePath);

		htmlReporter.config().setTheme(Theme.STANDARD);
		htmlReporter.config().setDocumentTitle(fileName);
		htmlReporter.config().setEncoding("utf-8");
		htmlReporter.config().setReportName(fileName);

		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		extent.setSystemInfo("Automation Tester", "Himanshu Batham");
		extent.setSystemInfo("FCG", "FlairdocsAutomation");
		extent.setSystemInfo("DemoProject", "DOTV2");

		return extent;
	}

	public static void captureScreenshot() throws IOException {
		Date d = new Date();
		fileName = d.toString().replace(":", "_").replace(" ", "_") + ".jpg";
		BaseTest.getPage().screenshot(new Page.ScreenshotOptions().setPath(Paths.get("./reports/" + fileName)));
	}

}