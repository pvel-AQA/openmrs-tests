package ui.pages;

import api.models.ui.Messages;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoginPage extends BasePage<LoginPage> {
    @Override
    public String url() {
        return "/login";
    }

    public LoginPage pageIsReady() {
        passwordField.shouldNotBe(Condition.visible);
        usernameField.shouldBe(Condition.visible);
        continueButton.shouldBe(Condition.visible);

        return this;
    }

    private final SelenideElement usernameField = $("#username");
    private final SelenideElement passwordField = $("#password");
    private final SelenideElement continueButton = $(Selectors.byXpath("//button[text()='Continue']"));
    private final SelenideElement logInButton = $(Selectors.byXpath("//button[text()='Log in']"));

    private final SelenideElement errorWrapper = $(".cds--inline-notification__text-wrapper");
    private final SelenideElement errorTitle = $(".cds--inline-notification__title");
    private final SelenideElement errorMessage = $(".cds--inline-notification__subtitle");
    private final SelenideElement errorMessageCloseButton = $(".cds--inline-notification__close-button");

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

    public LoginPage errorMessageInvalidUsernameOrPasswordIsDisplayed(){
        errorWrapper.shouldBe(Condition.visible);
        assertEquals(Messages.ERROR_TITLE_TEXT.getText(), errorTitle.getText());
        assertEquals(Messages.LOGIN_ERROR_MESSAGE.getText(), errorMessage.getText() );
        errorMessageCloseButton.click();

        return this;
    }
}
