package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


public class DriverManager {
    private static WebDriver driver;
    private static final String KEEP_BROWSER_OPEN_PROPERTY = "keepBrowserOpen";


    public static WebDriver getDriver() {
        if (driver == null) {
            driver = createChromeDriver();
        }
        return driver;
    }


    private static WebDriver createChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--start-maximized");
        return new ChromeDriver(options);
    }


    public static void quitDriver() {
        // Keep browser open by default; pass -DkeepBrowserOpen=false to close automatically.
        boolean keepBrowserOpen = Boolean.parseBoolean(System.getProperty(KEEP_BROWSER_OPEN_PROPERTY, "true"));
        if (keepBrowserOpen) {
            return;
        }

        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}

