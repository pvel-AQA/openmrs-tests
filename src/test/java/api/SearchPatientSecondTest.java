package api;

import api.models.CreatePatientResponse;
import api.requests.steps.AdminSteps;
import api.requests.steps.PatientSteps;
import common.assertions.CommonAssertions;
import common.generators.RandomGenerators;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchPatientSecondTest extends BaseTest{
    private static List<String> createdUuids = new ArrayList<>();
    private static String generatedString = RandomGenerators.randomString(7);

    public static Stream<Arguments> positivePatientSearchDataGenerated() {
        createdUuids = PatientSteps.createPatientsForSearch(4, true, generatedString);
        return Stream.of(
                Arguments.of(AdminSteps.findPatientByUuid(createdUuids.get(0)).getDisplay().substring(0,7), 1),
                Arguments.of(AdminSteps.findPatientByUuid(createdUuids.get(1)).getDisplay().substring(4,7), 1),
                Arguments.of(generatedString.substring(0,4).toLowerCase() + " " + AdminSteps.findPatientByUuid(createdUuids.get(2)).getDisplay().substring(4,7), 1),
                Arguments.of(generatedString.substring(0,4).toLowerCase() + "FN", 4),
                Arguments.of(generatedString.substring(0,5).toLowerCase() + "LN", 4),
                Arguments.of(generatedString.substring(0,4).toUpperCase() + "MN", 4),
                Arguments.of(generatedString.substring(0,3).toLowerCase(), 4));
    }
    @MethodSource("positivePatientSearchDataGenerated")
    @ParameterizedTest
    public void searchPatient_withMatchingTest(String searchText, int resultCount) {
        List<CreatePatientResponse> results = PatientSteps.searchPatientsByString(searchText);
        assertThat(results).hasSize(resultCount);
        CommonAssertions.assertFieldContainText(results, searchText);
    }

    public static Stream<Arguments> negativePatientSearchDataWhenDBisEmpty() {
        return Stream.of(
                Arguments.of("m"),
                Arguments.of("abrakadabra"));
    }
    @MethodSource("negativePatientSearchDataWhenDBisEmpty")
    @ParameterizedTest
    public void searchPatient_withoutMatching_WhenDB_isEmptyTest(String searchText) {
        List<CreatePatientResponse> results = PatientSteps.searchPatientsByString(searchText);
        assertThat(results).isEmpty();
    }

    public static Stream<Arguments> negativePatientSearchData() {
        createdUuids.addAll(PatientSteps.createPatientsForSearch(4, true, generatedString));
        return Stream.of(
            Arguments.of("m"),
            Arguments.of("abrakadabra"));
    }
    @MethodSource("negativePatientSearchData")
    @ParameterizedTest
    public void searchPatient_withoutMatchingTest(String searchText) {
        List<CreatePatientResponse> results = PatientSteps.searchPatientsByString(searchText);
        assertThat(results).isEmpty();
    }

    public static Stream<Arguments> unknownPatientSearchDataGenerated() {
        // I will need to uncomment this once my DB is empty before each test
        //createdUuids.add(PatientSteps.createUnknownPatient());
            //createdUuids.add(PatientSteps.createUnknownPatient());
        return Stream.of(
            //Arguments.of("Unknown" + " " + AdminSteps.findPatientByUuid(createdUuids.getLast()).getDisplay().substring(4,7), 1),
            Arguments.of("unknown", 16),
            Arguments.of("Unknown", 16),
            Arguments.of("UNKNOWN", 16));
    }
    @MethodSource("unknownPatientSearchDataGenerated")
    @ParameterizedTest
    public void searchUnknownPatientTest(String searchText, int resultCount) {
        List<CreatePatientResponse> results = PatientSteps.searchPatientsByString(searchText);
        assertThat(results).hasSize(resultCount);
        CommonAssertions.assertFieldContainText(results, searchText);
    }

  /*  public static Stream<Arguments> refineByGenderData() {
        return Stream.of(
                Arguments.of("M", "mi", 4),
                Arguments.of("F", "mi", 4),
                Arguments.of("O", "1000", 5),
                Arguments.of("U", "1000", 6));
    }
    @MethodSource("refineByGenderData")
    @ParameterizedTest
    public void searchPatientRefineBySexTest(String gender, String searchText, int resultCount) {
        List<CreatePatientResponse> results = PatientSteps.searchPatients(searchText);
        System.out.println("\u001B[35m" + searchText + "\u001B[0m");
        System.out.println("\u001B[35m" + results + "\u001B[0m");
        assertThat(results).hasSize(resultCount);
        //CommonAssertions.assertFieldContainText(results, searchText);
    }*/

    @AfterAll
    static void deleteTestPatients() {
        createdUuids.forEach(uuid -> {
            try {
                AdminSteps.deletePatientByUuid(uuid, true);
            } catch (Exception e) {
                System.err.println("Failed to delete patient: " + uuid + " — " + e.getMessage());
            }
        });
    }

}
