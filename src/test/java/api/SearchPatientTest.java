package api;

import api.models.CreatePatientResponse;
import api.requests.steps.AdminSteps;
import api.assertions.CommonAssertions;
import common.annotations.Skip;
import common.generators.RandomDataGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static api.constants.Constants.PATH_PARAM_PURGE;
import static org.assertj.core.api.Assertions.assertThat;

public class SearchPatientTest extends BaseTest{
    private static List<String> createdUuids = new ArrayList<>();
    private static final String generatedString = RandomDataGenerator.randomString(7);

    public static Stream<Arguments> positivePatientSearchDataGenerated() {
        createdUuids = AdminSteps.createPatientsForSearch(4, true, generatedString);
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
        List<CreatePatientResponse> results = AdminSteps.searchPatientsByString(searchText);
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
        List<CreatePatientResponse> results = AdminSteps.searchPatientsByString(searchText);
        assertThat(results).isEmpty();
    }

    public static Stream<Arguments> negativePatientSearchData() {
        createdUuids.addAll(AdminSteps.createPatientsForSearch(4, true, generatedString));
        return Stream.of(
            Arguments.of("m"),
            Arguments.of("abrakadabra"));
    }
    @Skip(reason = "flaky test, that should be updated")
    @MethodSource("negativePatientSearchData")
    @ParameterizedTest
    public void searchPatient_withoutMatchingTest(String searchText) {
        List<CreatePatientResponse> results = AdminSteps.searchPatientsByString(searchText);
        assertThat(results).isEmpty();
    }

    public static Stream<Arguments> unknownPatientSearchDataGenerated() {

        return Stream.of(
            Arguments.of("unknown", 20),
            Arguments.of("Unknown", 20),
            Arguments.of("UNKNOWN", 20));
    }
    @Skip(reason = "flaky test, that should be updated")
    @MethodSource("unknownPatientSearchDataGenerated")
    @ParameterizedTest
    public void searchUnknownPatientTest(String searchText, int resultCount) {
        List<CreatePatientResponse> results = AdminSteps.searchPatientsByString(searchText);
        assertThat(results).hasSize(resultCount);
        CommonAssertions.assertFieldContainText(results, searchText);
    }

    @AfterAll
    static void deleteTestPatients() {
        createdUuids.forEach(uuid -> AdminSteps.deletePatientByUuid(uuid, PATH_PARAM_PURGE));
    }

}
