package testcases;
import org.testng.annotations.Test;
import base.BaseTest;
import org.apache.log4j.Logger;
import pages.CreateNewFilePage;
import com.microsoft.playwright.Frame;
import config.ConfigReader;
import com.aventstack.extentreports.ExtentTest;

public class CreateNewFile extends BaseTest {
    private static final Logger log = Logger.getLogger(CreateNewFile.class);

    @Test
    public void loginAndOpenClosePanels() {
        try {
            // Use BaseTest's login and page
            login();
            ExtentTest extentTest = getExtentTest();
            log.info("Logged in using BaseTest.login()");
            extentTest.info("Logged in using BaseTest.login()");

            // --- Use POM for Create New File flow ---
            CreateNewFilePage createNewFilePage = new CreateNewFilePage(page);
            String domain = ConfigReader.get("domain");
            String project = ConfigReader.get("project");
            String rowId = ConfigReader.get("rowId");
            String erowId = ConfigReader.get("erowId");

            createNewFilePage.selectDomain(domain, extentTest);
            createNewFilePage.selectProject(project, extentTest);
            createNewFilePage.clickCreateNewFile(extentTest);
            Frame frame = createNewFilePage.switchToCreateFileIframe(extentTest);
            createNewFilePage.fillCreateFileForminfo(frame, rowId, extentTest);
            page.waitForTimeout(10000);

            boolean found = createNewFilePage.isRowPresent(rowId, extentTest);
            if (!found) {
                log.info("Could not find row with ROW ID: " + rowId);
                safeExtentFail("Could not find row with ROW ID: " + rowId);
            }
        } catch (Exception e) {
            log.error("Test failed: " + e.getMessage());
            safeExtentFail("Test failed: " + e.getMessage());
            System.out.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
