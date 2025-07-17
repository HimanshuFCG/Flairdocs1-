package testcases;
import org.testng.annotations.Test;
import base.BaseTest;
import org.apache.log4j.Logger;
import pages.CreateNewFilePage;
import com.microsoft.playwright.Frame;
import config.ConfigReader;

public class CreateNewFile extends BaseTest {
    private static final Logger log = Logger.getLogger(CreateNewFile.class);

    @Test
    public void loginAndOpenClosePanels() {
        try {
            // Use BaseTest's login and page
            login();
            log.info("Logged in using BaseTest.login()");
            test.info("Logged in using BaseTest.login()");

            // --- Use POM for Create New File flow ---
            CreateNewFilePage createNewFilePage = new CreateNewFilePage(page);
            String domain = ConfigReader.get("domain");
            String project = ConfigReader.get("project");
            String rowId = ConfigReader.get("rowId");
            String erowId = ConfigReader.get("erowId");

            createNewFilePage.selectDomain(domain, test);
            createNewFilePage.selectProject(project, test);
            createNewFilePage.clickCreateNewFile(test);
            Frame frame = createNewFilePage.switchToCreateFileIframe(test);
            createNewFilePage.fillCreateFileForm(frame, rowId, erowId, test);
            page.waitForTimeout(10000);

            boolean found = createNewFilePage.isRowPresent(rowId, test);
            if (!found) {
                log.info("Could not find row with ROW ID: " + rowId);
                test.fail("Could not find row with ROW ID: " + rowId);
            }
        } catch (Exception e) {
            log.error("Test failed: " + e.getMessage());
            test.fail("Test failed: " + e.getMessage());
            System.out.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
