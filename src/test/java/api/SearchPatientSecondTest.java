package api;

import api.models.CreatePatientResponse;
import api.requests.steps.AdminSteps;
import api.requests.steps.PatientSteps;
import common.assertions.CommonAssertions;
import common.testData.PatientsDataForSearch;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchPatientSecondTest extends BaseTest{

    private static final List<String> createdUuids = new ArrayList<>();
    @BeforeEach
    void createTestPatients(TestInfo testInfo) {
        if (testInfo.getTags().contains("skip-setup")) {
            return;
        }
        createdUuids.add(PatientsDataForSearch.createPatient("zz", "YYYYYY", "qwerty", "M", 16, "2009-09-09"));
        createdUuids.add(PatientsDataForSearch.createPatient("Givenname", "ZZZ", "YYYYY", "F", 15, "2010-09-09"));
        createdUuids.add(PatientsDataForSearch.createPatient("yyy", "Qwerty", "zzebra", "O", 16, "2009-09-19"));
        createdUuids.add(PatientsDataForSearch.createPatient("QwertyName", "", "", "U", 15, "2010-09-19"));
    }
//-----------------------------------------------------------------------------------------
// Test #1: checking that users that exist in DB are actually found with entered search text
//-----------------------------------------------------------------------------------------
    public static Stream<Arguments> positivePatientSearchData() {
        return Stream.of(
                Arguments.of("qwerty", 3),
                Arguments.of("QWERTY", 3),
                Arguments.of("yyy", 9),
                Arguments.of("zz", 9),
                Arguments.of("Zze", 2));
    }
    @MethodSource("positivePatientSearchData")
    @ParameterizedTest
    @Tag("skip-setup")
    public void searchPatient_withMatchingTest(String searchText, int resultCount) {
        List<CreatePatientResponse> results = PatientSteps.searchPatients(searchText);
        assertThat(results).hasSize(resultCount);
        CommonAssertions.assertFieldContainText(results, searchText);
    }
//-----------------------------------------------------------------------------------------
// Test #2: checking that 0 results are found when DB is empty
//-----------------------------------------------------------------------------------------
    public static Stream<Arguments> negativePatientSearchData() {
        return Stream.of(
                Arguments.of("m"),
                Arguments.of("abrakadabra"));
    }
    @MethodSource("negativePatientSearchData")
    @ParameterizedTest
    @Tag("skip-setup")
    public void searchPatient_withoutMatching_WhenDB_isEmptyTest(String searchText) {
        List<CreatePatientResponse> results = PatientSteps.searchPatients(searchText);
        assertThat(results).isEmpty();
    }
//-----------------------------------------------------------------------------------------
// Test #3: checking that 0 results are found when DB is NOT empty
//-----------------------------------------------------------------------------------------
    @MethodSource("negativePatientSearchData")
    @ParameterizedTest
    public void searchPatient_withoutMatchingTest(String searchText) {
        List<CreatePatientResponse> results = PatientSteps.searchPatients(searchText);
        assertThat(results).isEmpty();
    }
//-----------------------------------------------------------------------------------------
// Test #: checking that user without name
//-----------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------
// Test #: checking that UNKNOWN users
//-----------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------
// Test #: checking that user with extra "   " in the names
//-----------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------
// Test #: checking that
// refine search sex: any/M, F, O, U + Reset fields
//-----------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------
// Test #: checking that
// refine search DOB: day+month+year/year/year+month + Reset fields
//-----------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------
// Test #: checking that
//refine search Age: + Reset fields
//-----------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------
// Test #: checking that
//refine search Postcode + Reset fields
//-----------------------------------------------------------------------------------------
    @AfterEach
   void deleteTestPatients() {
        createdUuids.forEach(uuid -> {
            try {
                AdminSteps.deletePatientByUuid(uuid);
            } catch (Exception e) {
                System.err.println("Failed to delete patient: " + uuid + " — " + e.getMessage());
            }
        });
    }

}
