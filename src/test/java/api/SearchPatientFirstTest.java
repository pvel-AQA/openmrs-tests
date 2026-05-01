package api;

import api.models.CreatePatientResponse;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import api.requests.steps.AdminSteps;
import common.testData.PatientsDataForSearch;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchPatientFirstTest extends BaseTest{
    private List<CreatePatientResponse> searchPatients(String searchText) {
        Map<String, Object> params = Map.of("q", searchText);
        return new ValidatedCrudRequester<CreatePatientResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.PATIENT,
                ResponseSpecs.requestReturnsOK())
                .getAll(params, CreatePatientResponse.class);
    }

    private void assertFieldContainText(List<CreatePatientResponse> results, String searchText) {
        String[] searchWords = searchText.toLowerCase().replace("+", " ").trim().split("\\s+");
        for (CreatePatientResponse person : results) {
            String display = person.getDisplay().toLowerCase();
            for (String word : searchWords) {
                assertThat(display).contains(word);
            }
        }
    }

    private static final List<String> createdUuids = new ArrayList<>();
    @BeforeAll
    static void createTestPatients() {
        createdUuids.add(PatientsDataForSearch.createPatient("zz", "YYYYYY", "qwerty", "M", 16, "2009-09-09"));
        createdUuids.add(PatientsDataForSearch.createPatient("Givenname", "ZZZ", "YYYYY", "F", 15, "2010-09-09"));
        createdUuids.add(PatientsDataForSearch.createPatient("yyy", "Qwerty", "zzebra", "O", 16, "2009-09-19"));
        createdUuids.add(PatientsDataForSearch.createPatient("QwertyName", "", "", "U", 15, "2010-09-19"));
    }

    public static Stream<Arguments> positivePatientSearchData() {
        return Stream.of(
                Arguments.of("qwerty", 7),
                Arguments.of("QWERTY", 7),
                Arguments.of("yyy", 10),
                Arguments.of("zz", 10),
                Arguments.of("Zze", 2));
    }

    @MethodSource("positivePatientSearchData")
    @ParameterizedTest
    public void searchPatient_withMatchingTest(String searchText, int resultCount) {
        List<CreatePatientResponse> results = searchPatients(searchText);
        assertThat(results).hasSize(resultCount);
        assertFieldContainText(results, searchText);
    }

    public static Stream<Arguments> negativePatientSearchData() {
        return Stream.of(
                Arguments.of("m"),
                Arguments.of("abrakadabra"));
    }

    @MethodSource("negativePatientSearchData")
    @ParameterizedTest
    public void searchPatient_withoutMatchingTest(String searchText) {
        List<CreatePatientResponse> results = searchPatients(searchText);
        assertThat(results).isEmpty();
    }

    @AfterAll
    static void deleteTestPatients() {
        createdUuids.forEach(uuid -> {
            try {
                AdminSteps.deletePatientByUuid(uuid);
            } catch (Exception e) {
                System.err.println("Failed to delete patient: " + uuid + " — " + e.getMessage());
            }
        });
    }

}
