package testcases;

import base.BaseTest;
import config.ConfigReader;
import org.testng.annotations.Test;
import pages.ExpandPanelPage;

public class ExpandPanel extends BaseTest {

    @Test
    public void PanelExpansion() {
        try {
            login(); // Login handled in BaseTest
            log.info("Login completed");

            String domain = ConfigReader.get("domain");
            String project = ConfigReader.get("project");

            ExpandPanelPage expandPanelPage = new ExpandPanelPage(page);
            log.info("ExpandPanelPage initialized");

            expandPanelPage.selectDomain(domain, test);
            log.info("Domain selected: " + domain);

            expandPanelPage.selectProject(project, test);
            log.info("Project selected: " + project);

            expandPanelPage.goToProjectDetails(test);
            log.info("Navigated to project details");

            expandPanelPage.expandPanel(test);
            log.info("Panel expanded");

            expandPanelPage.collapsePanel(test);
            log.info("Panel collapsed");

        } catch (Exception e) {
            log.error("Test failed: " + e.getMessage(), e);
            test.fail("Test failed: " + e.getMessage());

            // Take screenshot on failure
            
            throw new RuntimeException("Test failed due to exception: ", e);
        }
    }
}



