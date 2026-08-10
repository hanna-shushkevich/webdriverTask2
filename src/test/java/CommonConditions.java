import com.epam.reportportal.junit5.ReportPortalExtension;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import driver.DriverManager;
import utils.ReportPortalScreenshotExtension;
import utils.ScreenshotUtility;
import utils.TestExecutionHelper;

@ExtendWith({ReportPortalExtension.class, ReportPortalScreenshotExtension.class})
public class CommonConditions {

    private static final Logger logger = LogManager.getLogger(CommonConditions.class);

    protected WebDriver driver;
    protected String currentTestName;
    protected TestExecutionHelper testDecorator;

    @BeforeEach
    public void setUp() {
        currentTestName = extractTestName();
        // Initialize helper for test execution tracking with logging and metrics
        testDecorator = new TestExecutionHelper(currentTestName);
        
        driver = DriverManager.createRemoteDriver();
        testDecorator.setDriver(driver);
        logger.info("WebDriver started");
    }

    @AfterEach
    public void stopBrowser() {
        try {
            logger.info("Tearing down test: " + currentTestName);
            
            if (driver != null) {
                logger.info("Closing WebDriver");
                DriverManager.quitDriver();
                logger.info("WebDriver closed successfully");
                testDecorator.logCompletion("PASSED");
            }
        } catch (Exception e) {
            logger.error("Error during quitting driver: " + e.getMessage(), e);
            testDecorator.logCompletion("FAILED");

            if (driver != null) {
                String screenshotPath = ScreenshotUtility.takeScreenshot(driver, currentTestName + "_CLEANUP_ERROR");
                if (screenshotPath != null) {
                    logger.error("Screenshot saved on quitting driver error: " + screenshotPath);
                }
            }
        }
    }

    /**
     * Capture screenshot on assertion failure
     * Uses TestExecutionHelper for consistent logging
     */
    protected void captureScreenshot(String stepName) {
        testDecorator.captureStepScreenshot(stepName);
    }

    /**
     * Log test step with automatic formatting
     * Uses TestExecutionHelper for consistent logging
     */
    protected void logStep(int stepNumber, String description) {
        testDecorator.logStep(stepNumber, description);
    }

    /**
     * Extract current test name from stack trace
     *
     * @return Current test method name
     */
    private String extractTestName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTrace) {
            if (element.getMethodName().startsWith("test")) {
                return element.getMethodName();
            }
        }
        return "UnknownTest";
    }
}