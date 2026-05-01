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
        final String adminUsername = "admin";
        final String adminRole = "Super User";

        RetrieveSessionResponse session = new ValidatedAuthRequester(RequestSpecs.adminSpec(),
                Endpoint.SESSION,
                ResponseSpecs.requestReturnsOK(),
                ResponseSpecs.requestReturnsSetCookieHeader()).getSession();

        assertThat(session.isAuthenticated()).isTrue();
        assertThat(session.getUser().getDisplay()).isEqualTo(adminUsername);
        assertThat(session.getUser().getSystemId()).isEqualTo(adminUsername);
        assertThat(session.getUser().getPerson().getDisplay()).isEqualTo(adminRole);
    }
}
