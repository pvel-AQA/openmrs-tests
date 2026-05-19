package ui;

import api.models.CreatePatientResponse;
import api.models.ui.Messages;
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

import static api.constants.Constants.PATH_PARAM_PURGE;

public class SearchPatientTest extends BaseUiTest {
    @Test
    @AdminSession
    public void searchDropDownShouldShowDefaultMessagesTest() {
        Header header = new PickLocationPage().open().pickOutpatientLocationAndConfirm()
                .header.clickSearchPatientIcon();

        softly.assertThat(header.getSearchInputPlaceholder())
                .isEqualTo(Messages.SEARCH_INPUT_FIELD_DEFAULT_TEXT.getText());

        softly.assertThat(header.getErrorTitleText())
                .isEqualTo(Messages.ERROR_TITLE_TEXT.getText());

        softly.assertThat(header.getErrorMessageText())
                .isEqualTo(Messages.SEARCH_RESULTS_ERROR_MESSAGE.getText());
    }

    @Test
    @AdminSession
    void searchDropdownShouldShowCorrectResultsTest() {

        String generatedString = RandomDataGenerator.randomString(7);
        List<String> createdUuids createdUuids = AdminSteps.createPatientsForSearch(4, true, generatedString);
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
        String generatedString = RandomDataGenerator.randomString(7);
        List<String> createdUuids = AdminSteps.createPatientsForSearch(4, true, generatedString);
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

        for (String uuid : createdUuids) {
            AdminSteps.deletePatientByUuid(uuid, PATH_PARAM_PURGE);
        }
    }

    @Test
    @AdminSession
    void searchInputFieldPopulatedWithTextAndThenClearedTest() {
        String generatedString = RandomDataGenerator.randomString(7);
        List<String>  createdUuids = AdminSteps.createPatientsForSearch(4, true, generatedString);
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
                .isEqualTo(Messages.SEARCH_INPUT_FIELD_DEFAULT_TEXT.getText());

        softly.assertThat(searchPatients.header.getErrorTitleText())
                .isEqualTo(Messages.ERROR_TITLE_TEXT.getText());

        softly.assertThat(searchPatients.header.getErrorMessageText())
                .isEqualTo(Messages.SEARCH_RESULTS_ERROR_MESSAGE.getText());

        for (String uuid : createdUuids) {
            AdminSteps.deletePatientByUuid(uuid, PATH_PARAM_PURGE);
        }
    }

    @Test
    //@RepeatedTest(30)
    @AdminSession
    public void searchPatientClickSearchButtonTest() {
        String generatedString = RandomDataGenerator.randomString(7);
        List<String> createdUuids = AdminSteps.createPatientsForSearch(4, true, generatedString);
        String searchText = generatedString.substring(0, 4);

        new PickLocationPage().open().pickOutpatientLocationAndConfirm();

        ServiceQueuesPage searchPatients = new ServiceQueuesPage();
        searchPatients.open().header.populateSearchPatientString(searchText);

        int countFromUIDropDown = searchPatients.header.getSearchResultsCount();
        List<UiPatientMandatoryInfo> resultsFromUIDropDownList = searchPatients.header.getSearchDropdownResults();
        softly.assertThat(resultsFromUIDropDownList).hasSize(countFromUIDropDown);

        SearchResultsPage searchResultsPage = searchPatients.clickSearchButton();

        softly.assertThat(searchResultsPage.pageIsReady()).isTrue();

        int countFromUIResultsPage = searchResultsPage.getSearchResultsCount();
        List<UiPatientMandatoryInfo> resultsFromUIResultsPage = searchResultsPage.getSearchResults();
        softly.assertThat(resultsFromUIResultsPage).hasSize(countFromUIResultsPage);

        List<CreatePatientResponse> apiResults = AdminSteps.searchPatientsByString(searchText);
        softly.assertThat(resultsFromUIResultsPage).hasSize(apiResults.size());

        apiResults.forEach(apiPatient -> softly.assertThat(resultsFromUIResultsPage)
                .anyMatch(ui -> (ui.getOpenMRSuuid() + " - " + ui.getNames()).equals(apiPatient.getDisplay())));

        for (String uuid : createdUuids) {
            AdminSteps.deletePatientByUuid(uuid, PATH_PARAM_PURGE);
        }
    }

    @Test
    //@RepeatedTest(30)
    @AdminSession
    public void searchPatientClickEnterTest() {
        String generatedString = RandomDataGenerator.randomString(7);
        List<String> createdUuids = AdminSteps.createPatientsForSearch(4, true, generatedString);
        String searchText = generatedString.substring(0, 3);

        new PickLocationPage().open().pickOutpatientLocationAndConfirm();

        ServiceQueuesPage searchPatients = new ServiceQueuesPage();
        searchPatients.open().header.populateSearchPatientString(searchText);

        int countFromUIDropDown = searchPatients.header.getSearchResultsCount();
        List<UiPatientMandatoryInfo> resultsFromUIDropDownList = searchPatients.header.getSearchDropdownResults();
        softly.assertThat(resultsFromUIDropDownList).hasSize(countFromUIDropDown);

        SearchResultsPage searchResultsPage = searchPatients.pressEnterButton();

        softly.assertThat(searchResultsPage.pageIsReady()).isTrue();

        int countFromUIResultsPage = searchResultsPage.getSearchResultsCount();
        List<UiPatientMandatoryInfo> resultsFromUIResultsPage = searchResultsPage.getSearchResults();

        softly.assertThat(resultsFromUIResultsPage).hasSize(countFromUIResultsPage);

        List<CreatePatientResponse> apiResults = AdminSteps.searchPatientsByString(searchText);
        softly.assertThat(resultsFromUIResultsPage).hasSize(apiResults.size());

        //assert Search results count on the Search Results page too
        apiResults.forEach(apiPatient -> softly.assertThat(resultsFromUIResultsPage)
                .anyMatch(ui -> (ui.getOpenMRSuuid() + " - " + ui.getNames()).equals(apiPatient.getDisplay())));

        for (String uuid : createdUuids) {
            AdminSteps.deletePatientByUuid(uuid, PATH_PARAM_PURGE);
        }
    }
}
