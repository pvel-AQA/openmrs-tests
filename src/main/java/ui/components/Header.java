package ui.components;

import api.models.ui.UiPatientMandatoryInfo;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ui.pages.PatientRegistrationPage;
import ui.pages.PickLocationPage;
import ui.pages.SearchResultsPage;
import ui.pages.ServiceQueuesPage;
import ui.parsers.PatientSearchResultParser;

import java.util.List;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.$;

public class Header extends BaseComponent {
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

    public Header(SelenideElement self) {
        super(self);
    }


    @Override
    protected SelenideElement getSelf() {
        return self;
    }

    public PatientRegistrationPage clickAddPatientButton() {
        addPatientButton.shouldBe(Condition.visible);
        addPatientButton.click();

        return new PatientRegistrationPage();
    }

    public PickLocationPage clickChangeClinicButton() {
        changeClinicButton.shouldBe(Condition.visible).click();

        return new PickLocationPage();
    }

    public ServiceQueuesPage populateSearchPatientString(String searchText) {
        searchPatientIcon.shouldBe(Condition.visible).click();
        searchTextInputField.shouldBe(Condition.visible, Condition.enabled);
        searchTextInputField.sendKeys(searchText);
        searchResultsContainer.shouldBe(Condition.visible);

        return getPage(ServiceQueuesPage.class);
    }

    public boolean isSearchIconHidden() {
        return searchPatientIcon.is(Condition.hidden);
    }

    public boolean isSearchInputVisible() {
        return searchTextInputField.is(Condition.visible);
    }

    public boolean isSearchInputEnabled() {
        return searchTextInputField.is(Condition.enabled);
    }

    public boolean isResultsContainerVisible() {
        return searchResultsContainer.is(Condition.visible);
    }

    public boolean isClearButtonVisible() {
        return clearTextInputFieldButton.is(Condition.visible);
    }

    public boolean isCloseButtonVisible() {
        return closeSearchPanelButton.is(Condition.visible);
    }


    public Header clickSearchPatientIcon() {
        searchPatientIcon.shouldBe(Condition.visible).click();

        return this;
    }

    public ServiceQueuesPage clickCloseSearchPanelButton() {
        closeSearchPanelButton.shouldBe(Condition.visible).click();

        return new ServiceQueuesPage();
    }

    public Header clickClearTextInputFieldButton() {
        clearTextInputFieldButton.shouldBe(Condition.visible).click();

        return this;
    }

    public SearchResultsPage pressEnterButton() {
        searchTextInputField.shouldBe(Condition.visible).click();
        searchResultsCount.click();
        searchTextInputField.click();
        searchTextInputField.pressEnter();

        return getPage(SearchResultsPage.class);
    }

    public SearchResultsPage clickSearchButton() {
        searchResultsCount.click();
        searchButton.shouldBe(Condition.visible).click();

        return getPage(SearchResultsPage.class);
    }

    public String getSearchInputPlaceholder() {
        return searchTextInputField
                .shouldBe(Condition.visible)
                .getAttribute("placeholder");
    }

    public String getErrorTitleText() {
        return errorTitle
                .shouldBe(Condition.visible)
                .getText();
    }

    public String getErrorMessageText() {
        return errorMessage
                .shouldBe(Condition.visible)
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
