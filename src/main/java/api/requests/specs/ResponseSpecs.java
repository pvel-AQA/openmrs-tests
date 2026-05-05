package api.requests.specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;

import static org.hamcrest.Matchers.*;

public final class ResponseSpecs {

    private ResponseSpecs() {
    }

    private static ResponseSpecBuilder defaultResponseBuilder() {
        return new ResponseSpecBuilder();
    }

    public static ResponseSpecification requestReturnsOK() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    public static ResponseSpecification requestReturnsCreated() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_CREATED)
                .build();
    }

    public static ResponseSpecification requestReturnsNoContent() {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_NO_CONTENT)
                .build();
    }

    public static ResponseSpecification requestReturnsSetCookieHeader() {
        return defaultResponseBuilder()
                .expectHeader("Set-Cookie", containsString("JSESSIONID"))
                .build();
    }

    public static ResponseSpecification requestReturnsAnyStatus() {
        return new ResponseSpecBuilder()
                .build();  // no status code expectation
    }

    public static ResponseSpecification requestReturnsBadRequest() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .build();
    }

    public static ResponseSpecification requestReturnBadRequestForIncorrectName(String fieldName, String errorValue) {
        return defaultResponseBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody("error.fieldErrors",
                        hasEntry(
                                equalTo("names[0]." + fieldName),
                                hasItem(hasEntry("message", errorValue))
                        ))
                .build();
    }

    public static ResponseSpecification requestReturnsNotFound() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_NOT_FOUND)
                .build();
    }

    public static ResponseSpecification requestReturnsMovedTemporarily() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_MOVED_TEMPORARILY)
                .build();
    }
}
