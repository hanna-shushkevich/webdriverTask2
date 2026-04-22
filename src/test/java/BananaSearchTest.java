import org.openqa.selenium.Platform;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import pages.GoogleSearchPage;
import pages.YouTubePage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import utils.DriverManager;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Scenario:
 •	Open Google Search;
 •	Search for ‘Banana Song’;
 •	Find a Youtube link (‘href’ contains ‘youtube.com’) with ‘Despicable Me 2’ in the text, follow this link;
 •	Assert that we are on Youtube site (page title);
 •	Assert that video was watched more than 50 million times
 */

public class BananaSearchTest {
    private WebDriver driver;
    private GoogleSearchPage googleSearchPage;
    private YouTubePage youTubePage;



    @BeforeEach
    public void setUp(){
      driver = DriverManager.getDriver();
        googleSearchPage = new GoogleSearchPage(driver);
        youTubePage = new YouTubePage(driver);
    }

/*
    @Test
    @DisplayName("Banana Song YouTube Search")
    public void testBananaSearchYouTubeScenario() {

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

    @Test
    @DisplayName("Selenium Grid")
    public void seleniumGridTest()  {


        // Directly open YouTube video to avoid Google Search captcha
        youTubePage.openYoutubeVideo();


        youTubePage.pauseVideo();
        assertTrue(youTubePage.isOnYouTubeSite(),
                "Step 1 Failed: (Placeholder) Still on YouTube page");


        youTubePage.clickAndHoldToFastForward();
        assertTrue(youTubePage.isOnYouTubeSite(),
                "Step 2 Failed: (Placeholder) Still on YouTube page");

        youTubePage.scrollPage();
        assertTrue(youTubePage.isOnYouTubeSite(),
                "Step 4 Failed: Still on YouTube page");

        youTubePage.clickSubscribeButton();
        assertTrue(youTubePage.isSubscribeModalPresent(),
                "Step 3 Failed: Subscribe modal is shown");



    }


    @AfterEach
    public void tearDown() {
        DriverManager.quitDriver();
    }
}

