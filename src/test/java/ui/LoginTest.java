package ui;

import api.constants.Constants;
import api.models.roles.AdminLogin;
import api.requests.specs.RequestSpecs;
import api.requests.steps.AdminSteps;
import com.codeborne.selenide.Condition;
import common.annotations.AdminSession;
import common.generators.RandomPasswordGenerator;
import common.annotations.InjectAdmin;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.SessionId;
import ui.pages.LoginPage;
import ui.pages.PickLocationPage;
import ui.pages.ServiceQueuesPage;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends BaseUiTest {

    @Test
    public void adminCanLoginTest(@InjectAdmin AdminLogin admin) {
        new LoginPage().open()
                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField(admin.getPassword())
                .clickLogInButton()
                .getPage(PickLocationPage.class)
                .getWelcomeText().shouldBe(Condition.visible)
                .shouldHave(Condition.text(PickLocationPage.WELCOME_ADMIN_TEXT));

        String browserSessionCookieValue = RequestSpecs.getBrowserSessionCookieValue();
        String identifierTypeUuid = AdminSteps.getIdentifierTypeUuidUsingJSessionId(browserSessionCookieValue);

        assertThat(identifierTypeUuid).isEqualTo(Constants.IDENTIFIER_TYPE_UUID);
    }

    @Test
    @AdminSession
    public void adminCanSetClinicMemorisedTest() {
        assertThat(new PickLocationPage().open()
                .pickOutpatientLocationClickRememberMyLocationAndConfirm()
                .pageIsReady()).isTrue();
    }

    @Test
    public void adminCanLoginClinicMemorisedTest(@InjectAdmin AdminLogin admin) {
        assertThat(new LoginPage().open()
                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField(admin.getPassword())
                .clickLogInButton()
                .getPage(ServiceQueuesPage.class)
                .pageIsReady()).isTrue();
    }

    @Test
    public void firstPageNoUserNameLoginTest() {
        assertThat(new LoginPage().open()
                .populateUserNameField("")
                .clickContinueButton()
                .pageIsReady()).isTrue();
    }

    @Test
    public void wrongAdminPasswordLoginTest(@InjectAdmin AdminLogin admin) {
        assertThat(new LoginPage().open()
                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField(RandomPasswordGenerator.generate())
                .clickLogInButton()
                .errorMessageInvalidUsernameOrPasswordIsDisplayed()
                .pageIsReady()).isTrue();
    }
}
