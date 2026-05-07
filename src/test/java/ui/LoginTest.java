package ui;

import api.models.roles.AdminLogin;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ui.pages.LoginPage;
import ui.pages.PickLocationPage;

import java.util.stream.Stream;

import static api.configs.Config.ADMIN_PASSWORD_CONST;
import static api.configs.Config.ADMIN_USERNAME_CONST;

public class LoginTest extends BaseUiTest {

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
    public void adminCanLoginClinicMemorisedTest() {
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
        String welcomeText = "Welcome Admin";
        AdminLogin admin = AdminLogin.getAdmin();

        new LoginPage().open()
                .populateUserNameField("")
                .clickContinueButton();
                //.get().shouldBe(Condition.visible).shouldHave(Condition.text(welcomeText));

        Selenide.sleep(5000);
    }

    public static Stream<Arguments> negativeLoginData() {
        AdminLogin admin = AdminLogin.getAdmin();
        return Stream.of(
                Arguments.of(admin.getUsername(), "12345"),
                Arguments.of(admin.getUsername(), ""),
                Arguments.of("test", admin.getPassword()),
                Arguments.of("", admin.getPassword()));
    }

    @MethodSource("negativeLoginData")
    @ParameterizedTest
    public void cannotLoginTest(String userName, String password) {
        //String welcomeText = "Welcome Admin";
        AdminLogin admin = AdminLogin.getAdmin();

        new LoginPage().open()
                .populateUserNameField(userName)
                .clickContinueButton()
                .populatePasswordField(password)
                .clickLogInButton();
                //.getPage(PickLocationPage.class)
                //.getWelcomeText().shouldBe(Condition.visible).shouldHave(Condition.text(welcomeText));

        Selenide.sleep(5000);

        // check Error: Invalid username or password - is shown till you close it
        // check that again screen with only UsernameField and "Continue"-btn is visible;
        // check that error message can be closed

    }

    @Test
    public void wrongAdminPasswordLoginTest() {
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
    public void emptyUserLoginTest() {
        String welcomeText = "Welcome Admin";
        AdminLogin admin = AdminLogin.getAdmin();

        new LoginPage().open()
                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField(admin.getPassword())
                .clickLogInButton()
                .getPage(PickLocationPage.class)
                .getWelcomeText().shouldBe(Condition.visible).shouldHave(Condition.text(welcomeText));
        //  "Drop-down message": Please fill out this field.
        Selenide.sleep(5000);
    }

    @Test
    public void emptyPasswordLoginTest() {
        String welcomeText = "Welcome Admin";
        AdminLogin admin = AdminLogin.getAdmin();

        new LoginPage().open()
                .populateUserNameField(admin.getUsername())
                .clickContinueButton()
                .populatePasswordField(admin.getPassword())
                .clickLogInButton()
                .getPage(PickLocationPage.class)
                .getWelcomeText().shouldBe(Condition.visible).shouldHave(Condition.text(welcomeText));
        //  "Drop-down message": Please fill out this field.
        Selenide.sleep(5000);
    }

}
