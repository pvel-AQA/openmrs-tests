package ui;

import api.models.roles.AdminLogin;
import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.Test;
import ui.pages.LoginPage;
import ui.pages.PickLocationPage;

public class LoginTest extends BaseUiTest {

    @Test
    public void adminCanLoginTest() {
        String welcomeText = "Welcome Admin";
        AdminLogin admin = AdminLogin.getAdmin();

        new LoginPage().open()
                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField(admin.getPassword())
                .clickLogInButton()
                .getPage(PickLocationPage.class)
                .getWelcomeText().shouldBe(Condition.visible).shouldHave(Condition.text(welcomeText));
    }
}
