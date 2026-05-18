import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.GoogleLoginPage;
import pages.PageExecutionDecorator; /// unused import
import utils.ConfigManager;
import utils.ScreenshotUtility;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests login on youtube with invalid credentials to verify error handling.
 * Will fail during execution due to google security restriction (test used for verifying that logging is working)
 */
@DisplayName("YouTube Login Tests")
public class YoutubeLoginTest extends CommonConditions {
    private static final Logger logger = LogManager.getLogger(YoutubeLoginTest.class);

    @Test
    @DisplayName("User cannot login with wrong password")
    public void testUserCannotLoginWithWrongPassword() throws Exception {
        logger.info("Test: User cannot login with wrong password - Starting");

        try {
            logger.info("Step 1: Loading test credentials");
            ConfigManager config = ConfigManager.getInstance();
            String testEmail = config.getLoginEmail();
            String testPassword = config.getLoginPassword();
            User testUser = new User(testEmail, testPassword);
            logger.info("Test user loaded: " + testUser.getEmail());

            logger.info("Step 2: Initializing GoogleLoginPage");
            GoogleLoginPage loginPage = new GoogleLoginPage();
            
            // Get decorator from page object (auto-initialized in BasePage constructor)
            var loginDecorator = loginPage.getDecorator();

            logger.info("Step 3: Opening YouTube and closing cookies modal");
            loginDecorator.executePageMethod(
                    () -> loginPage.openYouTubeSignInPage(),
                    "Opening YouTube Sign-In page"
            );

            logger.info("Step 5-6: Entering email and clicking Next");
            loginDecorator.executePageMethod(
                    () -> loginPage.enterEmail(testUser.getEmail()),
                    "Entering email: " + testUser.getEmail()
            );
            
            loginDecorator.executePageMethod(
                    () -> loginPage.clickNextButton(),
                    "Clicking Next button after email entry"
            );

            logger.info("Step 6-7: Entering password and clicking Next");
            loginDecorator.executePageMethod(
                    () -> loginPage.enterPassword(testUser.getPassword()),
                    "Entering password"
            );
            
            loginDecorator.executePageMethod(
                    () -> loginPage.clickNextButton(),
                    "Clicking Next button after password entry"
            );

            logger.info("Step 8: Verifying wrong password error message");
            boolean isErrorDisplayed = loginDecorator.executePageMethodWithReturn(
                    () -> loginPage.isWrongPasswordErrorDisplayed(),
                    "Checking for wrong password error message"
            );
            
            loginDecorator.captureStepScreenshot("LoginError");
            
            assertTrue(isErrorDisplayed, "Step 8 Failed: No error message displayed for wrong password");
            logger.info("Test passed: Wrong password error message is displayed");

        } catch (Exception e) {
            logger.error("Test: User cannot login with wrong password - FAILED with exception: " + e.getMessage(), e);
            // Capture screenshot on failure
            String screenshotPath = ScreenshotUtility.takeScreenshot(driver, "YoutubeLoginTest_FAILED");
            if (screenshotPath != null) {
                logger.error("Screenshot saved at: " + screenshotPath);
            }
            throw e;
        }
    }
}
