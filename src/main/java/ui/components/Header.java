package ui.components;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ui.pages.PatientRegistrationPage;
import ui.pages.PickLocationPage;

import static com.codeborne.selenide.Selenide.$;

public class Header extends BaseComponent {
    private final SelenideElement self = $("#omrs-top-nav-app-container");

    private final SelenideElement addPatientButton = $(By.xpath("//button[@data-tutorial-target='add-patient']"));
    //private final SelenideElement changeClinicButton = $("button[aria-label='Change location']");
    private final SelenideElement searchPatientButton = $("button[data-testid='searchPatientIcon']");
    private final SelenideElement searchTextInputField = $("button[data-testid='patientSearchBar']");
    private final SelenideElement changeClinicButton = $("button[aria-label='Change location']");


    @Override
    protected SelenideElement getSelf() {
        return self;
    }

    public PatientRegistrationPage clickAddPatientButton() {
        addPatientButton.shouldBe(Condition.visible);
        addPatientButton.click();

        return new PatientRegistrationPage();
    }

    public Header clickSearchPatientButton() {
        searchPatientButton.shouldBe(Condition.visible);
        searchPatientButton.click();

        return new Header();
    }

    public PickLocationPage clickChangeClinicButton(){
        changeClinicButton.shouldBe(Condition.visible);
        changeClinicButton.click();

        return new PickLocationPage();
    }
}
