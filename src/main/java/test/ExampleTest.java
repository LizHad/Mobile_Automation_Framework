package test;

import Google_Sheets_API.GoogleSheetsIntegration;
import io.qameta.allure.Description;
import object.ExampleAppPage;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class ExampleTest extends BaseTest {
    //@Test(dataProvider = "serialNumbers")
    @Parameters("header")
    @Test
    @Description(("1. Verify app landing page title\n" +
            "2. Select Skimmi from list\n"))

    public void A01_Print_Serial1_Test(String header) throws GeneralSecurityException, IOException {
        //The following test activates desired app, prints out timestamp and desired app value (element text) into google sheet.
        // It prints values below desired header.
        //Note that in GoogleSheetsIntegration class, there is commented code that simply prints on a new line each time if needed.
        
        ExampleAppPage exampleAppPage = new ExampleAppPage(driver);
        GoogleSheetsIntegration.writeToSheet(header, exampleAppPage.getValueFromApp());

    }

}
