package ui;

import api.constants.Constants;
import api.models.roles.AdminLogin;
import api.requests.specs.RequestSpecs;
import api.requests.steps.AdminSteps;
import com.codeborne.selenide.Condition;
import common.annotations.InjectAdmin;
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
                .clearSession()
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
    public void adminCanLoginWithClinicMemorisedTest(@InjectAdmin AdminLogin admin) {
        new LoginPage().open()
                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField(admin.getPassword())
                .clickLogInButton()

                .getPage(PickLocationPage.class)
                .checkItIsCorrectPage()
                .clinicLocationSelect()
                .clinicLocationClickRemember()
                .clinicLocationConfirm()
                .checkItIsCorrectPage()
                .header.clickMyAccountIconAndLogout()
                .checkItIsCorrectPage()

                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField(admin.getPassword())
                .clickLogInButton()

                .getPage(ServiceQueuesPage.class)
                .checkItIsCorrectPage()
                .header.clickChangeClinicButton()
                .clinicLocationClickRemember()
                .clinicLocationConfirm()
                .checkItIsCorrectPage()
                .header.clickMyAccountIconAndLogout()
                .checkItIsCorrectPage()

                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField(admin.getPassword())
                .clickLogInButton()
                .getPage(PickLocationPage.class)
                .checkItIsCorrectPage();
    }

    @Test
    public void userCannotLoginWithoutNameTest() {
        String emptyString = "";
        new LoginPage().open()
                .clearSession()
                .populateUserNameField(emptyString)
                .clickContinueButton()
                .checkItIsCorrectPage();
    }

    @Test
    public void cannotLoginWithWrongAdminPasswordTest(@InjectAdmin AdminLogin admin) {
        new LoginPage().open()
                .clearSession()
                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField(RandomDataGenerator.getIncorrectPassword())
                .clickLogInButton()
                .errorMessageInvalidUsernameOrPasswordIsDisplayed()
                .checkItIsCorrectPage();
    }
}
