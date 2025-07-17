package testcases;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import org.testng.annotations.Test;
import org.apache.log4j.Logger;
import pages.ExpandAllPanelsInAllTabsPage;

public class ExpandAllPanelsInAllTabsTest extends BaseTest {
    private static final Logger log = Logger.getLogger(ExpandAllPanelsInAllTabsTest.class);
    private ExpandAllPanelsInAllTabsPage expandPage;

    @Test
    public void expandAndCollapseAllPanelsInAllTabs() {
        ExtentTest test = extent.createTest("Expand and Collapse All Panels in All Tabs");
        try {
            login(); // Use login from BaseTest
            test.info("Logged in");
            log.info("Logged in");

            String domain = config.ConfigReader.get("domain");
            String project = config.ConfigReader.get("project");
            expandPage = new ExpandAllPanelsInAllTabsPage(page); // page from BaseTest
            test.info("Page object created");
            log.info("Page object created");

            expandPage.selectDomain(domain, test);
            test.info("Domain selected");
            log.info("Domain selected");
            expandPage.selectProject(project, test);
            test.info("Project selected");
            log.info("Project selected");
            expandPage.goToProjectDetails(test);
            test.info("Navigated to project details");
            log.info("Navigated to project details");

            expandPage.expandAndCollapseAllPanelsInAllTabs(test);
            test.pass("Successfully expanded and collapsed all panels in all tabs.");
            log.info("Successfully expanded and collapsed all panels in all tabs.");
        } catch (Exception e) {
            if (extent != null) test.fail("Test failed: " + e.getMessage());
            System.out.println("Test failed: " + e.getMessage());
            test.fail("Test failed: " + e.getMessage());
            log.error("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 