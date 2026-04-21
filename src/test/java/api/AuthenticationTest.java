package api;

import api.models.RetrieveSessionResponse;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.ValidatedAuthRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthenticationTest extends BaseTest {

    @Test
    public void adminCanBeAuthenticatedTest() {
        RetrieveSessionResponse session = new ValidatedAuthRequester(RequestSpecs.adminSpec(),
                Endpoint.SESSION,
                ResponseSpecs.requestReturnsOK(),
                ResponseSpecs.requestReturnsSetCookieHeader()).getSession();

        assertThat(session.isAuthenticated()).isTrue();
        assertThat(session.getUser().getDisplay()).isEqualTo("admin");
        assertThat(session.getUser().getSystemId()).isEqualTo("admin");
        assertThat(session.getUser().getPerson().getDisplay()).isEqualTo("Super User");
    }

    @Test
    public void testRaw() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 80;

        System.out.println("Trying: " + RestAssured.baseURI + ":" + RestAssured.port);

        RestAssured.given()
                .auth().preemptive().basic("admin", "Admin123")
                .log().all()
                .when()
                .get("/openmrs/ws/rest/v1/session")
                .then()
                .log().all()
                .statusCode(200);
    }
}
