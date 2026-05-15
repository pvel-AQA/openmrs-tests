package ui;

import api.models.CreatePatientResponse;
import api.models.ui.UiPatientMandatoryInfo;
import api.requests.steps.AdminSteps;
import common.annotations.AdminSession;
import common.generators.RandomDataGenerator;
import org.junit.jupiter.api.Test;
import ui.components.Header;
import ui.pages.PickLocationPage;
import ui.pages.SearchResultsPage;
import ui.pages.ServiceQueuesPage;

import java.util.ArrayList;
import java.util.List;

public class SearchPatientTest extends BaseUiTest {
    String errorMessageText = "Sorry, there was a an error. You can try to reload this page, or contact the site administrator and quote the error code above.";
    String errorTitleText = "Error";
    String inputFieldDefaultText = "Search for a patient by name or identifier number";
    Boolean PATH_PARAM_PURGE = true;

    @Test
    @AdminSession
    public void searchDropDownShouldShowDefaultMessagesTest() {
        Header header = new PickLocationPage().open().pickOutpatientLocationAndConfirm()
                .header.clickSearchPatientIcon();

        softly.assertThat(header.getSearchInputPlaceholder())
                .isEqualTo(inputFieldDefaultText);

        softly.assertThat(header.getErrorTitleText())
                .isEqualTo(errorTitleText);

        softly.assertThat(header.getErrorMessageText())
                .isEqualTo(errorMessageText);
    }

    @Test
    @AdminSession
    void searchDropdownShouldShowCorrectResultsTest() {
        List<String> createdUuids = new ArrayList<>();
        String generatedString = RandomDataGenerator.randomString(7);
        createdUuids = AdminSteps.createPatientsForSearch(4, true, generatedString);
        String searchText = generatedString.substring(0, 4);

        new PickLocationPage().open().pickOutpatientLocationAndConfirm();
        ServiceQueuesPage searchPatients = new ServiceQueuesPage();
        searchPatients.open().header.populateSearchPatientString(searchText);

        int countFromUIDropDown = searchPatients.header.getSearchResultsCount();
        List<UiPatientMandatoryInfo> resultsFromUIDropDownList = searchPatients.header.getSearchDropdownResults();
        softly.assertThat(resultsFromUIDropDownList).hasSize(countFromUIDropDown);

        List<CreatePatientResponse> apiResults = AdminSteps.searchPatientsByString(searchText);
        softly.assertThat(resultsFromUIDropDownList).hasSize(apiResults.size());

        apiResults.forEach(apiPatient -> softly.assertThat(resultsFromUIDropDownList)
                .anyMatch(ui -> (ui.getOpenMRSuuid() + " - " + ui.getNames()).equals(apiPatient.getDisplay())));

        createdUuids.forEach(uuid -> {
            AdminSteps.deletePatientByUuid(uuid, PATH_PARAM_PURGE);
        });
    }

    @Test
    @AdminSession
    public void searchPanelActivateAndCloseTest() {
        new PickLocationPage().open().pickOutpatientLocationAndConfirm();
        ServiceQueuesPage page = new ServiceQueuesPage();
        page.header.clickSearchPatientIcon();

        softly.assertThat(page.header.isSearchIconHidden()).isTrue();
        softly.assertThat(page.header.isSearchInputVisible()).isTrue();
        softly.assertThat(page.header.isSearchInputEnabled()).isTrue();
        softly.assertThat(page.header.isResultsContainerVisible()).isTrue();
        softly.assertThat(page.header.isCloseButtonVisible()).isTrue();

        page.header.clickCloseSearchPanelButton();
        softly.assertThat(page.header.isSearchIconHidden()).isFalse();
        softly.assertThat(page.header.isSearchInputVisible()).isFalse();
        softly.assertThat(page.header.isSearchInputEnabled()).isFalse();
        softly.assertThat(page.header.isResultsContainerVisible()).isFalse();
        softly.assertThat(page.header.isClearButtonVisible()).isFalse();
        softly.assertThat(page.header.isCloseButtonVisible()).isFalse();
    }

    @Test
    @AdminSession
    public void searchPanelActivateEnterSearchStringAndCloseTest() {
        List<String> createdUuids = new ArrayList<>();
        String generatedString = RandomDataGenerator.randomString(7);
        createdUuids = AdminSteps.createPatientsForSearch(4, true, generatedString);
        String searchText = generatedString.substring(0, 4);

        new PickLocationPage().open().pickOutpatientLocationAndConfirm();
        ServiceQueuesPage page = new ServiceQueuesPage();
        page.header.populateSearchPatientString(searchText);

        softly.assertThat(page.header.isSearchIconHidden()).isTrue();
        softly.assertThat(page.header.isSearchInputVisible()).isTrue();
        softly.assertThat(page.header.isSearchInputEnabled()).isTrue();
        softly.assertThat(page.header.isResultsContainerVisible()).isTrue();
        softly.assertThat(page.header.isClearButtonVisible()).isTrue();
        softly.assertThat(page.header.isCloseButtonVisible()).isTrue();

        page.header.clickCloseSearchPanelButton();
        softly.assertThat(page.header.isSearchIconHidden()).isFalse();
        softly.assertThat(page.header.isSearchInputVisible()).isFalse();
        softly.assertThat(page.header.isSearchInputEnabled()).isFalse();
        softly.assertThat(page.header.isResultsContainerVisible()).isFalse();
        softly.assertThat(page.header.isClearButtonVisible()).isFalse();
        softly.assertThat(page.header.isCloseButtonVisible()).isFalse();

        createdUuids.forEach(uuid -> {
            AdminSteps.deletePatientByUuid(uuid, PATH_PARAM_PURGE);
        });
    }

