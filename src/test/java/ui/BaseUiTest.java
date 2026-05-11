package ui;

import api.BaseTest;
import api.configs.Config;
import com.codeborne.selenide.Configuration;
import common.extensions.AdminSessionExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

@ExtendWith(AdminSessionExtension.class)
public class BaseUiTest extends BaseTest {

    @BeforeAll
    public static void setup() {
        Configuration.baseUrl = Config.getProperty(Config.UI_BASE_URL_CONST);
        Configuration.browser = Config.getProperty(Config.BROWSER_CONST);
        Configuration.remote = Config.getProperty(Config.BROWSER_REMOTE_CONST);
        Configuration.browserSize = Config.getProperty(Config.BROWSER_SIZE_CONST);

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true));
    }
}
