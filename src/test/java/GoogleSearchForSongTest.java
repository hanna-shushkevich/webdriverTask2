import pages.GoogleSearchPage;
import pages.YouTubePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import driver.DriverManager;
import utils.ScreenshotUtility;

import static org.junit.jupiter.api.Assertions.*;



public class GoogleSearchForSongTest extends CommonConditions {
    private static final Logger logger = LogManager.getLogger(GoogleSearchForSongTest.class);

    private GoogleSearchPage googleSearchPage;
    private YouTubePage youTubePage;

    @Test
    @DisplayName("Search for Banana Song via Google")
    public void testBananaSearchYouTubeScenario() {
        try {
            logger.info("Test: Search for Banana Song via Google - Starting");
            googleSearchPage = new GoogleSearchPage(driver);
            youTubePage = new YouTubePage(driver);

            logger.info("Step 1: Opening Google Search");
            googleSearchPage.openGoogle();
            assertTrue(googleSearchPage.isOnGoogleSearchPage(),
                    "Step 1 Failed: Not navigated to Google Search page");
            logger.info("Step 1: Successfully on Google Search page");

            logger.info("Step 2: Searching for 'Banana Song'");
            googleSearchPage.searchFor("Banana Song");
            assertTrue(googleSearchPage.isOnGoogleSearchPage(),
                    "Step 2 Failed: Not on Google Search results page after searching");
            logger.info("Step 2: Search completed successfully");

            logger.info("Step 3: Clicking YouTube link with 'Despicable Me 2'");
            boolean linkFound = googleSearchPage.clickYouTubeLinkWithText("Despicable Me 2");
            assertTrue(linkFound,
                    "Step 3 Failed: Couldn't find and click YouTube link with 'Despicable Me 2'");
            logger.info("Step 3: YouTube link found and clicked");

            logger.info("Step 4: Switching to YouTube window");
            youTubePage.switchToNewWindow();
            assertTrue(youTubePage.isOnYouTubeSite(),
                    "Step 4 Failed: Youtube site is not open");
            logger.info("Step 4: Successfully switched to YouTube");
            
            logger.info("Test: Search for Banana Song via Google - PASSED");

        } catch (Exception e) {
            logger.error("Test: Search for Banana Song via Google - FAILED with exception: " + e.getMessage(), e);
            String screenshotPath = ScreenshotUtility.takeScreenshot(driver, "GoogleSearchForSongTest_FAILED");
            if (screenshotPath != null) {
                logger.error("Screenshot saved at: " + screenshotPath);
            }
            throw e;
        }
    }
}