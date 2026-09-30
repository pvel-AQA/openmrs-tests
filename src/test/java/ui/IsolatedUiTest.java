package ui;

import api.models.roles.AdminLogin;
import common.annotations.InjectAdmin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;
import ui.pages.LoginPage;
import ui.pages.PickLocationPage;
import ui.pages.ServiceQueuesPage;

@Isolated
public class IsolatedUiTest extends BaseUiTest {

    @Test
    public void adminCanLoginWithClinicMemorisedTest(@InjectAdmin AdminLogin admin) {
        new LoginPage().open()
                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField(admin.getPassword())
                .clickLogInButton()

                .getPage(PickLocationPage.class)
                .selectClinicLocation()
                .clinicLocationClickRemember()
                .confirmClinicLocation()
                .header.clickMyAccountIconAndLogout()

                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField(admin.getPassword())
                .clickLogInButton()

                .getPage(ServiceQueuesPage.class)
                .header.clickChangeClinicButton()
                .clinicLocationClickRemember()
                .confirmClinicLocation()
                .header.clickMyAccountIconAndLogout()

                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField(admin.getPassword())
                .clickLogInButton()
                .getPage(PickLocationPage.class)
                .checkItIsCorrectPage();
    }
}
