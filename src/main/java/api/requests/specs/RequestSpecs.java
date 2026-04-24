package api.requests.specs;

import api.configs.Config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.List;

public final class RequestSpecs {
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private RequestSpecs() {
    }

    private static RequestSpecBuilder defaultRequestSpecBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(
                        new RequestLoggingFilter(),
                        new ResponseLoggingFilter()
                ))
                .setBaseUri(Config.getProperty(Config.API_BASE_URL_CONST));
    }

    public static RequestSpecification unauthSpec() {
        return defaultRequestSpecBuilder().build();
    }

    public static RequestSpecification adminSpec() {
        return defaultRequestSpecBuilder()
                .addHeader(AUTHORIZATION_HEADER, "Basic YWRtaW46QWRtaW4xMjM=")
                .build();
    }
}
