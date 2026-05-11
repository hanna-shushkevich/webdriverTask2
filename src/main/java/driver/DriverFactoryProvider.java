package driver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * DriverFactoryProvider: Provides appropriate DriverFactory based on execution mode.
 * Uses Factory Method pattern to select between concrete factories.
 *
 * Responsibility: Return correct factory implementation based on configuration
 */
public class DriverFactoryProvider {
    private static final Logger logger = LogManager.getLogger(DriverFactoryProvider.class);


    public static DriverFactory getFactory(String executionMode, String browser) {
        logger.debug("Getting factory for execution mode: " + executionMode + ", browser: " + browser);

        String mode = executionMode != null ? executionMode.toLowerCase() : "grid";
        String browserType = browser != null ? browser.toLowerCase() : "chrome";

        if ("local".equals(mode)) {
            if ("firefox".equals(browserType)) {
                logger.debug("Returning FirefoxDriverFactory");
                return new FirefoxDriverFactory();
            } else {
                logger.debug("Returning ChromeDriverFactory");
                return new ChromeDriverFactory();
            }
        } else {
            // Default to grid mode
            logger.debug("Returning GridDriverFactory for browser: " + browserType);
            return new GridDriverFactory(browserType);
        }
    }
}

