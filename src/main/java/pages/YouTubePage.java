package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class YouTubePage extends BasePage {
    

    private static final By VIDEO_CONTAINER = By.id("content");
    private static final By COOKIES_ACCEPT_BUTTON = By.xpath("//button[@aria-label='Accept all']");
    private static final By VIEW_COUNT_ELEMENT = By.xpath("//span[contains(text(), 'views')] | //span[contains(text(), 'vista')] | //span[contains(text(), 'просмотр')]");


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
            WebElement info = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//ytd-watch-info-text//yt-formatted-string[@id='info']")
            ));
    
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

}

