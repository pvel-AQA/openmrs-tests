package ui;

import api.models.CreatePatientResponse;
import api.models.ui.Messages;
import api.models.ui.UiPatientMandatoryInfo;
import api.requests.steps.AdminSteps;
import common.annotations.AdminSession;
import common.annotations.Skip;
import common.generators.RandomDataGenerator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import ui.components.Header;
import ui.pages.PickLocationPage;
import ui.pages.SearchResultsPage;
import ui.pages.ServiceQueuesPage;

import java.util.ArrayList;
import java.util.List;

import static api.constants.Constants.PATH_PARAM_PURGE;

public class SearchPatientTest extends BaseUiTest {
    private static List<String> createdUuids = new ArrayList<>();

    @Skip(reason = "flaky test, that should be updated")
    @Test
    @AdminSession
    public void searchDropDownShowsDefaultMessagesTest() {
        Header header = new PickLocationPage().open()
                .selectClinicLocation()
                .confirmClinicLocation()
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
    void searchDropdownShowsCorrectResultsTest() {
        String generatedPartOfTheName = RandomDataGenerator.randomString(5);
        createdUuids.addAll(AdminSteps.createPatientsForSearch(4, true, generatedPartOfTheName));

        new PickLocationPage().open().selectClinicLocation().confirmClinicLocation();
        ServiceQueuesPage searchPatients = new ServiceQueuesPage();
        searchPatients.open().header.populateSearchPatientString(generatedPartOfTheName);

        int countFromUIDropDown = searchPatients.header.getSearchResultsCount();
        List<UiPatientMandatoryInfo> resultsFromUIDropDownList = searchPatients.header.getSearchDropdownResults();
        softly.assertThat(resultsFromUIDropDownList).hasSize(countFromUIDropDown);

        List<CreatePatientResponse> apiResults = AdminSteps.searchPatientsByString(generatedPartOfTheName);
        softly.assertThat(resultsFromUIDropDownList).hasSize(apiResults.size());

        apiResults.forEach(apiPatient -> softly.assertThat(resultsFromUIDropDownList)
                .anyMatch(ui -> (ui.getOpenMRSuuid() + " - " + ui.getNames()).equals(apiPatient.getDisplay())));
    }

    @Test
    @AdminSession
    public void searchPanelCanBeClosedTest() {
        new PickLocationPage().open().selectClinicLocation().confirmClinicLocation();
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
    public void searchPanelCanBeClosedAfterSearchTest() {
        String generatedPartOfTheName = RandomDataGenerator.randomString(5);
        createdUuids.addAll(AdminSteps.createPatientsForSearch(4, true, generatedPartOfTheName));

        new PickLocationPage().open().selectClinicLocation().confirmClinicLocation();
        ServiceQueuesPage page = new ServiceQueuesPage();
        page.header.populateSearchPatientString(generatedPartOfTheName);

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
    }

    @Skip(reason = "flaky test, that should be updated")
    @Test
    @AdminSession
    void searchInputCanBeClearedTest() {
        String generatedPartOfTheName = RandomDataGenerator.randomString(5);
        createdUuids.addAll(AdminSteps.createPatientsForSearch(4, true, generatedPartOfTheName));


        new PickLocationPage().open().selectClinicLocation().confirmClinicLocation();
        ServiceQueuesPage searchPatients = new ServiceQueuesPage();
        searchPatients.open().header.populateSearchPatientString(generatedPartOfTheName);

        int countFromUIDropDown = searchPatients.header.getSearchResultsCount();
        List<UiPatientMandatoryInfo> resultsFromUIDropDownList = searchPatients.header.getSearchDropdownResults();
        softly.assertThat(resultsFromUIDropDownList).hasSize(countFromUIDropDown);

        List<CreatePatientResponse> apiResults = AdminSteps.searchPatientsByString(generatedPartOfTheName);
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
    }

    @Test
    @AdminSession
    public void userCanNavigateToSearchResultsPageByClickingSearchButtonTest() {
        String generatedPartOfTheName = RandomDataGenerator.randomString(5);
        createdUuids.addAll(AdminSteps.createPatientsForSearch(4, true, generatedPartOfTheName));

        new PickLocationPage().open().selectClinicLocation().confirmClinicLocation();

        ServiceQueuesPage searchPatients = new ServiceQueuesPage();
        searchPatients.open().header.populateSearchPatientString(generatedPartOfTheName);

        int countFromUIDropDown = searchPatients.header.getSearchResultsCount();
        List<UiPatientMandatoryInfo> resultsFromUIDropDownList = searchPatients.header.getSearchDropdownResults();
        softly.assertThat(resultsFromUIDropDownList).hasSize(countFromUIDropDown);

        SearchResultsPage searchResultsPage = searchPatients.header.clickSearchButton();

        int countFromUIResultsPage = searchResultsPage.getSearchResultsCount();
        List<UiPatientMandatoryInfo> resultsFromUIResultsPage = searchResultsPage.getSearchResults();
        softly.assertThat(resultsFromUIResultsPage).hasSize(countFromUIResultsPage);

        List<CreatePatientResponse> apiResults = AdminSteps.searchPatientsByString(generatedPartOfTheName);
        softly.assertThat(resultsFromUIResultsPage).hasSize(apiResults.size());

        apiResults.forEach(apiPatient -> softly.assertThat(resultsFromUIResultsPage)
                .anyMatch(ui -> (ui.getOpenMRSuuid() + " - " + ui.getNames()).equals(apiPatient.getDisplay())));
    }

    @Test
    @AdminSession
    public void userCanNavigateToSearchResultsPageByPressingEnterTest() {
        String generatedPartOfTheName = RandomDataGenerator.randomString(5);
        createdUuids.addAll(AdminSteps.createPatientsForSearch(4, true, generatedPartOfTheName));

        new PickLocationPage().open().selectClinicLocation().confirmClinicLocation();

        ServiceQueuesPage searchPatients = new ServiceQueuesPage();
        searchPatients.header.populateSearchPatientString(generatedPartOfTheName);

        int countFromUIDropDown = searchPatients.header.getSearchResultsCount();
        List<UiPatientMandatoryInfo> resultsFromUIDropDownList = searchPatients.header.getSearchDropdownResults();
        softly.assertThat(resultsFromUIDropDownList).hasSize(countFromUIDropDown);

        SearchResultsPage searchResultsPage = searchPatients.header.pressEnterButton();

        int countFromUIResultsPage = searchResultsPage.getSearchResultsCount();
        List<UiPatientMandatoryInfo> resultsFromUIResultsPage = searchResultsPage.getSearchResults();

        softly.assertThat(resultsFromUIResultsPage).hasSize(countFromUIResultsPage);

        List<CreatePatientResponse> apiResults = AdminSteps.searchPatientsByString(generatedPartOfTheName);
        softly.assertThat(resultsFromUIResultsPage).hasSize(apiResults.size());

        apiResults.forEach(apiPatient -> softly.assertThat(resultsFromUIResultsPage)
                .anyMatch(ui -> (ui.getOpenMRSuuid() + " - " + ui.getNames()).equals(apiPatient.getDisplay())));
    }
}
