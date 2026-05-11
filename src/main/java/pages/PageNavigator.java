package pages;

import driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

/**
 * PageNavigator: Utility class for page navigation operations.
 */
public class PageNavigator {
    private static final Logger logger = LogManager.getLogger(PageNavigator.class);


    public static void navigateTo(String url) {
        logger.info("Navigating to URL: " + url);
        try {
            DriverManager.getDriver().navigate().to(url);
            logger.debug("Navigation complete");
        } catch (Exception e) {
            logger.error("Failed to navigate to: " + url, e);
            throw e;
        }
    }

    public static String getPageTitle() {
        String title = DriverManager.getDriver().getTitle();
        logger.debug("Current page title: " + title);
        return title;
    }


    public static void switchToNewWindow() {
        WebDriver driver = DriverManager.getDriver();
        String originalWindow = driver.getWindowHandle();
        logger.debug("Original window handle: " + originalWindow);
        
        try {
            for (String windowHandle : driver.getWindowHandles()) {
                if (!windowHandle.equals(originalWindow)) {
                    driver.switchTo().window(windowHandle);
                    logger.info("Switched to new window handle: " + windowHandle);
                    logger.debug("New window title: " + driver.getTitle());
                    return;
                }
            }
            logger.warn("No new window found");
        } catch (Exception e) {
            logger.error("Failed to switch to new window: " + e.getMessage(), e);
            throw e;
        }
    }
}

