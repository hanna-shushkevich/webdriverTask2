package utils;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class DriverManager {
    private static WebDriver driver;
    //private static final String GRID_URL = "http://192.168.100.136:4444/wd/hub";
    private static final String GRID_URL = "http://localhost:4444/wd/hub";

    public static WebDriver getDriver() {
        if (driver == null) {
            // Default to Chrome if not specified
            String browser = System.getProperty("browser", "chrome");
            driver = createRemoteDriver(browser);
        }
        return driver;
    }

   /* private static WebDriver createRemoteDriver(String browser) {
        try {
            if (browser.equalsIgnoreCase("firefox")) {
                FirefoxOptions options = new FirefoxOptions();
                options.addArguments("--start-maximized");
                return new RemoteWebDriver(new URL(GRID_URL), options);
            } else {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--disable-blink-features=AutomationControlled");
                options.addArguments("--start-maximized");
                return new RemoteWebDriver(new URL(GRID_URL), options);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Grid URL", e);
        }
    }*/

    private static WebDriver createRemoteDriver(String browser) {
        if (driver == null) {
            try {
                DesiredCapabilities capabilities = new DesiredCapabilities();
                capabilities.setBrowserName(browser); //chrome/firefox
                capabilities.setPlatform(Platform.MAC);

                driver = new RemoteWebDriver(new URL(GRID_URL), capabilities);
                driver.manage().window().maximize();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return driver;
    }

    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}