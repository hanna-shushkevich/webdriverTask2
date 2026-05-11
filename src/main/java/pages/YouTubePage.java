package pages;

import driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.ConfigManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class YouTubePage extends BasePage {
    private static final Logger logger = LogManager.getLogger(YouTubePage.class);

    private static final By COOKIES_ACCEPT_BUTTON = By.xpath("//button[@aria-label='Accept all']");
    private static final By VIDEO_CONTAINER = By.xpath("//div[@class='html5-video-container']");
    private static final By SUBSCRIBE_BUTTON = By.xpath("//button[contains(@aria-label, 'Subscribe')]");
    private static final By SUBSCRIBE_MODAL = By.xpath("//ytd-modal-with-title-and-button-renderer//yt-formatted-string[@id='title' and contains(text(), 'Want to subscribe')]");


    @FindBy(xpath = "//button[@aria-label='Accept all']")
    private WebElement acceptCookiesBtn;

    @FindBy(xpath = "//div[@class='html5-video-container']")
    private WebElement videoContainerElement;

    @FindBy(xpath = "//button[contains(@aria-label, 'Subscribe')]")
    private WebElement subscribeBtn;

    public YouTubePage() {
        super();
    }


    public boolean isOnYouTubeSite() {
        logger.debug("Checking if on YouTube site");
        acceptCookiesIfPresent();
        boolean isOnYoutube = waitForPageTitle("YouTube");
        logger.info("Is on YouTube site: " + isOnYoutube);
        return isOnYoutube;
    }


    private void acceptCookiesIfPresent() {
        try {
            logger.debug("Attempting to accept cookies modal");
            WebElement acceptButton = wait.until(
                ExpectedConditions.elementToBeClickable(COOKIES_ACCEPT_BUTTON)
            );
            acceptButton.click();
            Thread.sleep(500);
            logger.info("YouTube cookies accepted successfully");
        } catch (Exception e) {
            logger.debug("Cookies modal not present or already accepted: " + e.getMessage());
            // no modal, continue
        }
    }

    /**
     * Parse view count string to double.
     * Handles multiple formats:
     * - Compact form: 116M / 50.5M / 500K / 1.2B
     * - Full number form: 116,747,332 / 116 747 332 / 116.747.332
     *
     * @param viewCountStr View count string to parse
     * @return Parsed view count as double
     * @throws IllegalArgumentException if format cannot be parsed
     */
    private double parseViewCount(String viewCountStr) {
        logger.debug("Parsing view count: " + viewCountStr);
        if (viewCountStr == null) {
            logger.error("viewCountStr is null");
            throw new IllegalArgumentException("viewCountStr is null");
        }
    
        String text = viewCountStr.trim();
    
        // 1) Compact form: 116M / 50.5M / 500K / 1.2B (case-insensitive)
        Pattern compactPattern = Pattern.compile("(?i)(\\d+(?:[\\.,]\\d+)?)\\s*([KMB])\\b");
        Matcher compactMatcher = compactPattern.matcher(text);
        if (compactMatcher.find()) {
            double number = Double.parseDouble(compactMatcher.group(1).replace(',', '.'));
            String suffix = compactMatcher.group(2).toUpperCase();
            logger.debug("Compact form detected: " + number + suffix);
    
            switch (suffix) {
                case "K":
                    return number * 1_000d;
                case "M":
                    return number * 1_000_000d;
                case "B":
                    return number * 1_000_000_000d;
            }
        }
    
        // 2) Full number form: 116,747,332 (or 116 747 332 / 116.747.332)
        Pattern fullNumberPattern = Pattern.compile("(\\d{1,3}(?:[\\s,\\.]\\d{3})+|\\d+)");
        Matcher fullMatcher = fullNumberPattern.matcher(text);
        if (fullMatcher.find()) {
            String digitsOnly = fullMatcher.group(1).replaceAll("[\\s,\\.]", "");
            double result = Double.parseDouble(digitsOnly);
            logger.debug("Full number form detected: " + result);
            return result;
        }
    
        logger.error("Cannot parse view count from: " + viewCountStr);
        throw new IllegalArgumentException("Cannot parse view count from: " + viewCountStr);
    }



    private void skipAdIfPresent() {
        By skipAdButton = By.className("ytp-skip-ad-button");

        try {
            logger.debug("Waiting for ad skip button");
            Thread.sleep(2000);
            WebElement skipButton = wait.until(
                ExpectedConditions.elementToBeClickable(skipAdButton)
            );
            skipButton.click();
            logger.info("Ad skipped successfully");
        } catch (Exception e) {
            logger.debug("Ad skip button not found: " + e.getMessage());
            // No skip button, ad automatically closed after finished
        }
    }

    public void pauseVideo() {
        logger.info("Pausing video");
        try {
            skipAdIfPresent();
            Thread.sleep(500);

            WebDriver driver = DriverManager.getDriver();
            Actions actions = new Actions(driver);
            actions.sendKeys(Keys.SPACE).perform();
            logger.debug("Pause key sent");

            Thread.sleep(3000);

            actions.sendKeys(Keys.SPACE).perform();
            logger.debug("Play key sent");
            logger.info("Video pause/play action completed");
        } catch (Exception e) {
            logger.error("Video pause action failed: " + e.getMessage(), e);
        }
    }


    public void clickAndHoldToFastForward() {
        logger.info("Fast-forwarding video");
        try {
            WebDriver driver = DriverManager.getDriver();
            WebElement videoContainer = driver.findElement(VIDEO_CONTAINER);

            Actions actions = new Actions(driver);
            actions.clickAndHold(videoContainer)
                   .pause(3000)
                   .release()
                   .perform();
            logger.info("Fast-forward action completed");
        } catch (Exception e) {
            logger.error("Fast forward action failed: " + e.getMessage(), e);
        }
    }


    public void scrollPage() {
        logger.info("Scrolling page");
        try {
            WebDriver driver = DriverManager.getDriver();
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            jsExecutor.executeScript("window.scrollBy(0, 500);");
            logger.debug("Scrolled down by 500 pixels");
            logger.info("Page scroll completed");
        } catch (Exception e) {
            logger.error("Scroll action failed: " + e.getMessage(), e);
        }
    }


    public void clickSubscribeButton() {
        logger.info("Clicking subscribe button");
        try {
            logger.debug("Attempting standard click");
            // Use ElementLocator to find subscribe button by aria-label
            By subscribeButtonLocator = ElementLocator.findButtonByAriaLabel("Subscribe");
            wait.until(
                ExpectedConditions.elementToBeClickable(subscribeButtonLocator)
            );
            
            WebDriver driver = DriverManager.getDriver();
            // Click via JavaScript with the element
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement subscribeButton = driver.findElement(subscribeButtonLocator);
            js.executeScript("arguments[0].click();", subscribeButton);
            Thread.sleep(500);
            logger.info("Subscribe button clicked successfully via JavaScript");
        } catch (Exception e) {
            logger.warn("Standard click failed, attempting fallback 1: " + e.getMessage());
            // ...existing code...
            try {
                WebDriver driver = DriverManager.getDriver();
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript(
                    "var btn = document.querySelector('button[aria-label*=\"Subscribe\"]'); " +
                    "if(btn) { btn.click(); }"
                );
                Thread.sleep(500);
                logger.info("Subscribe button clicked successfully via fallback 1");
            } catch (Exception ex) {
                logger.warn("Fallback 1 failed, attempting fallback 2: " + ex.getMessage());
                // ...existing code...
                try {
                    WebDriver driver = DriverManager.getDriver();
                    By subscribeButtonLocator = ElementLocator.findButtonByAriaLabel("Subscribe");
                    WebElement subscribeButton2 = driver.findElement(subscribeButtonLocator);
                    JavascriptExecutor js2 = (JavascriptExecutor) driver;
                    js2.executeScript("arguments[0].focus();", subscribeButton2);
                    subscribeButton2.sendKeys(Keys.ENTER);
                    Thread.sleep(500);
                    logger.info("Subscribe button clicked successfully via fallback 2");
                } catch (Exception exl) {
                    logger.error("All subscribe button click attempts failed: " + exl.getMessage(), exl);
                }
            }
        }
    }


    public boolean isSubscribeModalPresent() {
        logger.debug("Checking if subscribe modal is present");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(SUBSCRIBE_MODAL));
            logger.info("Subscribe modal is present");
            return true;
        } catch (Exception e) {
            logger.debug("Subscribe modal not found: " + e.getMessage());
            return false;
        }
    }


    public void openYoutubeVideo() {
        logger.info("Opening YouTube video");
        ConfigManager config = ConfigManager.getInstance();
        String videoUrl = config.getTestVideoUrl();
        logger.debug("Test video URL: " + videoUrl);
        navigateTo(videoUrl);
        acceptCookiesIfPresent();
        logger.info("YouTube video page loaded successfully");
    }
}


