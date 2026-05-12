package ui;

import api.models.CreatePatientResponse;
import api.models.PatientResponse;
import api.models.comparison.ModelAssertions;
import api.requests.steps.AdminSteps;
import com.codeborne.selenide.Selenide;
import common.annotations.AdminSession;
import org.junit.jupiter.api.Test;
import ui.pages.ServiceQueuesPage;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class SearchPatientTest extends BaseUiTest{

    @Test
    @AdminSession
    public void emptySearchPatientModalTest(){
        //check default text and Error message
        Selenide.sleep(5000);

    }

    // Test: start search "barb" close search frame and nothing is changed on the main screen. Just search frame is closed.

    // Test: start search "barb" click x-btn and search field is empty. Search results drop-down shows ""

    @Test
    @AdminSession
    public void searchPatientDropDownResultsTest(){
        //create test data
        String searchText = "barb";
        ServiceQueuesPage searchBarb = new ServiceQueuesPage();
        searchBarb.open()
                .header.enterSearchPatientString(searchText);
        int countInDropDown = searchBarb.header.searchResultsCount(searchText);

        //API results
        List<CreatePatientResponse> foundPatientsViaAPI = AdminSteps.searchPatientsByString(searchText);
        //Assert
        assertEquals(foundPatientsViaAPI.size(),countInDropDown);
        List<CreatePatientResponse> patientsInDropDown = searchBarb.header.foundPatientsDisplayedInDropDown(searchText);
        //ModelAssertions.assertThatModels(patientsInDropDown, foundPatientsViaAPI).match();
        //assertThat(foundPatientsViaAPI).containsExactlyInAnyOrderElementsOf(patientsInDropDown);
        System.out.println(patientsInDropDown);

        Selenide.sleep(5000);


        // check number results in drop down
        // check names of the results

        //clickSearchBtn -> test
        //clickEnter -> test

        // check number results on the results page
        // check names of the results


    }
//ByFirstNameFrameTest
    public void searchPatientByMiddleNameTest(){}

    public void searchPatientByLastNameTest(){}

    public void searchPatientByIDTest(){}

    public void searchPatientByNamesAndIDTest(){}
}
