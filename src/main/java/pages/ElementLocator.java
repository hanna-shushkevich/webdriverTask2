package pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;


public class ElementLocator {
    private static final Logger logger = LogManager.getLogger(ElementLocator.class);

    public static By findLinkByTextAndHref(String linkText, String hrefPattern) {
        String xpath = String.format("//a[contains(., '%s') and contains(@href, '%s')]", linkText, hrefPattern);
        logger.debug("Generated XPath: " + xpath);
        return By.xpath(xpath);
    }


    public static By findButtonByAriaLabel(String labelPattern) {
        String xpath = String.format("//button[contains(@aria-label, '%s')]", labelPattern);
        logger.debug("Generated XPath for button: " + xpath);
        return By.xpath(xpath);
    }


}

