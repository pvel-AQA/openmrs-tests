package ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.codeborne.selenide.Selenide.$;

public class LoginUserTest {
    @BeforeAll
    public static void setupSelenoid(){
        //Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "https://o3.openmrs.org/openmrs/spa"; //"http://192.168.0.16:3000";
        Configuration.browser = "chrome";
        //Configuration.browserVersion = "128.0";
        Configuration.browserSize = "1920x1080";

        Configuration.browserCapabilities.setCapability("selenoid:options", Map.of("enableVNC", true, "enableLog", true));
    }

    @Test
    public void adminCanLoginWithCorrectDataTest(){
        Selenide.open("/login");

        $(Selectors.byId("username")).sendKeys("admin");
        Selenide.sleep(5000);
        $(Selectors.byText("Continue")).click();

        //check that Username contains "admin"?
        //$(Selectors.byId("username")).sendKeys("Admin123");
        Selenide.sleep(5000);

    }
}
