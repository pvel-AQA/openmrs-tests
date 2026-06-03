package ui;

import api.constants.Constants;
import api.models.roles.AdminLogin;
import api.requests.specs.RequestSpecs;
import api.requests.steps.AdminSteps;
import com.codeborne.selenide.Condition;
import common.annotations.InjectAdmin;
import common.annotations.Skip;
import common.generators.RandomDataGenerator;
import org.junit.jupiter.api.Test;
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

    @Skip(reason = "flaky test, that should be updated")
    @Test
    public void adminCanLoginClinicMemorisedTest(@InjectAdmin AdminLogin admin) {
        new LoginPage().open()
                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField(admin.getPassword())
                .clickLogInButton()
                .getPage(ServiceQueuesPage.class)
                .pageIsReady();
    }

    @Test
    public void firstPageNoUserNameLoginTest() {
        String emptyString = "";
        new LoginPage().open()
                .populateUserNameField(emptyString)
                .clickContinueButton()
                .pageIsReady();
    }

    //@Skip(reason = "flaky test, that should be updated")
    @Test
    public void wrongAdminPasswordLoginTest(@InjectAdmin AdminLogin admin) {
        new LoginPage().open()
                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField(RandomDataGenerator.getIncorrectPassword())
                .clickLogInButton()
                .errorMessageInvalidUsernameOrPasswordIsDisplayed()
                .pageIsReady();
    }
}
