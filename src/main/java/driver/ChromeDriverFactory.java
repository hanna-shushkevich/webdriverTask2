package driver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


public class ChromeDriverFactory implements DriverFactory {
    private static final Logger logger = LogManager.getLogger(ChromeDriverFactory.class);

    @Override
    public WebDriver createDriver() {
        logger.info("Creating Chrome WebDriver");
        try {
            ChromeOptions options = new ChromeOptions();
            WebDriver driver = new ChromeDriver(options);
            logger.info("Chrome WebDriver created successfully");
            return driver;
        } catch (Exception e) {
            logger.error("Failed to create Chrome WebDriver", e);
            throw new RuntimeException("Failed to create Chrome WebDriver", e);
        }
    }
}

