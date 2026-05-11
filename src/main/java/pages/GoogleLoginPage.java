package pages;

import driver.DriverManager;
import model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * GoogleLoginPage: Page object for Google login flow.
 * Handles email and password entry for YouTube login.
 * Note: Uses Google's ServiceLogin endpoint which is used for YouTube authentication.
 */
public class GoogleLoginPage extends BasePage {
    private static final Logger logger = LogManager.getLogger(GoogleLoginPage.class);

    // Selectors
    private static final By EMAIL_INPUT = By.id("identifierId");
    private static final By PASSWORD_INPUT = By.name("Passwd");
    private static final By NEXT_BUTTON = By.xpath("//span[contains(text(), 'Next')]");
    private static final By ERROR_MESSAGE = By.xpath("//*[contains(text(), 'Wrong password')]");
    private static final By COOKIES_ACCEPT_BUTTON = By.xpath("//span[contains(text(), 'Accept all')]/ancestor::button");

    // PageFactory elements
    @FindBy(id = "identifierId")
    private WebElement emailInput;

    @FindBy(name = "Passwd")
    private WebElement passwordInput;


    public GoogleLoginPage() {
        super();
        logger.debug("GoogleLoginPage initialized");
    }


    public void openYouTubeSignInPage() {
        logger.info("Opening YouTube sign-in page");
        navigateTo("https://www.youtube.com/");
        
        // Accept cookies if present
        acceptCookiesIfPresent();
        
        // Click the Sign in button
        try {
            WebDriver driver = DriverManager.getDriver();
            // Use ElementLocator to find sign-in link by aria-label
            By signInLocator = ElementLocator.findButtonByAriaLabel("Sign in");
            WebElement signInButton = driver.findElement(signInLocator);
            waitForElement(signInButton);
            signInButton.click();
            logger.info("Clicked Sign in button");
            
            // Switch to new window if sign-in opens in new tab
            if (driver.getWindowHandles().size() > 1) {
                switchToNewWindow();
            }
        } catch (Exception e) {
            logger.error("Failed to click Sign in button: " + e.getMessage());
            throw e;
        }
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


    public void enterEmail(String email) {
        logger.info("Entering email");
        try {
            WebElement email_field = wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL_INPUT));
            email_field.sendKeys(email);
            logger.info("Email entered successfully");
        } catch (Exception e) {
            logger.error("Failed to enter email: " + e.getMessage());
            throw e;
        }
    }


    public void clickNextButton() {
        logger.info("Clicking Next button");
        try {
            WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(NEXT_BUTTON));
            nextButton.click();
            logger.info("Next button clicked");
        } catch (Exception e) {
            logger.error("Failed to click Next button: " + e.getMessage());
            throw e;
        }
    }


    public void enterPassword(String password) {
        logger.info("Entering password");
        try {
            WebElement password_field = wait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD_INPUT));
            password_field.sendKeys(password);
            logger.info("Password entered successfully");
        } catch (Exception e) {
            logger.error("Failed to enter password: " + e.getMessage());
            throw e;
        }
    }


    public boolean isWrongPasswordErrorDisplayed() {
        logger.info("Checking for wrong password error message");
        try {
            WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE));
            boolean isDisplayed = errorMsg.isDisplayed();
            logger.info("'Wrong password' error message is " + (isDisplayed ? "displayed" : "not displayed"));
            return isDisplayed;
        } catch (Exception e) {
            logger.warn("'Wrong password' error message not found: " + e.getMessage());
            return false;
        }
    }


    public void login(User user) {
        logger.info("Starting login process with user: " + user.getEmail());
        try {
            enterEmail(user.getEmail());
            clickNextButton();

            // Wait for password field to appear
            Thread.sleep(1000);

            enterPassword(user.getPassword());
            clickNextButton();

            logger.info("Login process completed");
        } catch (Exception e) {
            logger.error("Login process failed: " + e.getMessage(), e);
            throw new RuntimeException("Login process failed: " + e.getMessage(), e);
        }
    }
}

