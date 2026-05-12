package ui;

import api.models.CreatePatientRequest;
import api.models.roles.AdminLogin;
import api.requests.steps.AdminSteps;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import common.annotations.AdminSession;
import common.generators.RandomDataGenerator;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.remote.SessionId;
import ui.components.Header;
import ui.pages.LoginPage;
import ui.pages.PickLocationPage;
import ui.pages.ServiceQueuesPage;

import static com.codeborne.selenide.Selenide.switchTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

        assertThat(sessionId.toString()).isNotEmpty();
    }

    @Test
    @AdminSession
    public void adminCanSetClinicMemorisedTest() {
        new PickLocationPage().open()
                .pickOutpatientLocationClickRememberMyLocationAndConfirm()
                .atPage();

        Selenide.sleep(15000);
        //uncheck clinic
    }

    @Test
    @AdminSession
    public void adminCanLoginClinicMemorisedTest() {
        AdminLogin admin = AdminLogin.getAdmin();
        //precondition: check clinic

        new LoginPage().open()
                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField(admin.getPassword())
                .clickLogInButton()
                .getPage(ServiceQueuesPage.class)
                .atPage();

        Selenide.sleep(15000);
        //uncheck clinic
    }


    @Test
    public void firstPageNoUserNameLoginTest() {
        new LoginPage().open()
                .populateUserNameField("")
                .clickContinueButton()
                .atPage();

        Selenide.sleep(5000);
    }


    @Test
    public void wrongAdminPasswordLoginTest() {
        AdminLogin admin = AdminLogin.getAdmin();

        new LoginPage().open()
                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField("test1234")
                .clickLogInButton()
                .errorMessageInvalidUsernameOrPasswordIsDisplayed()
                .atPage();

        Selenide.sleep(5000);
    }

    //Tests for empty username and password

}
