package testcases;

import base.BaseTest;
import config.ConfigReader;
import org.testng.annotations.Test;
import pages.CompleteLoginPage;

public class CompleteLoginTest extends BaseTest {
    @Test
    public void completeLoginAndTabSelection() {
        try {
            // Login and ExtentTest setup are handled by BaseTest
            login();
            String domain = ConfigReader.get("domain");
            String project = ConfigReader.get("project");

            CompleteLoginPage completeLoginPage = new CompleteLoginPage(page);
            completeLoginPage.selectDomain(domain, test);
            completeLoginPage.selectProject(project, test);
            completeLoginPage.goToProjectDetails(test);

            // Click through the required tabs
            String[] tabNames = {
                "Assignments",
                "Liaison Files",
                "Estimates",
                "Contracting",
                "Authorization Management",
                "Issue Management",
                "Financials",
                    "Expropriation",
                "Checklist",
                "Utility Coordination",
                    "Railroad Coordination"

            };
            for (String tab : tabNames) {
                completeLoginPage.clickTab(tab, test);
            }
            test.pass("Successfully completed login and tab selection flow.");
        } catch (Exception e) {
            log.error("Test failed: " + e.getMessage());
            test.fail("Test failed: " + e.getMessage());
            System.out.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 