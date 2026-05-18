package pages;

import driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver; // unused import
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

/**
Abstract base class for all page objects.

 */
public abstract class BasePage {
    protected static final Logger logger = LogManager.getLogger(BasePage.class);
    protected WebDriverWait wait;
    protected PageExecutionDecorator decorator;
    private static final int DEFAULT_TIMEOUT_SECONDS = 10;



    public BasePage() {

        /// this code is for demonstration of possibility to init one more Singleton instance:
        /// uncomment line below to explore how private DriverManager init() affects
//        DriverManager dm = new DriverManager();

        this.wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(DEFAULT_TIMEOUT_SECONDS));
        // Initialize PageFactory fields annotated with @FindBy
        PageFactory.initElements(DriverManager.getDriver(), this);
        // Initialize decorator for this page object
        this.decorator = new PageExecutionDecorator(this, this.getClass().getSimpleName());
        logger.debug(this.getClass().getSimpleName() + " page object initialized");
    }
    
    /**
     * Get the PageExecutionDecorator for this page object.
     * Allows transparent use of decorator pattern for method execution monitoring.
     * 
     * @return PageExecutionDecorator instance wrapping this page object
     */
    public PageExecutionDecorator getDecorator() {
        return decorator;
    }


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


    protected void navigateTo(String url) {
        PageNavigator.navigateTo(url);
    }

    public String getPageTitle() {
        return PageNavigator.getPageTitle();
    }

    public void switchToNewWindow() {
        PageNavigator.switchToNewWindow();
    }


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

