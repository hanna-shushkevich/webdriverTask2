package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

/**
 * TestExecutionDecorator: Implements Decorator pattern for test execution tracking.
 * Wraps test execution with additional behaviors: logging, metrics, screenshots.
 */
public class TestExecutionDecorator {
    private static final Logger logger = LogManager.getLogger(TestExecutionDecorator.class);
    private final String testName;
    private final long startTime;
    private WebDriver driver;
    private int stepCount = 0;


    public TestExecutionDecorator(String testName) {
        this.testName = testName;
        this.startTime = System.currentTimeMillis();
        logger.info("==============================================");
        logger.info("===  Starting test: " + testName);
        logger.info("==============================================");
    }


    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }


    public void logStep(int stepNumber, String description) {
        stepCount++;
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

    /// Decorator added performance metrics to test run:
    public void logCompletion(String status) {
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("=== Test: " + testName + " - " + status);
        logger.info("=== Total steps executed: " + stepCount);
        logger.info("=== Execution time: " + executionTime + "ms");
        logger.info("===========================================\n");
    }
}
