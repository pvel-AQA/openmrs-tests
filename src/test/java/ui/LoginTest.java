package ui;

import api.models.roles.AdminLogin;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.SessionId;
import ui.pages.LoginPage;
import ui.pages.PickLocationPage;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends BaseUiTest {

    @Test
    public void adminCanLoginTest() {
        AdminLogin admin = AdminLogin.getAdmin();

        new LoginPage().open()
                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField(admin.getPassword())
                .clickLogInButton()
                .getPage(PickLocationPage.class)
                .getWelcomeText().shouldBe(Condition.visible)
                .shouldHave(Condition.text(PickLocationPage.WELCOME_ADMIN_TEXT));

        SessionId sessionId = Selenide.webdriver().driver().getSessionId();

        assertThat(sessionId.toString()).isNotEmpty();
    }
}
