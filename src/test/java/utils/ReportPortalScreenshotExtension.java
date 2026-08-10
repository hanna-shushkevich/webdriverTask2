package utils;

import com.epam.reportportal.service.ReportPortal;
import driver.DriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

public class ReportPortalScreenshotExtension implements AfterTestExecutionCallback {

    private static final Logger logger = LogManager.getLogger(ReportPortalScreenshotExtension.class);

    @Override
    public void afterTestExecution(ExtensionContext context) {
        if (context.getExecutionException().isPresent()) {
            Throwable exception = context.getExecutionException().get();
            attachScreenshot(context.getDisplayName() + " - FAILED"); //exception present -> test failed, attachScreenshot
            emitFailureDetails(context.getDisplayName(), exception);
        }
    }

    private void attachScreenshot(String message) {
        Path tempFile = null;
        try {
            WebDriver driver = DriverManager.getDriver();

            if (driver instanceof TakesScreenshot) {
                byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                tempFile = Files.createTempFile("rp-screenshot-", ".png");
                Files.write(tempFile, bytes);
                // ReportPortal reads and uploads the file asynchronously on a background
                // thread, so it must not be deleted right after this call returns.
                // deleteOnExit() lets the upload finish while still cleaning up eventually.
                tempFile.toFile().deleteOnExit();
                boolean emitted = ReportPortal.emitLog(message, "ERROR", new Date(), tempFile.toFile()); //send to reportPortal
                if (!emitted) {
                    logger.warn("ReportPortal screenshot log was not emitted: no active ReportPortal context on this thread");
                }
            } else {
                logger.warn("Cannot attach screenshot to ReportPortal: driver is null or does not support screenshots");
            }
        } catch (Exception e) {
            logger.error("Failed to attach screenshot to ReportPortal", e);
        }
    }

    /**
     * Emits the exception message and full stack trace as a separate ReportPortal log entry,
     * so failures can be diagnosed directly in RP without cross-referencing local log files.
     */
    private void emitFailureDetails(String testName, Throwable exception) {
        try {
            StringWriter stringWriter = new StringWriter();
            exception.printStackTrace(new PrintWriter(stringWriter));
            String logMessage = testName + " - FAILED: " + exception.getMessage()
                    + System.lineSeparator() + stringWriter;

            boolean emitted = ReportPortal.emitLog(logMessage, "ERROR", new Date());
            if (!emitted) {
                logger.warn("ReportPortal failure details log was not emitted: no active ReportPortal context on this thread");
            }
        } catch (Exception e) {
            logger.error("Failed to emit failure details to ReportPortal", e);
        }
    }
}