    @Test
    @AdminSession
    void searchInputFieldPopulatedWithTextAndThenClearedTest() {
        List<String> createdUuids = new ArrayList<>();
        String generatedString = RandomDataGenerator.randomString(7);
        createdUuids = AdminSteps.createPatientsForSearch(4, true, generatedString);
        String searchText = generatedString.substring(0, 4);

        new PickLocationPage().open().pickOutpatientLocationAndConfirm();
        ServiceQueuesPage searchPatients = new ServiceQueuesPage();
        searchPatients.open().header.populateSearchPatientString(searchText);

        int countFromUIDropDown = searchPatients.header.getSearchResultsCount();
        List<UiPatientMandatoryInfo> resultsFromUIDropDownList = searchPatients.header.getSearchDropdownResults();
        softly.assertThat(resultsFromUIDropDownList).hasSize(countFromUIDropDown);

        List<CreatePatientResponse> apiResults = AdminSteps.searchPatientsByString(searchText);
        softly.assertThat(resultsFromUIDropDownList).hasSize(apiResults.size());

        apiResults.forEach(apiPatient -> softly.assertThat(resultsFromUIDropDownList)
                .anyMatch(ui -> (ui.getOpenMRSuuid() + " - " + ui.getNames()).equals(apiPatient.getDisplay())));

        searchPatients.header.clickClearTextInputFieldButton();

        softly.assertThat(searchPatients.header.getSearchInputPlaceholder())
                .isEqualTo(inputFieldDefaultText);

        softly.assertThat(searchPatients.header.getErrorTitleText())
                .isEqualTo(errorTitleText);

        softly.assertThat(searchPatients.header.getErrorMessageText())
                .isEqualTo(errorMessageText);

        createdUuids.forEach(uuid -> {
            AdminSteps.deletePatientByUuid(uuid, PATH_PARAM_PURGE);
        });
    }

    @Test
    @AdminSession
    public void searchPatientClickSearchButtonTest() {
        String generatedString = RandomDataGenerator.randomString(7);
        List<String> createdUuids = AdminSteps.createPatientsForSearch(4, true, generatedString);
        String searchText = generatedString.substring(0, 4);

        SearchResultsPage searchResultsPage = new PickLocationPage().open().pickOutpatientLocationAndConfirm()
                .header.populateSearchPatientString(searchText)
                .clickSearchButton();

        softly.assertThat(searchResultsPage.atPage()).isTrue();

        int countFromUIResultsPage = searchResultsPage.getSearchResultsCount();
        List<UiPatientMandatoryInfo> resultsFromUIResultsPage = searchResultsPage.getSearchResults();
        softly.assertThat(resultsFromUIResultsPage).hasSize(countFromUIResultsPage);

        List<CreatePatientResponse> apiResults = AdminSteps.searchPatientsByString(searchText);
        softly.assertThat(resultsFromUIResultsPage).hasSize(apiResults.size());

        apiResults.forEach(apiPatient -> softly.assertThat(resultsFromUIResultsPage)
                .anyMatch(ui -> (ui.getOpenMRSuuid() + " - " + ui.getNames()).equals(apiPatient.getDisplay())));

        createdUuids.forEach(uuid -> {
            AdminSteps.deletePatientByUuid(uuid, PATH_PARAM_PURGE);
        });
    }

    @Test
    @AdminSession
    public void searchPatientClickEnterTest() {
        List<String> createdUuids = new ArrayList<>();
        String generatedString = RandomDataGenerator.randomString(7);
        createdUuids = AdminSteps.createPatientsForSearch(4, true, generatedString);
        String searchText = generatedString.substring(0, 3);

        SearchResultsPage searchResultsPage = new PickLocationPage().open().pickOutpatientLocationAndConfirm()
                .header.populateSearchPatientString(searchText)
                .pressEnterButton();

        softly.assertThat(searchResultsPage.atPage()).isTrue();

        int countFromUIResultsPage = searchResultsPage.getSearchResultsCount();
        List<UiPatientMandatoryInfo> resultsFromUIResultsPage = searchResultsPage.getSearchResults();
        softly.assertThat(resultsFromUIResultsPage).hasSize(countFromUIResultsPage);

        List<CreatePatientResponse> apiResults = AdminSteps.searchPatientsByString(searchText);
        softly.assertThat(resultsFromUIResultsPage).hasSize(apiResults.size());

        apiResults.forEach(apiPatient -> softly.assertThat(resultsFromUIResultsPage)
                .anyMatch(ui -> (ui.getOpenMRSuuid() + " - " + ui.getNames()).equals(apiPatient.getDisplay())));

        createdUuids.forEach(uuid -> {
            AdminSteps.deletePatientByUuid(uuid, PATH_PARAM_PURGE);
        });
    }
}
