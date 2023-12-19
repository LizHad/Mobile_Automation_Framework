package utils;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import java.io.ByteArrayInputStream;

public class ScreenshotListener extends TestListenerAdapter implements ITestListener {
    @Override
    public void onTestFailure(ITestResult result) {

        Object androidDriverAttribute = result.getTestContext().getAttribute("AndroidDriver");
        if (WebDriver.class.isAssignableFrom(androidDriverAttribute.getClass())) {
            Allure.addAttachment("ScreenshotFailure",new ByteArrayInputStream(((TakesScreenshot) androidDriverAttribute).getScreenshotAs(OutputType.BYTES)));
        }

    }
}
