package test;

import com.google.common.collect.ImmutableMap;
import io.appium.java_client.MobileElement;
import io.appium.java_client.TouchAction;
import io.appium.java_client.android.Activity;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidElement;
import io.appium.java_client.android.nativekey.AndroidKey;
import io.appium.java_client.android.nativekey.KeyEvent;
import io.appium.java_client.remote.AndroidMobileCapabilityType;
import io.appium.java_client.remote.MobileCapabilityType;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.touch.offset.PointOption;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.ITestContext;
import org.testng.annotations.*;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.github.automatedowl.tools.AllureEnvironmentWriter.allureEnvironmentWriter;


public class BaseTest {

    protected static AndroidDriver<AndroidElement> driver;
    private static AppiumDriverLocalService service;
    String serialMobile = System.getenv("MobileSN");

    @BeforeSuite
    public void setUp(ITestContext testContext) throws MalformedURLException, InterruptedException {

        //setAllureEnvironment();
        startAppiumService();
        // PreTest();
        DesiredCapabilities dc = new DesiredCapabilities();
        dc.setCapability(MobileCapabilityType.NO_RESET, true);
        dc.setCapability(MobileCapabilityType.UDID, serialMobile);// Automation Mobile
        dc.setCapability(AndroidMobileCapabilityType.APP_PACKAGE, getData("APP_PACKAGE"));
        dc.setCapability(AndroidMobileCapabilityType.APP_ACTIVITY, getData("APP_ACTIVITY"));
        dc.setCapability("automationName", "UiAutomator2");
        //dc.setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 300000); // Set timeout to 5 minutes
        //driver = new AndroidDriver<>(new URL("http://localhost:4723/wd/hub"), dc);// if Start from Appium Studio
        driver = new AndroidDriver<>(dc); // if Start from server
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
        testContext.setAttribute("AndroidDriver", driver);
        ClearCacheInTest();
    }

    public void startAppiumService(){

        AppiumServiceBuilder builder = new AppiumServiceBuilder();
        builder.withIPAddress("127.0.0.1");
        builder.usingAnyFreePort();
        service = AppiumDriverLocalService.buildService(builder);
        service.start();
        System.out.println("Appium Service Started");
    }

    public void CloseApp(){
        driver.closeApp();
    }
    public void LaunchApp(){
        driver.launchApp();
    }

    public void CloseLaunchApp(){
        driver.closeApp();
        driver.launchApp();
    }

    public void ActivateDeviceSettings(){
        Activity activity = new Activity("com.android.settings", "com.android.settings.Settings");
        driver.startActivity(activity);
    }

    public void clickOnScreenDynamically(int a, int b){
        int width = driver.manage().window().getSize().width;
        int height = driver.manage().window().getSize().height;
        int x = width / a;
        int y = height / b;
        TouchAction touchAction = new TouchAction<>(driver);
        new TouchAction<>(driver).press(PointOption.point(x, y))
                .release().perform();
    }
    @AfterSuite
    public void tearDown() throws InterruptedException {
        service.stop();
        CloseAll();
        driver.quit();
    }

    //Get data from xml file
    public String getData (String nodeName) {

        DocumentBuilder dBuilder;
        Document doc = null;
        String path = System.getProperty("user.dir") + "\\src\\main\\java\\data\\data.xml";
        File fXmlFile = new File(path);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(fXmlFile);
        }
        catch(Exception e) {
            System.out.println("Exception in reading XML file: " + e);
        }
        doc.getDocumentElement().normalize();
        return doc.getElementsByTagName(nodeName).item(0).getTextContent();
    }
    //DeviceLocked
    public boolean isDeviceLocked() {
        boolean isLocked = driver.isDeviceLocked();
        if(isLocked) {
            driver.unlockDevice();
        }
        return false;
    }
    public void ClearCacheInTest(){
        driver.resetApp();
    }

    //Press on Home + App Switch and Close all Application
    public void CloseAll() throws InterruptedException {
        driver.pressKey(new KeyEvent().withKey(AndroidKey.HOME));
        driver.pressKey(new KeyEvent().withKey(AndroidKey.APP_SWITCH));
        closeAllApps();
    }

    public  void closeAllApps() throws InterruptedException {

        MobileElement btnCloseAll = driver.findElementByXPath("//*[@text='Close all']");
        btnCloseAll.click();
    }

    //Enable data,wifi,location,bluetooth before running
    public void PreTest(){

        runShellScript("adb shell svc data enable");
        runShellScript("adb shell svc wifi enable");
        runShellScript("adb shell am broadcast -a io.appium.settings.bluetooth --es setstatus enable");
        runShellScript("adb shell settings put secure location_mode 3");
    }

    public LocalTime getDeviceLocalTime(int x, int y){

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime localtime = LocalTime.parse(LocalTime.now().plusHours(x).plusMinutes(y).format(dtf))  ;
        System.out.println("Today " + localtime);
        return localtime;
    }

    //AM PM- imperial USA metric system (depends on device iOS 12 and region)
    public String getDeviceLocalTimeAmPm(int x, int y) {

        // 1. get current system date/time
        LocalDateTime localDateTime = LocalDateTime.now().plusHours(x).plusMinutes(y);
        System.out.println("Current system date/time is :- \n"
                + localDateTime);
        // 2. get default system zone
        ZoneId zoneId = ZoneId.systemDefault();
        System.out.println("\nDefault Zone is :- \n"
                + zoneId);
        // 3. get LocalDateTime with Zone & AM/PM marker
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                .ofPattern("hh:mm a");
        Locale locale = Locale.US;
        String output = localDateTime.format(dateTimeFormatter.withLocale(locale));
        System.out.println("Today " + locale.getDisplayName(locale) + " ➙ " + output);
        System.out.println(output);
        return output;
    }

    public static void runShellScript(String command){

        int iExitValue;
        String sCommandString;
        sCommandString = command;
        CommandLine oCmdLine = CommandLine.parse(sCommandString);
        DefaultExecutor oDefaultExecutor = new DefaultExecutor();
        oDefaultExecutor.setExitValue(0);
        try{
            iExitValue = oDefaultExecutor.execute(oCmdLine);

        } catch (ExecuteException e){
            System.out.println("Fail");
            e.printStackTrace();
        } catch (IOException e){
            System.out.println("Denied");
            e.printStackTrace();
        }
    }

    public void tapByCoordinates(int x , int y){

        TouchAction touchAction = new TouchAction(driver);
        touchAction.tap(PointOption.point(x, y)).perform();
    }

    public void setAllureEnvironment() {

        allureEnvironmentWriter(
                ImmutableMap.<String, String>builder()
                        .put("Browser", "Chrome")
                        .put("Browser.Version", "70.0.3538.77")
                        .put("URL", "http://testjs.site88.net")
                        .build(), System.getProperty("user.dir")
                        + "/allure-results/");
    }

    public void refreshPage(){
        driver.getPageSource();
    }

    /*In some classes we need to verify that robot is off in order for class to begin running
    successfully. If robot is on, we will turn it off. */

}
