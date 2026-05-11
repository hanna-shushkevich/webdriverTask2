package driver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

/**
 * DriverManager: Implements Singleton pattern for WebDriver lifecycle management.
 * Responsibility: Manage single WebDriver instance (creation, retrieval, cleanup).
 * Uses DriverFactoryProvider to obtain appropriate factory (Factory Method pattern).
 */
public class DriverManager {
    private static final Logger logger = LogManager.getLogger(DriverManager.class);
    private static WebDriver driver;

    /**
     * Create or return existing WebDriver singleton
     * Uses Factory Method pattern via DriverFactoryProvider
     * 
     * @return WebDriver singleton instance
     */
    public static WebDriver createRemoteDriver() {
        if (driver != null) {
            logger.debug("WebDriver already initialized, returning existing instance");
            return driver;
        }

        String executionMode = System.getProperty("execution.mode", "grid");
        String browser = System.getProperty("browser", "chrome");

        logger.info("Creating WebDriver - Mode: " + executionMode + ", Browser: " + browser);

        try {
            // Use Factory Method pattern to get appropriate factory
            DriverFactory factory = DriverFactoryProvider.getFactory(executionMode, browser);
            driver = factory.createDriver();
            
            // Maximize window
            driver.manage().window().maximize();
            
            logger.info("WebDriver initialized successfully");
            return driver;
        } catch (Exception e) {
            logger.error("Failed to initialize WebDriver", e);
            throw new RuntimeException("Failed to initialize WebDriver", e);
        }
    }

    /**
     * Get current WebDriver instance (Singleton getter)
     * 
     * @return WebDriver singleton instance
     */
    public static WebDriver getDriver() {
        if (driver == null) {
            logger.warn("WebDriver not initialized. Call createRemoteDriver() first.");
            return createRemoteDriver();
        }
        return driver;
    }

    /**
     * Quit and cleanup WebDriver singleton
     */
    public static void quitDriver() {
        if (driver != null) {
            try {
                driver.quit();
                logger.info("WebDriver quit successfully");
            } catch (Exception e) {
                logger.error("Error while quitting driver", e);
            } finally {
                driver = null;
            }
        }
    }

}