package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {
    @Override
    public String url() {
        return "/login";
    }

    private SelenideElement usernameField = $("#username");
    private SelenideElement passwordField = $("#password");
    private SelenideElement continueButton = $(Selectors.byXpath("//button[text()='Continue']"));
    private SelenideElement logInButton = $(Selectors.byXpath("//button[text()='Log in']"));
    //elements of Login_Page_1
    // Logo?
    //elements of Login_Page_2

    public LoginPage populateUserNameField(String username) {
        usernameField.shouldBe(Condition.visible);
        usernameField.sendKeys(username);
        usernameField.click();

        return this;
    }

    public LoginPage populatePasswordField(String password) {
        passwordField.shouldBe(Condition.visible);
        passwordField.sendKeys(password);
        passwordField.click();

        return this;
    }

    public LoginPage clickContinueButton() {
        continueButton.click();

        return this;
    }

    public LoginPage clickLogInButton() {
        logInButton.click();

        return this;
    }

    public LoginPage userNameFieldIsEmpty() {
        usernameField.shouldBe(Condition.visible);
        usernameField.shouldBe(Condition.empty);

        return this;
    }

    public LoginPage userNameFieldIsVisible() {
        usernameField.shouldBe(Condition.visible);

        return this;
    }

    public LoginPage passwordFieldIsEmpty() {
        passwordField.shouldBe(Condition.visible);
        passwordField.shouldBe(Condition.empty);

        return this;
    }

    public LoginPage continueButtonIsVisibleAndClickable() {
        continueButton.shouldBe(Condition.visible);
        continueButton.shouldBe(Condition.clickable);

        return this;
    }

    public LoginPage logInButtonIsVisibleAndClickable() {
        logInButton.shouldBe(Condition.visible);
        logInButton.shouldBe(Condition.clickable);

        return this;
    }
}
