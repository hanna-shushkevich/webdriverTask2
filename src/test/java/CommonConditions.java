import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import driver.DriverManager;
import utils.ScreenshotUtility;

/**
Base class for all tests
 */
public class CommonConditions {

   //logs
    private static final Logger logger = LogManager.getLogger(CommonConditions.class);

     protected WebDriver driver;
     protected String currentTestName;

    @BeforeEach
    public void setUp() {
        currentTestName = extractTestName();
        logger.info("========================================");
        logger.info("Starting test: " + currentTestName);
        logger.info("========================================");
        
        driver = DriverManager.createRemoteDriver();
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
            }
        } catch (Exception e) {
            logger.error("Error during quitting driver: " + e.getMessage(), e);

            if (driver != null) {
                String screenshotPath = ScreenshotUtility.takeScreenshot(driver, currentTestName + "_CLEANUP_ERROR");
                if (screenshotPath != null) {
                    logger.error("Screenshot saved on quitting driver error: " + screenshotPath);
                }
            }
        }
        logger.info("Test completed: " + currentTestName);
        logger.info("========================================\n");
    }

    /**
     * Capture screenshot on assertion failure
     */
    protected void captureScreenshot(String stepName) {
        if (driver != null) {
            String screenshotPath = ScreenshotUtility.takeScreenshot(driver, currentTestName + "_" + stepName);
            if (screenshotPath != null) {
                logger.info("Screenshot captured for step: " + stepName + " at " + screenshotPath);
            }
        }
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