import pages.GoogleSearchPage;
import pages.YouTubePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import utils.ScreenshotUtility;

import static org.junit.jupiter.api.Assertions.*;


public class BananaSearchTest extends CommonConditions {
    private static final Logger logger = LogManager.getLogger(BananaSearchTest.class);

    private YouTubePage youTubePage;


/*  Failing due to Google captcha, replaced with directly navigating to Youtube
    @Test
    @DisplayName("Banana Song YouTube Search")
    public void testBananaSearchYouTubeScenario() {
        googleSearchPage = new GoogleSearchPage(driver);
        youTubePage = new YouTubePage(driver);

        googleSearchPage.openGoogle();
        assertTrue(googleSearchPage.isOnGoogleSearchPage(), 
            "Step 1 Failed: Should be on Google Search page");
        
        googleSearchPage.searchFor("Banana Song");
        assertTrue(googleSearchPage.isOnGoogleSearchPage(), 
            "Step 2 Failed: Search is performed");

        boolean linkFound = googleSearchPage.clickYouTubeLinkWithText("Despicable Me 2");
        assertTrue(linkFound, 
            "Step 3 Failed: Should find and click YouTube link with 'Despicable Me 2'");

        youTubePage.switchToNewWindow();
        assertTrue(youTubePage.isOnYouTubeSite(), 
            "Step 4 Failed: Youtube is open");

        
        assertTrue(youTubePage.hasMoreThan50MillionViews(), 
            "Step 5 Failed: Video should have more than 50 million views");

        youTubePage.pauseVideo();
        assertTrue(youTubePage.isOnYouTubeSite(), 
            "Step 6 Failed: Still on YouTube page after pausing video");

        youTubePage.clickAndHoldToFastForward();
        assertTrue(youTubePage.isOnYouTubeSite(), 
            "Step 7 Failed: Still on YouTube page after fast-forward");

        youTubePage.clickSubtitlesButton();
        assertTrue(youTubePage.isOnYouTubeSite(),
                "Step 8 Failed: Still on YouTube page after clicking subtitles button");

        youTubePage.scrollPage();
        assertTrue(youTubePage.isOnYouTubeSite(), 
            "Step 9 Failed: Still on YouTube page after scrolling");


    }
*/
    /**
     * Scenario:
     •	Open YouTube video by link
     •	Pause-resume the video
     •	Fast-forward the video
     •	Scroll the page
     •	Click subscribe button
     */
     @Test
     @DisplayName("Selenium Grid")
     public void seleniumGridTest() throws Exception {
         try {
             logger.info("Test: Selenium Grid - Starting YouTube video interaction test");
             youTubePage = new YouTubePage();
             
             // Get decorator from page object (auto-initialized in BasePage constructor)
             var youtubeDecorator = youTubePage.getDecorator();

            // Directly open YouTube video to avoid Google Search captcha
            youtubeDecorator.executePageMethod(
                    () -> youTubePage.openYoutubeVideo(),
                    "Opening YouTube video"
            );

            youtubeDecorator.executePageMethod(
                    () -> youTubePage.pauseVideo(),
                    "Pausing video"
            );
            assertTrue(youTubePage.isOnYouTubeSite(),
                    "Step 2 Failed: (Placeholder) Not on on YouTube page after pausing video");

            youtubeDecorator.executePageMethod(
                    () -> youTubePage.clickAndHoldToFastForward(),
                    "Fast-forwarding video"
            );
            assertTrue(youTubePage.isOnYouTubeSite(),
                    "Step 3 Failed: (Placeholder) Not on on YouTube page after fast-forwarding video");

            youtubeDecorator.executePageMethod(
                    () -> youTubePage.scrollPage(),
                    "Scrolling page"
            );
            assertTrue(youTubePage.isOnYouTubeSite(),
                    "Step 4 Failed: (Placeholder) Not on on YouTube page after scrolling the page");

            youtubeDecorator.executePageMethod(
                    () -> youTubePage.clickSubscribeButton(),
                    "Clicking subscribe button"
            );
            assertTrue(youTubePage.isSubscribeModalPresent(),
                    "Step 5 Failed: Subscribe modal is not found");
            
            logger.info("Test: Selenium Grid - PASSED");

         } catch (Exception e) {
             logger.error("Test: Selenium Grid - FAILED with exception: " + e.getMessage(), e);
             // Capture screenshot on failure
             String screenshotPath = ScreenshotUtility.takeScreenshot(driver, "BananaSearchTest_FAILED");
             if (screenshotPath != null) {
                 logger.error("Screenshot saved at: " + screenshotPath);
             }
             throw e;
         }
     }


}

