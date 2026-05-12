package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

/**
 * TestExecutionHelper: Helper class for test execution tracking and metrics.
 * Provides logging, metrics capture, and screenshot functionality for tests.
 *
 * Note: This is a Helper/Service pattern, not a true Decorator pattern.
 * It supports test lifecycle management by offering utility methods for
 * structured logging, step tracking, and screenshot capture.
 */
public class TestExecutionHelper {
    private static final Logger logger = LogManager.getLogger(TestExecutionHelper.class);
    private final String testName;
    private final long startTime;
    private WebDriver driver;


    public TestExecutionHelper(String testName) {
        this.testName = testName;
        this.startTime = System.currentTimeMillis();
        logger.info("========================================");
        logger.info("Starting test: " + testName);
        logger.info("========================================");
    }


    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }


    public void logStep(int stepNumber, String description) {
        logger.info("Step " + stepNumber + ": " + description);
    }


    public void captureStepScreenshot(String stepName) {
        if (driver != null) {
            String screenshotPath = ScreenshotUtility.takeScreenshot(driver, testName + "_" + stepName);
            if (screenshotPath != null) {
                logger.info("Screenshot captured for step: " + stepName);
            }
        }
    }


    public void logCompletion(String status) {
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("Test: " + testName + " - " + status);
        logger.info("Execution time: " + executionTime + "ms");
        logger.info("========================================\n");
    }


}


