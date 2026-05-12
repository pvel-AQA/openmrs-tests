package ui.pages;

import api.models.CreatePatientRequest;
import api.models.ui.GenderUi;
import com.codeborne.selenide.*;
import common.generators.RandomDataGenerator;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Selenide.*;

public class PatientRegistrationPage extends BasePage<PatientRegistrationPage> {
    private final SelenideElement registerPatientButton = $(By.xpath("//button[text()='Register patient']"));
    private final SelenideElement cancelButton = $(By.xpath("//button[text()='Cancel']"));
    private final SelenideElement patientNameIsKnownYesButton = $(By.xpath("//div/span[text()=\"Patient's Name is Known?\"]/../following-sibling::div/button/span[text()='Yes']"));
    private final SelenideElement firstNameField = $("#givenName");
    private final SelenideElement middleNameField = $("#middleName");
    private final SelenideElement familyNameField = $("#familyName");
    private final ElementsCollection sexRadioButtons = $$("fieldset>div");
    private final SelenideElement dateOfBirthKnownYesButton = $(By.xpath("//div/span[text()='Date of Birth Known?']/../following-sibling::div/button/span[text()='Yes']"));
    private final SelenideElement birthdateDayPartOfField = $(By.xpath("//span[@role='spinbutton' and text()='dd']"));
    private final SelenideElement addressField = $("#address1");
    private final SelenideElement address2Field = $("#address2");
    private final SelenideElement cityVillageField = $("#cityVillage");
    private final SelenideElement stateProvinceField = $("#stateProvince");
    private final SelenideElement countryField = $("#country");
    private final SelenideElement postalCodeField = $("#postalCode");
    private final SelenideElement telephoneNumberField = $("#phone");

    @Override
    public String url() {
        return "/patient-registration";
    }

    @Override
    public Boolean atPage() {
        return patientNameIsKnownYesButton.is(Condition.visible);
    }

    public PatientRegistrationPage clickOnPatientNameIsKnownYesButton() {
        patientNameIsKnownYesButton.shouldBe(Condition.visible);
        patientNameIsKnownYesButton.click();

        return this;
    }

    public PatientRegistrationPage clickOnRegisterPatientButton() {
        registerPatientButton.shouldBe(Condition.visible);
        registerPatientButton.click();

        return this;
    }

    public PatientRegistrationPage clickOnCancelButton() {
        cancelButton.shouldBe(Condition.visible);
        cancelButton.click();

        return this;
    }

    public PatientRegistrationPage selectRandomGender() {
        String randomGender = GenderUi.getRandomGender().getGender();

        sexRadioButtons.filterBy(Condition.visible).shouldHave(CollectionCondition.size(sexRadioButtons.size()));
        sexRadioButtons.findBy(Condition.text(randomGender)).click();

        return this;
    }

    public PatientRegistrationPage populateFirstNameField(String firstname) {
        firstNameField.shouldBe(Condition.visible);
        firstNameField.sendKeys(firstname);

        return this;
    }

    public PatientRegistrationPage populateMiddleNameField(String middleName) {
        middleNameField.shouldBe(Condition.visible);
        middleNameField.sendKeys(middleName);

        return this;
    }

    public PatientRegistrationPage populateFamilyNameField(String familyName) {
        familyNameField.shouldBe(Condition.visible);
        familyNameField.sendKeys(familyName);

        return this;
    }

    public PatientRegistrationPage populateBirthdayField(String dateOfBirth) {
        birthdateDayPartOfField.shouldBe(Condition.visible);
        birthdateDayPartOfField.sendKeys(dateOfBirth);

        return this;
    }

    public PatientRegistrationPage clickOnDateOfBirthKnownYesButton() {
        dateOfBirthKnownYesButton.shouldBe(Condition.visible);
        dateOfBirthKnownYesButton.click();

        return this;
    }

    public PatientRegistrationPage populateAddressField(String address) {
        addressField.shouldBe(Condition.visible);
        addressField.sendKeys(address);

        return this;
    }

    public PatientRegistrationPage populateAddress2Field(String address) {
        address2Field.shouldBe(Condition.visible);
        address2Field.sendKeys(address);

        return this;
    }

    public PatientRegistrationPage populateCityVillageField(String cityVillage) {
        cityVillageField.shouldBe(Condition.visible);
        cityVillageField.sendKeys(cityVillage);

        return this;
    }

    public PatientRegistrationPage populateCountryField(String country) {
        countryField.shouldBe(Condition.visible);
        countryField.sendKeys(country);

        return this;
    }

    public PatientRegistrationPage populateStateProvinceField(String stateProvince) {
        stateProvinceField.shouldBe(Condition.visible);
        stateProvinceField.sendKeys(stateProvince);

        return this;
    }

    public PatientRegistrationPage populatePostalCodeField(String postalCode) {
        postalCodeField.shouldBe(Condition.visible);
        postalCodeField.sendKeys(postalCode);

        return this;
    }

    public PatientRegistrationPage populateTelephoneNumberField(String number) {
        telephoneNumberField.shouldBe(Condition.visible);
        telephoneNumberField.sendKeys(number);

        return this;
    }

    public PatientSummaryPage registerPatientWithAllFieldsPopulatedCorrectly(CreatePatientRequest patient) {
        clickOnPatientNameIsKnownYesButton();
        populateFirstNameField(patient.getPerson().getNames().getFirst().getGivenName());
        populateMiddleNameField(patient.getPerson().getNames().getFirst().getMiddleName());
        populateFamilyNameField(patient.getPerson().getNames().getFirst().getFamilyName());
        selectRandomGender();
        clickOnDateOfBirthKnownYesButton();
        populateBirthdayField(patient.getPerson().getBirthdate());
        populateAddressField(patient.getPerson().getAddresses().getFirst().getAddress1());
        populateAddress2Field(patient.getPerson().getAddresses().getFirst().getAddress2());
        populateCityVillageField(patient.getPerson().getAddresses().getFirst().getCityVillage());
        populateStateProvinceField(patient.getPerson().getAddresses().getFirst().getStateProvince());
        populateCountryField(patient.getPerson().getAddresses().getFirst().getCountry());
        populatePostalCodeField(patient.getPerson().getAddresses().getFirst().getPostalCode());
        populateTelephoneNumberField(patient.getPerson().getAttributes().getFirst().getValue());

        clickOnRegisterPatientButton();

        return new PatientSummaryPage();
    }

}
