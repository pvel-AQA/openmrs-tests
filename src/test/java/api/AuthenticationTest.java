package api;

import api.configs.Config;
import api.models.RetrieveSessionResponse;
import api.requests.Endpoint;
import api.requests.skeleton.requesters.ValidatedAuthRequester;
import api.requests.specs.RequestSpecs;
import api.requests.specs.ResponseSpecs;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthenticationTest extends BaseTest {

    @Test
    public void adminCanBeAuthenticatedTest() {
        final String adminRole = "Super User";

        RetrieveSessionResponse session = new ValidatedAuthRequester(RequestSpecs.adminSpec(),
                Endpoint.SESSION,
                ResponseSpecs.requestReturnsOK(),
                ResponseSpecs.requestReturnsSetCookieHeader()).getSession();

        assertThat(session.isAuthenticated()).isTrue();
        assertThat(session.getUser().getDisplay()).isEqualTo(Config.ADMIN_USERNAME_CONST);
        assertThat(session.getUser().getSystemId()).isEqualTo(Config.ADMIN_USERNAME_CONST);
        assertThat(session.getUser().getPerson().getDisplay()).isEqualTo(adminRole);
    }
}
