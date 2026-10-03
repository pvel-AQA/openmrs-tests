package ui;

import api.BaseTest;
import api.configs.Config;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import common.extensions.AdminSessionExtension;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
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
        SelenideLogger.addListener("AllureSelenide", new AllureSelenide());

        Configuration.browserCapabilities.setCapability("selenoid:options",
                Map.of("enableVNC", true, "enableLog", true));
    }

    @BeforeEach
    public void setupAllureBrowserContext(TestInfo testInfo) {
        String browser = Configuration.browser;

        String displayName = testInfo.getDisplayName() + " [" + browser.toUpperCase() + "]";

        Allure.getLifecycle().updateTestCase(testCase -> {
            testCase.setName(displayName);
            testCase.setHistoryId(testCase.getHistoryId() + "-" + browser);
        });

        Allure.label("browser", browser);
        Allure.label("tag", browser);
        Allure.parameter("Browser", browser);
    }

    @AfterEach
    public void tearDown() {
        Selenide.closeWebDriver();
    }
}
