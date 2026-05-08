package ui.components;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ui.pages.PatientRegistrationPage;

import static com.codeborne.selenide.Selenide.$;

public class Header extends BaseComponent {
    private final SelenideElement self = $("#omrs-top-nav-app-container");

    private final SelenideElement addPatientButton = $(By.xpath("//button[@data-tutorial-target='add-patient']"));

    @Override
    protected SelenideElement getSelf() {
        return self;
    }

    public PatientRegistrationPage clickAddPatientButton() {
        addPatientButton.shouldBe(Condition.visible);
        addPatientButton.click();

        return new PatientRegistrationPage();
    }
}
