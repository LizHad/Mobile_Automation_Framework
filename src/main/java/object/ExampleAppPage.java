package object;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import object.BasePage;

public class ExampleAppPage extends BasePage {
    public ExampleAppPage(AppiumDriver driver) {
        super(driver);
    }
    @AndroidFindBy(id="element")
    private MobileElement txtElement;

    public String getValueFromApp(){
        waitForElementVisibility(txtElement);
        return txtElement.getText();
    }
}
