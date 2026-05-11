package driver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;


public class FirefoxDriverFactory implements DriverFactory {
    private static final Logger logger = LogManager.getLogger(FirefoxDriverFactory.class);


    @Override
    public WebDriver createDriver() {
        logger.info("Creating Firefox WebDriver");
        try {
            FirefoxOptions options = new FirefoxOptions();
            WebDriver driver = new FirefoxDriver(options);
            logger.info("Firefox WebDriver created successfully");
            return driver;
        } catch (Exception e) {
            logger.error("Failed to create Firefox WebDriver", e);
            throw new RuntimeException("Failed to create Firefox WebDriver", e);
        }
    }
}

