package utils;

import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import test.BaseTest;

public class Listener extends BaseTest implements ITestListener
{
    @Override
    public void onTestStart(ITestResult result) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // TODO Auto-generated method stub
        System.out.println("Success of test cases and its details are : "+result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("----------- Test:" + result.getName() + " Failed -------------");
        saveScreenshot();
        System.out.println("Failure of test cases and its details are : "+result.getName());
    }
    @Override
    public void onTestSkipped(ITestResult result) {
        // TODO Auto-generated method stub
        System.out.println("Skip of test cases and its details are : "+result.getName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // TODO Auto-generated method stub
        System.out.println("Failure of test cases and its details are : "+result.getName());
    }

    @Override
    public void onStart(ITestContext context) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFinish(ITestContext context) {
        // TODO Auto-generated method stub

    }

    @Attachment(value = "Page Screen-Shot", type = "image/png")
    public byte[] saveScreenshot() {
        return ((TakesScreenshot)driver).getScreenshotAs(OutputType.BYTES);
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        // TODO Auto-generated method stub
        System.out.println("Failure of test cases and its details are : "+result.getName());
    }
}