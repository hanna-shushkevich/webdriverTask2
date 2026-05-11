package pages;

import driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.ConfigManager;



public class GoogleSearchPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(GoogleSearchPage.class);
    private static final String GOOGLE_URL = "https://www.google.com/";
    private static final By COOKIES_ACCEPT_BUTTON = By.id("L2AGLb");
    private static final By SEARCH_INPUT = By.cssSelector("textarea.gLFyf");
    private static final By SEARCH_RESULTS = By.xpath("//div[@id='search']");


    @FindBy(css = "textarea.gLFyf")
    private WebElement searchBox;

    @FindBy(id = "L2AGLb")
    private WebElement acceptCookiesButton;

    public GoogleSearchPage() {
        super();
    }


    public void openGoogle() {
        logger.info("Opening Google Search page");
        ConfigManager config = ConfigManager.getInstance();
        String googleUrl = config.getGoogleUrl();
        logger.debug("Google URL from config: " + googleUrl);
        navigateTo(googleUrl);
        acceptCookiesIfPresent();
        logger.info("Google Search page opened successfully");
    }


    private void acceptCookiesIfPresent() {
        try {
            logger.debug("Attempting to accept cookies modal");
            WebElement acceptButton = wait.until(
                ExpectedConditions.elementToBeClickable(COOKIES_ACCEPT_BUTTON)
            );
            acceptButton.click();
            Thread.sleep(500);
            logger.info("Cookies accepted successfully");
        } catch (Exception e) {
            logger.debug("Cookies modal not present or already accepted: " + e.getMessage());
            // no modal, continue
        }
    }


    public void searchFor(String searchQuery) {
        logger.info("Searching for: " + searchQuery);
        WebElement searchField = waitForElement(DriverManager.getDriver().findElement(SEARCH_INPUT));
        searchField.clear();
        searchField.sendKeys(searchQuery);
        logger.debug("Search query entered: " + searchQuery);
        searchField.submit();
        logger.debug("Search submitted");

        wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_RESULTS));
        logger.info("Search results loaded successfully");
    }


    public boolean clickYouTubeLinkWithText(String linkText) {
        logger.info("Searching for YouTube link with text: " + linkText);
        
        try {
            // Use ElementLocator to generate XPath dynamically
            By xpathLocator = ElementLocator.findLinkByTextAndHref(linkText, "youtube.com");
            logger.debug("XPath locator generated via ElementLocator");
            
            WebElement youtubeLink = wait.until(
                ExpectedConditions.elementToBeClickable(xpathLocator)
            );
            logger.debug("YouTube link found: " + youtubeLink.getText());
            youtubeLink.click();
            logger.info("YouTube link clicked successfully");
            return true;
        } catch (Exception e) {
            logger.warn("YouTube link with text '" + linkText + "' not found or not clickable: " + e.getMessage());
            return false;
        }
    }


    public boolean isOnGoogleSearchPage() {
        String title = getPageTitle();
        boolean isOnGoogle = title.contains("Google");
        logger.debug("Is on Google Search page: " + isOnGoogle + " (Title: " + title + ")");
        return isOnGoogle;
    }
}


