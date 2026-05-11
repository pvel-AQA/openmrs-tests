package ui;

import api.models.roles.AdminLogin;
import api.requests.steps.AdminSteps;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.SessionId;
import ui.components.Header;
import ui.pages.LoginPage;
import ui.pages.PickLocationPage;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends BaseUiTest {

    @Test
    public void adminCanLoginTest() {
        final String welcomeText = "Welcome Admin";
        AdminLogin admin = AdminLogin.getAdmin();

        new LoginPage().open()
                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField(admin.getPassword())
                .clickLogInButton()
                .getPage(PickLocationPage.class)
                .getWelcomeText().shouldBe(Condition.visible).shouldHave(Condition.text(welcomeText));

        SessionId sessionId = Selenide.webdriver().driver().getSessionId();
        String adminJSessionValue = AdminSteps.retrieveJSessionValue(admin);

        assertThat(sessionId.toString()).isEqualTo(adminJSessionValue);
    }
    //************************************************************************************************
    @Test
    public void adminCanLoginClinicMemorisedTest() {
        AdminLogin admin = AdminLogin.getAdmin();

        new LoginPage().open()
                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField(admin.getPassword())
                .clickLogInButton()
                .(Header.class)
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
