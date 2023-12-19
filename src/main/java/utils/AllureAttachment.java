package utils;

import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class AllureAttachment {
    @Attachment(value = "{0}", type = "text/plain")
    public static String addTextAttachment(String message) {
        return message;
    }


    @Attachment(value = "Page Screenshot", type = "image/png", fileExtension = ".png")
    static byte[] attachScreenshot(AndroidDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
