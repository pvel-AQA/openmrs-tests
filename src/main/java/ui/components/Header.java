package ui.components;

import api.models.CreatePatientResponse;
import api.models.PatientResponse;
import com.codeborne.selenide.*;
import org.openqa.selenium.By;
import ui.pages.PatientRegistrationPage;
import ui.pages.PickLocationPage;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Header extends BaseComponent {
    private final SelenideElement self = $("#omrs-top-nav-app-container");

    private final SelenideElement addPatientButton = $(By.xpath("//button[@data-tutorial-target='add-patient']"));
    private final SelenideElement searchPatientButton = $("button[data-testid='searchPatientIcon']");
    //private final SelenideElement searchTextInputField = $("button[data-testid='patientSearchBar']");
    private final SelenideElement searchTextInputField = $("[data-testid='patientSearchBar']");
    private final SelenideElement changeClinicButton = $("button[aria-label='Change location']");
    private final SelenideElement searchButton = $(Selectors.byText("Search"));

    private final SelenideElement searchResultsCount = $("[class*='resultsText']");
    private final SelenideElement searchResultsContainer = $("[data-testid='floatingSearchResultsContainer']");


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

    public Header enterSearchPatientString(String searchText) {
        searchPatientButton.shouldBe(Condition.visible);
        searchPatientButton.click();
        Selenide.sleep(5000);
        //Selenide.switchTo().frame("-esm-patient-search__patient-search-bar__searchArea___AwmMr");
        searchTextInputField.shouldBe(Condition.visible, Condition.enabled);
        searchTextInputField.sendKeys(searchText);
        Selenide.sleep(5000);
        //searchButton.click();;
        return this;
    }

    public int searchResultsCount(String searchText) {
        String text = searchResultsCount.getText();
        int count = Integer.parseInt(text.split(" ")[0]);

        return count;
    }

    public List<CreatePatientResponse> foundPatientsDisplayedInDropDown(String searchText) {
        ElementsCollection patientsDisplayedInDropDownResults = $("[data-testid='floatingSearchResultsContainer']").findAll("a");
        System.out.println(patientsDisplayedInDropDownResults);
        //return patientsDisplayedInDropDownResults;

        //compare that count of API list = count of Displayed list
        String text = searchResultsCount.getText();
        int searchResultsCountInDropDown = Integer.parseInt(text.split(" ")[0]);
        int countActuallyDisplayedInDropDown = patientsDisplayedInDropDownResults.size();
        assertEquals(searchResultsCountInDropDown, countActuallyDisplayedInDropDown);

        return patientsDisplayedInDropDownResults.stream()
                .map(element -> {
                    CreatePatientResponse patient = new CreatePatientResponse();
                    patient.setDisplay(element.getText());
                    patient.setUuid(element.getAttribute("href")
                            .replace("/patient/", "")
                            .replace("/chart/", "")); // adjust to your URL pattern
                    return patient;
                })
                .collect(Collectors.toList());
    }

    //how to get server error 404 in black message modal?




}
