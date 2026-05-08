package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class LoginPage extends BasePage<LoginPage> {
    @Override
    public String url() {
        return "/login";
    }

    private SelenideElement usernameField = $("#username");
    private SelenideElement passwordField = $("#password");
    private SelenideElement continueButton = $(Selectors.byXpath("//button[text()='Continue']"));
    private SelenideElement logInButton = $(Selectors.byXpath("//button[text()='Log in']"));
    private SelenideElement errorMessage = $(".cds--inline-notification__subtitle");
    private SelenideElement errorMessageCloseButton = $(".cds--inline-notification__close-button");

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

    public LoginPage clickErrorMessageCloseButton() {
        errorMessageCloseButton.click();

        return this;
    }
}
