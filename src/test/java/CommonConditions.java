import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.WebDriver;
import driver.DriverManager;
import utils.ScreenshotUtility;
import utils.TestExecutionDecorator;

/**
 * CommonConditions: Base class for all tests.
 * Responsibility: Test lifecycle management (setup, teardown).
 * Uses TestExecutionDecorator for cross-cutting concerns (logging, metrics, screenshots).
 * Decorator pattern: wraps test lifecycle with additional behaviors.
 */
public class CommonConditions {

    private static final Logger logger = LogManager.getLogger(CommonConditions.class);

    protected WebDriver driver;
    protected String currentTestName;
    protected TestExecutionDecorator testDecorator;

    @BeforeEach
    public void setUp(TestInfo testInfo) {
        currentTestName = testInfo.getDisplayName();
        // Initialize decorator for test execution tracking with logging and metrics
        testDecorator = new TestExecutionDecorator(currentTestName);

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
     * Capture screenshot on assertion failure.
     * Uses TestExecutionDecorator for consistent logging.
     */
    protected void captureScreenshot(String stepName) {
        testDecorator.captureStepScreenshot(stepName);
    }

    /**
     * Log test step with automatic formatting.
     * Uses TestExecutionDecorator for consistent logging.
     */
    protected void logStep(int stepNumber, String description) {
        testDecorator.logStep(stepNumber, description);
    }
    /// Why I removed method  private String extractTestName() ?
    /// because it did not work for 2 reasons:
    /// 1. When using it in setUp() our test method was not in stack jet as setUp() is called before
    /// 2. We tried to find method beginning with test... but seleniumGridTest() was named not properly

}
