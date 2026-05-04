package driver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import utils.ConfigManager;

import java.net.MalformedURLException;
import java.net.URL;


public class DriverManager {
    private static final Logger logger = LogManager.getLogger(DriverManager.class);
    private static WebDriver driver;


    public static WebDriver createRemoteDriver() {
        if (driver != null) {
            logger.debug("WebDriver already initialized, returning existing instance");
            return driver;
        }

        String executionMode = System.getProperty("execution.mode", "grid");
        String browser = System.getProperty("browser", "chrome").toLowerCase();

        logger.info("Creating WebDriver - Mode: " + executionMode + ", Browser: " + browser);

        try {
            if ("local".equalsIgnoreCase(executionMode)) {
                driver = createLocalDriver(browser);
            } else {
                driver = createGridDriver(browser);
            }
            driver.manage().window().maximize();
            logger.info("WebDriver initialized successfully");
            return driver;
        } catch (Exception e) {
            logger.error("Failed to initialize WebDriver", e);
            throw new RuntimeException("Failed to initialize WebDriver", e);
        }
    }

    private static WebDriver createLocalDriver(String browser) {
        logger.debug("Creating local " + browser + " driver");
        switch (browser) {
            case "firefox":
                return new FirefoxDriver(new FirefoxOptions());
            case "chrome":
            default:
                return new ChromeDriver(new ChromeOptions());
        }
    }

    private static WebDriver createGridDriver(String browser) {
        ConfigManager config = ConfigManager.getInstance();
        String gridUrl = config.getGridUrl();
        String platform = config.getGridPlatform();

        logger.debug("Connecting to Selenium Grid at: " + gridUrl);

        try {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setBrowserName(browser.equals("firefox") ? "firefox" : "chrome");
            capabilities.setPlatform(Platform.valueOf(platform.toUpperCase()));
            return new RemoteWebDriver(new URL(gridUrl), capabilities);
        } catch (MalformedURLException e) {
            logger.error("Invalid Grid URL: " + gridUrl, e);
            throw new RuntimeException("Invalid Grid URL", e);
        }
    }

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