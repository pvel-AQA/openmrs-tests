package ui.components;

import api.models.ui.UiPatientMandatoryInfo;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ui.pages.PatientRegistrationPage;
import ui.pages.PickLocationPage;
import ui.pages.SearchResultsPage;
import ui.parsers.PatientSearchResultParser;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.hidden;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class Header extends BaseComponent {
    private final SelenideElement self = $("#omrs-top-nav-app-container");

    private final SelenideElement addPatientButton = $(By.xpath("//button[@data-tutorial-target='add-patient']"));
    private final SelenideElement searchPatientIcon = $("button[data-testid='searchPatientIcon']");
    private final SelenideElement searchTextInputField = $("input[data-testid='patientSearchBar']");
    private final SelenideElement clearTextInputFieldButton = $("button[aria-label='Clear']");
    private final SelenideElement changeClinicButton = $("button[aria-label='Change location']");
    private final SelenideElement searchButton = $(Selectors.byText("Search"));
    private final SelenideElement closeSearchPanelButton = $("button[data-testid='closeSearchIcon']");
    private final SelenideElement searchResultsCount = $("[class*='resultsText']");
    private final SelenideElement searchResultsContainer = $("[data-testid='floatingSearchResultsContainer']");
    private final SelenideElement errorTitle = $("p[class*='errorMessage']");
    private final SelenideElement errorMessage = $("p[class*='errorCopy']");

    private final PatientSearchResultParser parser = new PatientSearchResultParser();

    @Override
    protected SelenideElement getSelf() {
        return self;
    }

    public PatientRegistrationPage clickAddPatientButton() {
        addPatientButton.shouldBe(visible);
        addPatientButton.click();

        return new PatientRegistrationPage();
    }

    public PickLocationPage clickChangeClinicButton() {
        changeClinicButton.shouldBe(visible).click();

        return new PickLocationPage();
    }

    public Header populateSearchPatientString(String searchText) {
        searchPatientIcon.shouldBe(visible).click();
        searchTextInputField.shouldBe(visible, Condition.enabled);
        searchTextInputField.sendKeys(searchText);
        searchResultsContainer.shouldBe(visible);

        return this;
    }

    public Header shouldHaveSearchPanelClosed() {
        searchPatientIcon.shouldBe(visible);
        searchTextInputField.shouldNotBe(visible);
        //searchTextInputField.shouldNotBe(Condition.enabled);
        searchResultsContainer.shouldNotBe(visible);
        clearTextInputFieldButton.shouldNotBe(visible);
        closeSearchPanelButton.shouldNotBe(visible);

        return this;
    }

    public Header shouldHaveSearchPanelOpen(boolean expectClearButton) {
        searchPatientIcon.shouldBe(hidden);
        searchTextInputField.shouldBe(visible);
        searchTextInputField.shouldBe(Condition.enabled);
        searchResultsContainer.shouldBe(visible);
        closeSearchPanelButton.shouldBe(visible);
        clearTextInputFieldButton.shouldBe(expectClearButton ? visible : hidden);

        return this;
    }

    public Header clickSearchPatientIcon() {
        searchPatientIcon.shouldBe(visible).click();

        return this;
    }

    public Header clickCloseSearchPanelButton() {
        closeSearchPanelButton.shouldBe(visible).click();

        return this;
    }

    public Header clickClearTextInputFieldButton() {
        clearTextInputFieldButton.shouldBe(visible).click();

        return this;
    }

    public SearchResultsPage pressEnterButton() {
        searchTextInputField.shouldBe(visible).click();
        searchResultsCount.click();
        searchTextInputField.click();
        searchTextInputField.pressEnter();

        return getPage(SearchResultsPage.class);
    }

    public SearchResultsPage clickSearchButton() {
        searchResultsCount.click();
        searchButton.shouldBe(visible).click();

        return getPage(SearchResultsPage.class);
    }

    public String getSearchInputPlaceholder() {
        return searchTextInputField
                .shouldBe(visible)
                .getAttribute("placeholder");
    }

    public String getErrorTitleText() {
        return errorTitle
                .shouldBe(visible)
                .getText();
    }

    public String getErrorMessageText() {
        return errorMessage
                .shouldBe(visible)
                .getText();
    }

    public int getSearchResultsCount() {
        String text = searchResultsCount.getText();

        return Integer.parseInt(text.split(" ")[0]);
    }

    public List<UiPatientMandatoryInfo> getSearchDropdownResults() {

        return searchResultsContainer
                .findAll("a")
                .stream()
                .map(parser::parse)
                .collect(Collectors.toList());
    }
}
