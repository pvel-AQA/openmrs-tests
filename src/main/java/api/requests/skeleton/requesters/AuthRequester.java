package api.requests.skeleton.requesters;

import api.requests.Endpoint;
import api.requests.HttpRequest;
import api.requests.skeleton.interfaces.SessionEndpointInterface;
import common.helpers.StepLogger;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.given;

public class AuthRequester extends HttpRequest implements SessionEndpointInterface {

    public AuthRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification... responseSpecifications) {
        super(requestSpecification, endpoint, responseSpecifications);
    }

    @Override
    public ValidatableResponse get(String username, String password) {
        return StepLogger.log("Post request to " + Endpoint.SESSION.getUrl(), () -> {
            return given()
                    .auth().preemptive().basic(username, password)
                    .spec(requestSpecification)
                    .when()
                    .get(Endpoint.SESSION.getUrl())
                    .then()
                    .assertThat()
                    .spec(responseSpecifications);
        });
    }
}
