import pages.GoogleSearchPage;
import pages.YouTubePage;
import pages.PageExecutionDecorator;
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
    public void testBananaSearchYouTubeScenario() throws Exception {
        try {
            logger.info("Test: Search for Banana Song via Google - Starting");
            googleSearchPage = new GoogleSearchPage();
            youTubePage = new YouTubePage();
            
            // Pattern 1: Get decorator directly from page object
            PageExecutionDecorator googleDecorator = googleSearchPage.getDecorator();
            PageExecutionDecorator youtubeDecorator = youTubePage.getDecorator();

            // Step 1: Opening Google Search with decorator
            googleDecorator.executePageMethod(
                    () -> googleSearchPage.openGoogle(),
                    "Opening Google Search page"
            );
            assertTrue(googleSearchPage.isOnGoogleSearchPage(),
                    "Step 1 Failed: Not navigated to Google Search page");

            // Step 2: Searching for 'Banana Song' with decorator
            googleDecorator.executePageMethod(
                    () -> googleSearchPage.searchFor("Banana Song"),
                    "Searching for 'Banana Song'"
            );
            assertTrue(googleSearchPage.isOnGoogleSearchPage(),
                    "Step 2 Failed: Not on Google Search results page after searching");

            // Step 3: Clicking YouTube link with decorator
            googleDecorator.executePageMethod(
                    () -> {
                        boolean linkFound = googleSearchPage.clickYouTubeLinkWithText("Despicable Me 2");
                        assertTrue(linkFound, "YouTube link not found");
                    },
                    "Clicking YouTube link with 'Despicable Me 2'"
            );

            // Step 4: Switching to YouTube window with decorator
            youtubeDecorator.executePageMethod(
                    () -> youTubePage.switchToNewWindow(),
                    "Switching to YouTube window"
            );
            assertTrue(youTubePage.isOnYouTubeSite(),
                    "Step 4 Failed: Youtube site is not open");
            
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