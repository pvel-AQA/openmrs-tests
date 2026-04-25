package api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SearchPatientTest {
    //search by name, middle name,lastname, id
    //results start appear after 2 symbols are entered,
    // with 1 symbol 0 search results and message "Sorry, no patient charts were found"
    //Result display:
    //XX search results
    //sorted by openMRS_ID

    //Actions -> Edit patient details
    //Start visit

    public static Stream<Arguments> patientSearchData(){
        return Stream.of(
                //search string positive combinations
                Arguments.of("mi+6j", 200, 1, ""),
                Arguments.of("MI+6J", 200, 1, ""),
                Arguments.of("mi", 200, 6, ""),
                Arguments.of("middle", 200, 3, ""),
                Arguments.of("m", 200, 0, ""),
                Arguments.of("abrakadabra", 200, 0, ""),
                Arguments.of("le", 200, 2, ""));
    }
    @MethodSource("patientSearchData")
    @ParameterizedTest
    //public void searchPatient(String firstName, String middleName, String lastName, String id, String expectedMessage){
    public void searchPatient(String searchText, int expectedStatusCode, int resultCount, String expectedMessage){
        //String searchText = firstName + "+" + middleName + "+" + lastName+ "+" + id;
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 80;
        ValidatableResponse response = given()
                .auth().preemptive().basic("admin", "Admin123")
                .log().all()
                .urlEncodingEnabled(false)
                .queryParam("q", searchText)/*, middleName, lastName, id)*/
                .queryParam("totalCount", true)
                .get("/openmrs/ws/rest/v1/patient")
                .then()
                .log().all()
                .statusCode(expectedStatusCode);
        // 1. Check totalCount matches expected
        Integer actualTotalCount = response.extract().path("totalCount");
        int count = actualTotalCount != null ? actualTotalCount : response.extract().path("results.size()");
        assertThat("totalCount mismatch for query: " + searchText,
                count, equalTo(resultCount));

        if (resultCount == 0) {
            List<?> results = response.extract().path("results");
            assertThat("Expected empty results for query: " + searchText, results, hasSize(0));
        }

        // 2. If results are expected, check each "display" contains the searchText (case-insensitive)
        if (resultCount > 0) {
            List<String> displayValues = response.extract().path("results.display");

            // Normalize the searchText: replace "+" with " " so "mi+6j" becomes "mi 6j",
            // then split into tokens and check each token appears in at least one display value
            String[] tokens = searchText.toLowerCase().replace("+", " ").trim().split("\\s+");

            for (String display : displayValues) {
                String displayLower = display.toLowerCase();
                for (String token : tokens) {
                    assertThat(
                            "Display value '" + display + "' does not contain token '" + token + "'",
                            displayLower, containsString(token)
                    );
                }
            }
        }
    }
}
