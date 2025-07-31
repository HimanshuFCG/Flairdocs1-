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
    private static String fileName;

    // Singleton access
    public static ExtentReports getInstance() {
        if (extent == null) {
            String defaultFileName = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + "_ExtentReport.html";
            createInstance(defaultFileName);
        }
        return extent;
    }

    public static ExtentReports createInstance(String fileNameParam) {
        fileName = fileNameParam;
        Path reportsDir = Paths.get(System.getProperty("user.dir"), "reports");
    
        try {
            if (!Files.exists(reportsDir)) {
                Files.createDirectories(reportsDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        String reportPath = reportsDir.resolve(fileName).toString();
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
    
        // Enhanced configuration
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle("Automation Report");
        sparkReporter.config().setEncoding("utf-8");
        sparkReporter.config().setReportName("FlairDocs Automation Results");
        sparkReporter.config().setTimelineEnabled(true);
        
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Tester", "Himanshu Batham");
        extent.setSystemInfo("Project", "CADOTV2");
        extent.setSystemInfo("Environment", "Demo");
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
    
        return extent;
    }

  
}
