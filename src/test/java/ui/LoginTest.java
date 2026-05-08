package ui;

import api.models.roles.AdminLogin;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ui.pages.LoginPage;
import ui.pages.PagesHeader;
import ui.pages.PickLocationPage;
import ui.pages.ServiceQueuesPage;

import java.util.stream.Stream;

import static api.configs.Config.ADMIN_PASSWORD_CONST;
import static api.configs.Config.ADMIN_USERNAME_CONST;

public class LoginTest extends BaseUiTest {

    @Test
    public void adminCanLoginClinicMemorisedTest() {
        AdminLogin admin = AdminLogin.getAdmin();

        new LoginPage().open()
                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField(admin.getPassword())
                .clickLogInButton()
                .getPage(PagesHeader.class)
                .getChangeClinicButton().shouldBe(Condition.visible);

        Selenide.sleep(5000);
    }
    @Test
    public void adminCanLoginClinicNeedsToBeSelectedTest() {
        String welcomeText = "Welcome Admin";
        AdminLogin admin = AdminLogin.getAdmin();

        new LoginPage().open()
                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField(admin.getPassword())
                .clickLogInButton()
                .getPage(PickLocationPage.class)
                .getWelcomeText().shouldBe(Condition.visible).shouldHave(Condition.text(welcomeText));

        Selenide.sleep(5000);
    }

    @Test
    public void firstPageNoUserNameLoginTest() {
        LoginPage loginPage = new LoginPage();

        loginPage.open()
                .populateUserNameField("")
                .clickContinueButton();

        loginPage.getPasswordField().shouldNotBe(Condition.visible);
        loginPage.getUsernameField().shouldBe(Condition.visible);
        loginPage.getContinueButton().shouldBe(Condition.visible);

        Selenide.sleep(5000);
    }


    @Test
    public void wrongAdminPasswordLoginTest() {
        AdminLogin admin = AdminLogin.getAdmin();
        LoginPage loginPage = new LoginPage();

        loginPage.open()
                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField("test1234")
                .clickLogInButton();

        loginPage.getPasswordField().shouldNotBe(Condition.visible);
        loginPage.getUsernameField().shouldBe(Condition.visible);
        loginPage.getContinueButton().shouldBe(Condition.visible);

        loginPage.getErrorMessage().shouldBe(Condition.visible);
        Selenide.sleep(5000);
        loginPage.open()
                .clickErrorMessageCloseButton();
        loginPage.getErrorMessage().shouldNotBe(Condition.visible);
        Selenide.sleep(5000);
    }
}
