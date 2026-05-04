package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ScreenshotUtility: Handles screenshot capture for test failure diagnostics.
 * Creates timestamped screenshot files in a centralized directory.
 *
 * Usage:
 *  ScreenshotUtility.takeScreenshot(driver, "LoginFailed");
 *  Returns path to saved screenshot
 */
public class ScreenshotUtility {
    private static final Logger logger = LogManager.getLogger(ScreenshotUtility.class);
    private static final String SCREENSHOTS_DIR = "screenshots";

    static {
        // Ensure screenshots directory exists
        try {
            Files.createDirectories(Paths.get(SCREENSHOTS_DIR));
            logger.debug("Screenshots directory ready: " + SCREENSHOTS_DIR);
        } catch (IOException e) {
            logger.error("Failed to create screenshots directory: " + e.getMessage());
        }
    }

    /**
     * Take screenshot with automatic timestamp and optional test name
     *
     * @param driver   WebDriver instance
     * @param testName Optional test name for better identification
     * @return Path to saved screenshot file, or null if capture failed
     */
    public static String takeScreenshot(WebDriver driver, String testName) {
        if (driver == null) {
            logger.warn("Cannot take screenshot: WebDriver is null");
            return null;
        }

        try {
            // Generate timestamp-based filename
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date());
            String filename = String.format("%s_%s_%s.png", 
                    testName != null ? testName : "screenshot",
                    timestamp,
                    System.nanoTime() % 1000); // Add nanotime for uniqueness in parallel execution

            String filePath = Paths.get(SCREENSHOTS_DIR, filename).toString();

            // Capture screenshot
            TakesScreenshot screenshot = (TakesScreenshot) driver;
            File sourceFile = screenshot.getScreenshotAs(OutputType.FILE);

            // Save to destination
            File destinationFile = new File(filePath);
            Files.copy(sourceFile.toPath(), destinationFile.toPath());

            String absolutePath = new File(filePath).getAbsolutePath();
            logger.info("Screenshot captured successfully: " + absolutePath);
            logger.info("Screenshot relative path: " + filePath);

            return filePath;

        } catch (IOException e) {
            logger.error("Failed to capture screenshot: " + e.getMessage(), e);
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error during screenshot capture: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Take screenshot without test name
     *
     * @param driver WebDriver instance
     * @return Path to saved screenshot file
     */
    public static String takeScreenshot(WebDriver driver) {
        return takeScreenshot(driver, null);
    }

    /**
     * Get absolute path of screenshots directory
     *
     * @return Absolute path to screenshots directory
     */
    public static String getScreenshotsDirectory() {
        return new File(SCREENSHOTS_DIR).getAbsolutePath();
    }
}

