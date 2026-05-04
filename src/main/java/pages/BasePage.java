package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

/**
 * BasePage: Abstract base class for all page objects.
 * Encapsulates driver management, wait strategies, and common page interactions.
 * Supports both manual element location and Selenium PageFactory.
 *
 * Usage in subclasses:
 * - Call super(driver) to initialize BasePage
 * - Optionally use @FindBy annotations on WebElement fields (PageFactory automatically initializes)
 * - Use protected methods like waitForElement(), navigateTo(), waitForPageTitle()
 */
public abstract class BasePage {
    protected static final Logger logger = LogManager.getLogger(BasePage.class);
    protected WebDriver driver;
    protected WebDriverWait wait;
    private static final int DEFAULT_TIMEOUT_SECONDS = 10;


    /**
     * Constructor initializes driver, wait handler, and PageFactory elements
     *
     * @param driver WebDriver instance
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
        // Initialize PageFactory fields annotated with @FindBy
        PageFactory.initElements(driver, this);
        logger.debug(this.getClass().getSimpleName() + " page object initialized");
    }

    /**
     * Wait for element visibility
     *
     * @param element WebElement to wait for
     * @return Visible WebElement
     */
    protected WebElement waitForElement(WebElement element) {
        logger.debug("Waiting for element to be visible");
        try {
            WebElement visibleElement = wait.until(ExpectedConditions.visibilityOf(element));
            logger.debug("Element is now visible");
            return visibleElement;
        } catch (Exception e) {
            logger.error("Failed to wait for element visibility: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Navigate to specified URL
     *
     * @param url Target URL
     */
    protected void navigateTo(String url) {
        logger.info("Navigating to URL: " + url);
        driver.navigate().to(url);
        logger.debug("Navigation complete");
    }

    /**
     * Get current page title
     *
     * @return Page title
     */
    public String getPageTitle() {
        String title = driver.getTitle();
        logger.debug("Current page title: " + title);
        return title;
    }

    /**
     * Switch to newly opened window/tab.
     * Finds first window handle that's different from current handle.
     */
    public void switchToNewWindow() {
        String originalWindow = driver.getWindowHandle();
        logger.debug("Original window handle: " + originalWindow);
        
        for (String windowHandle : driver.getWindowHandles()) {
            if (!windowHandle.equals(originalWindow)) {
                driver.switchTo().window(windowHandle);
                logger.info("Switched to new window handle: " + windowHandle);
                logger.debug("New window title: " + driver.getTitle());
                break;
            }
        }
    }

     /**
      * Wait for page title to contain specified text
      *
      * @param textInTitle Text to find in title
      * @return true if title contains text within timeout
      */
     protected boolean waitForPageTitle(String textInTitle) {
         logger.info("Waiting for page title to contain: " + textInTitle);
         try {
             boolean result = wait.until(ExpectedConditions.titleContains(textInTitle));
             logger.info("Page title contains: " + textInTitle);
             return result;
         } catch (Exception e) {
             logger.error("Timeout waiting for page title to contain: " + textInTitle);
             throw e;
         }
     }
}

