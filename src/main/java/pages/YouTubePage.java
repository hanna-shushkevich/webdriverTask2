package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class
YouTubePage extends BasePage {

    private static final By COOKIES_ACCEPT_BUTTON = By.xpath("//button[@aria-label='Accept all']");
    private static final By VIEW_COUNT_ELEMENT = By.xpath("//ytd-watch-info-text//yt-formatted-string[@id='info']");
    private static final By VIDEO_CONTAINER = By.xpath("//div[@class='html5-video-container']");
    private static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=wCkerYMffMo";
    private static final By SEARCH_BUTTON = By.xpath("//button[@aria-label='Search']");
    private static final By SUBSCRIBE_BUTTON = By.xpath("//button[contains(@aria-label, 'Subscribe')]");
    private static final By SUBSCRIBE_MODAL = By.xpath("//ytd-modal-with-title-and-button-renderer//yt-formatted-string[@id='title' and contains(text(), 'Want to subscribe')]");

    public YouTubePage(WebDriver driver) {
        super(driver);
    }

    public boolean isOnYouTubeSite() {
        acceptCookiesIfPresent();
        return waitForPageTitle("YouTube");
    }


    private void acceptCookiesIfPresent() {
        try {

            WebElement acceptButton = wait.until(
                ExpectedConditions.elementToBeClickable(COOKIES_ACCEPT_BUTTON)
            );
            acceptButton.click();
            

            Thread.sleep(500);
        } catch (Exception e) {
            // no modal, continue
        }
    }


    public String getViewCount() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(VIDEO_CONTAINER));
            WebElement info = wait.until(ExpectedConditions.visibilityOfElementLocated(VIEW_COUNT_ELEMENT));
    
            return info.getText();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean hasMoreThan50MillionViews() {
        String viewCount = getViewCount();
        
        if (viewCount == null) {
            return false;
        }
        
        try {
            double views = parseViewCount(viewCount);
            return views > 50_000_000;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parses view count string in various formats (116M, 116,747,332, 50.5M, 500K, etc.)
     * and converts to actual number.
     *
     * @param viewCountStr the view count string (e.g., "116M", "116,747,332", "500K")
     * @return the numeric view count
     */
    private double parseViewCount(String viewCountStr) {
        if (viewCountStr == null) {
            throw new IllegalArgumentException("viewCountStr is null");
        }
    
        String text = viewCountStr.trim();
    
        // 1) Compact form: 116M / 50.5M / 500K / 1.2B (case-insensitive)
        Pattern compactPattern = Pattern.compile("(?i)(\\d+(?:[\\.,]\\d+)?)\\s*([KMB])\\b");
        Matcher compactMatcher = compactPattern.matcher(text);
        if (compactMatcher.find()) {
            double number = Double.parseDouble(compactMatcher.group(1).replace(',', '.'));
            String suffix = compactMatcher.group(2).toUpperCase();
    
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
            return Double.parseDouble(digitsOnly);
        }
    
        throw new IllegalArgumentException("Cannot parse view count from: " + viewCountStr);
    }

    
    private void skipAdIfPresent() {
        By skipAdButton = By.className("ytp-skip-ad-button");

        try {
            Thread.sleep(2000);
            WebElement skipButton = wait.until(
                ExpectedConditions.elementToBeClickable(skipAdButton)
            );
            skipButton.click();
        } catch (Exception e) {
            // No skip button, ad automatically closed after finished
        }
    }


    public void pauseVideo() {
     try {
        skipAdIfPresent();
        Thread.sleep(500);

        Actions actions = new Actions(driver);
        actions.sendKeys(Keys.SPACE).perform();

        Thread.sleep(3000);

        actions.sendKeys(Keys.SPACE).perform();
    } catch (Exception e) {

    }}

    public void clickAndHoldToFastForward() {
        try {
           // wait.until(ExpectedConditions.visibilityOfElementLocated(VIDEO_CONTAINER));
            WebElement videoContainer = driver.findElement(VIDEO_CONTAINER);

            Actions actions = new Actions(driver);
            actions.clickAndHold(videoContainer)
                   .pause(3000)
                   .release()
                   .perform();
        } catch (Exception e) {
        }
    }

    public void scrollPage() {
        try {
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            jsExecutor.executeScript("window.scrollBy(0, 500);");
        } catch (Exception e) {
        }
    }


    /*

    public void clickSubtitlesButton() {
        try {
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            jsExecutor.executeScript("document.querySelector('.ytp-right-controls-left .ytp-subtitles-button').click();");
        } catch (Exception e) {

        }
    }
*/
    public void clickSearchButton() {
        try {
            // Wait for the button to be present
            wait.until(
                ExpectedConditions.elementToBeClickable(SEARCH_BUTTON)
            );
            
            // Click via JavaScript with the element
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement searchButton = driver.findElement(SEARCH_BUTTON);
            js.executeScript("arguments[0].click();", searchButton);
            Thread.sleep(500);
        } catch (Exception e) {
            // Fallback: Try clicking by CSS class using JS
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript(
                    "var btn = document.querySelector('button.ytSearchboxComponentSearchButton'); " +
                    "if(btn) { btn.click(); }"
                );
                Thread.sleep(500);
            } catch (Exception ex) {
                // Last resort: Try focus and Enter key
                try {
                    WebElement searchButton2 = driver.findElement(SEARCH_BUTTON);
                    JavascriptExecutor js2 = (JavascriptExecutor) driver;
                    js2.executeScript("arguments[0].focus();", searchButton2);
                    searchButton2.sendKeys(Keys.ENTER);
                    Thread.sleep(500);
                } catch (Exception exl) {
                    // All click attempts failed
                }
            }
        }
    }

    public void clickSubscribeButton() {
        try {
            wait.until(
                ExpectedConditions.elementToBeClickable(SUBSCRIBE_BUTTON)
            );
            
            // Click via JavaScript with the element
            JavascriptExecutor js = (JavascriptExecutor) driver;
            WebElement subscribeButton = driver.findElement(SUBSCRIBE_BUTTON);
            js.executeScript("arguments[0].click();", subscribeButton);
            Thread.sleep(500);
        } catch (Exception e) {
            // Fallback: Try clicking by aria-label using JS
            try {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                js.executeScript(
                    "var btn = document.querySelector('button[aria-label*=\"Subscribe\"]'); " +
                    "if(btn) { btn.click(); }"
                );
                Thread.sleep(500);
            } catch (Exception ex) {
                // Last resort: Try focus and Enter key
                try {
                    WebElement subscribeButton2 = driver.findElement(SUBSCRIBE_BUTTON);
                    JavascriptExecutor js2 = (JavascriptExecutor) driver;
                    js2.executeScript("arguments[0].focus();", subscribeButton2);
                    subscribeButton2.sendKeys(Keys.ENTER);
                    Thread.sleep(500);
                } catch (Exception exl) {
                    // All click attempts failed
                }
            }
        }
    }


    public boolean isSubscribeModalPresent() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(SUBSCRIBE_MODAL));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void openYoutubeVideo() {
        navigateTo(YOUTUBE_URL);
        acceptCookiesIfPresent();
    }
}

