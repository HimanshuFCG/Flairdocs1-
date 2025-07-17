package testcases;

import base.BaseTest;
import config.ConfigReader;
import org.testng.annotations.Test;
import com.aventstack.extentreports.ExtentTest;
import utilities.PanelActions;
import pages.CompleteLoginAndPanelPage;
import org.apache.log4j.Logger;


public class CompleteLoginAndPanelTest extends BaseTest {
    private static final Logger log = Logger.getLogger(CompleteLoginAndPanelTest.class);

    @Test
    public void loginAndOpenClosePanels() {
        ExtentTest test = extent.createTest("Complete Login And Panel Test");
        try {
            login();
            test.info("Logged in using BaseTest.login()");
            log.info("Logged in using BaseTest.login()");

            String domain = ConfigReader.get("domain");
            String project = ConfigReader.get("project");

            CompleteLoginAndPanelPage completePage = new CompleteLoginAndPanelPage(page);
            test.info("Page object created");
            log.info("Page object created");

            completePage.selectDomain(domain, test);
            test.info("Domain selected");
            log.info("Domain selected");
            completePage.selectProject(project, test);
            test.info("Project selected");
            log.info("Project selected");
            completePage.goToProjectDetails(test);
            test.info("Navigated to project details");
            log.info("Navigated to project details");

            String[] panelTitles = {
                "Project Information", "Project Status Log", "Right of Way Maps", "External Agreement","Authorization Summary","Sales Book","Certification","Import","All Project Information Documents"
            };
            PanelActions panelActions = new PanelActions(page);
            for (String panelTitle : panelTitles) {
                try {
                    test.info("Opening panel: " + panelTitle);
                    log.info("Opening panel: " + panelTitle);
                    panelActions.openPanel(panelTitle);
                    test.info("Opened panel: " + panelTitle);
                    log.info("Opened panel: " + panelTitle);
                    panelActions.closePanel(panelTitle);
                    test.info("Closed panel: " + panelTitle);
                    log.info("Closed panel: " + panelTitle);
                } catch (Exception e) {
                    test.fail("Error handling panel: " + panelTitle + " - " + e.getMessage());
                    log.error("Error handling panel: " + panelTitle + " - " + e.getMessage());
                }
            }
            test.pass("Panels opened and closed successfully.");
            log.info("Panels opened and closed successfully.");
        } catch (Exception e) {
            test.fail("Test failed: " + e.getMessage());
            log.error("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
