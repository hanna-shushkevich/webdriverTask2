package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class GoogleSearchPage extends BasePage {
    private static final String GOOGLE_URL = "https://www.google.com/";
    private static final By COOKIES_ACCEPT_BUTTON = By.id("L2AGLb");
    private static final By SEARCH_INPUT = By.xpath("//textarea");
    private static final By SEARCH_RESULTS = By.xpath("//div[@id='search']");


    public GoogleSearchPage(WebDriver driver) {
        super(driver);
    }

    public void openGoogle() {
        navigateTo(GOOGLE_URL);
        acceptCookiesIfPresent();
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

    public void searchFor(String searchQuery) {
        WebElement searchBox = waitForElement(driver.findElement(SEARCH_INPUT));
        searchBox.clear();
        searchBox.sendKeys(searchQuery);
        searchBox.submit();

        wait.until(ExpectedConditions.visibilityOfElementLocated(SEARCH_RESULTS));

    }


    public boolean clickYouTubeLinkWithText(String linkText) {
        String xpathLocator = String.format("//a[contains(., '%s') and contains(@href, 'youtube.com')]", linkText);
        
        try {
            WebElement youtubeLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath(xpathLocator))
            );
            youtubeLink.click();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public boolean isOnGoogleSearchPage() {
        String title = getPageTitle();
        return title.contains("Google");
    }
}
