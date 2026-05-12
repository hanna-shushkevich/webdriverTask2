package pages;

import driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ScreenshotUtility;

/**
 * PageExecutionDecorator: Implements the Decorator Pattern for page object method execution.
 * 
 * Wraps page object method calls with additional behaviors:
 * - Execution timing and performance metrics
 * - Enhanced logging with step tracking
 * - Automatic error handling and exception logging
 * - Screenshot capture on failures
 * - Method result handling
 * 
 * This decorator allows cross-cutting concerns to be applied to page object interactions
 * without modifying the page objects themselves, following the Open/Closed Principle.
 */
public class PageExecutionDecorator {
    private static final Logger logger = LogManager.getLogger(PageExecutionDecorator.class);
    private final BasePage page;
    private final String pageName;
    private long methodStartTime;
    private int stepCounter = 0;


    /**
     * Constructor to initialize the decorator with a page object.
     * 
     * @param page The BasePage object to be decorated and monitored
     * @param pageName Name of the page for logging and identification purposes
     */
    public PageExecutionDecorator(BasePage page, String pageName) {
        this.page = page;
        this.pageName = pageName;
        logger.info("PageExecutionDecorator initialized for: " + pageName);
    }


    /**
     * Executes a page method with added behavior: logging, timing, error handling.
     * This method wraps any page operation and adds metrics and diagnostics.
     * 
     * Used for void methods (methods that don't return a value).
     * 
     * @param methodAction The page method to execute (typically a lambda/functional interface)
     * @param methodDescription Description of what the method does for logging
     * @throws Exception If the method execution fails after logging and metrics
     */
    public void executePageMethod(PageMethodAction methodAction, String methodDescription) throws Exception {
        stepCounter++;
        String stepLog = "Step " + stepCounter + " [" + pageName + "]: " + methodDescription;
        
        logger.info("═══════════════════════════════════════════════════════════");
        logger.info(stepLog);
        logger.info("═══════════════════════════════════════════════════════════");
        
        methodStartTime = System.currentTimeMillis();
        
        try {
            methodAction.execute();
            logExecutionSuccess(methodDescription);
        } catch (Exception e) {
            logExecutionFailure(methodDescription, e);
            throw e;
        }
    }

    /**
     * Executes a page method that returns a value with added behavior.
     * This method wraps page operations that return results and adds metrics and diagnostics.
     * 
     * Used for methods that return a value (boolean, String, List, etc.).
     * 
     * @param <T> The return type of the method
     * @param methodAction The page method to execute that returns a value
     * @param methodDescription Description of what the method does for logging
     * @return The result from the method execution
     * @throws Exception If the method execution fails after logging and metrics
     */
    public <T> T executePageMethodWithReturn(PageMethodActionWithReturn<T> methodAction, String methodDescription) throws Exception {
        stepCounter++;
        String stepLog = "Step " + stepCounter + " [" + pageName + "]: " + methodDescription;
        
        logger.info("═══════════════════════════════════════════════════════════");
        logger.info(stepLog);
        logger.info("═══════════════════════════════════════════════════════════");
        
        methodStartTime = System.currentTimeMillis();
        
        try {
            T result = methodAction.execute();
            logExecutionSuccess(methodDescription);
            return result;
        } catch (Exception e) {
            logExecutionFailure(methodDescription, e);
            throw e;
        }
    }


    /**
     * Captures a screenshot at the current test step.
     * Useful for documenting page state during test execution and debugging.
     * 
     * @param stepName Name/description of the step for the screenshot filename
     */
    public void captureStepScreenshot(String stepName) {
        try {
            String screenshotName = pageName + "_Step" + stepCounter + "_" + stepName;
            String screenshotPath = ScreenshotUtility.takeScreenshot(DriverManager.getDriver(), screenshotName);
            if (screenshotPath != null) {
                logger.info("Screenshot captured: " + screenshotPath);
            }
        } catch (Exception e) {
            logger.warn("Failed to capture screenshot: " + e.getMessage());
        }
    }


    private void logExecutionSuccess(String methodDescription) {
        long executionTime = System.currentTimeMillis() - methodStartTime;
        logger.info("✓ SUCCESS - " + methodDescription);
        logger.info("  Execution time: " + executionTime + "ms");
        logger.info("───────────────────────────────────────────────────────────\n");
    }


    private void logExecutionFailure(String methodDescription, Exception exception) {
        long executionTime = System.currentTimeMillis() - methodStartTime;
        logger.error("✗ FAILURE - " + methodDescription);
        logger.error("  Exception: " + exception.getClass().getSimpleName());
        logger.error("  Message: " + exception.getMessage());
        logger.error("  Execution time before failure: " + executionTime + "ms");
        logger.error("  Page: " + pageName);
        

        try {
            String failureScreenshot = pageName + "_Step" + stepCounter + "_FAILED";
            ScreenshotUtility.takeScreenshot(DriverManager.getDriver(), failureScreenshot);
            logger.error("  Failure screenshot captured for diagnostics");
        } catch (Exception screenshotException) {
            logger.debug("Could not capture failure screenshot: " + screenshotException.getMessage());
        }
        
        logger.error("───────────────────────────────────────────────────────────\n");
    }





    /**
     * Functional interface for page methods that perform actions without returning a value.
     * Used with executePageMethod() for void methods.
     */
    @FunctionalInterface
    public interface PageMethodAction {
        /**
         * Execute a page object method.
         * 
         * @throws Exception If the method execution fails
         */
        void execute() throws Exception;
    }


    /**
     * Functional interface for page methods that perform actions and return a value.
     * Used with executePageMethodWithReturn() for methods returning values.
     * 
     * @param <T> The type of value returned by the method
     */
    @FunctionalInterface
    public interface PageMethodActionWithReturn<T> {
        /**
         * Execute a page object method and return a result.
         * 
         * @return The result from the method execution
         * @throws Exception If the method execution fails
         */
        T execute() throws Exception;
    }
}

