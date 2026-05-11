package driver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import utils.ConfigManager;

import java.net.MalformedURLException;
import java.net.URL;


public class GridDriverFactory implements DriverFactory {
    private static final Logger logger = LogManager.getLogger(GridDriverFactory.class);
    private final String browser;


    public GridDriverFactory(String browser) {
        this.browser = browser.toLowerCase();
    }


    @Override
    public WebDriver createDriver() {
        ConfigManager config = ConfigManager.getInstance();
        String gridUrl = config.getGridUrl();
        String platform = config.getGridPlatform();

        logger.info("Creating Selenium Grid WebDriver - Browser: " + browser + ", URL: " + gridUrl);

        try {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setBrowserName(browser.equals("firefox") ? "firefox" : "chrome");
            capabilities.setPlatform(Platform.valueOf(platform.toUpperCase()));
            
            WebDriver driver = new RemoteWebDriver(new URL(gridUrl), capabilities);
            logger.info("Selenium Grid WebDriver created successfully");
            return driver;
        } catch (MalformedURLException e) {
            logger.error("Invalid Grid URL: " + gridUrl, e);
            throw new RuntimeException("Invalid Grid URL: " + gridUrl, e);
        } catch (Exception e) {
            logger.error("Failed to create Selenium Grid WebDriver", e);
            throw new RuntimeException("Failed to create Selenium Grid WebDriver", e);
        }
    }
}

