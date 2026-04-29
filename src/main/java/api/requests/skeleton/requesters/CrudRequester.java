package api.requests.skeleton.requesters;

import api.configs.Config;
import api.models.BaseModel;
import api.requests.Endpoint;
import api.requests.HttpRequest;
import api.requests.skeleton.interfaces.CrudEndpointInterface;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequest implements CrudEndpointInterface {
    private static String UUID = "uuid";
    private static String PATH_PARAM_UUID = "uuid";

    public CrudRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification... responseSpecifications) {
        super(requestSpecification, endpoint, responseSpecifications);
    }

    @Override
    public ValidatableResponse get(String uuid, Class<?> clazz) {
        return given()
                .spec(requestSpecification)
                .pathParams(PATH_PARAM_UUID, uuid)
                .when()
                .get(Config.getProperty(Config.API_VERSION_CONST) + endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecifications);
    }

    @Override
    public ValidatableResponse getWithParams(Map<String, Object> queryParams, Class<?> clazz) {
        return given()
                .spec(requestSpecification)
                .when()
                .queryParams(queryParams)
                .get(Config.getProperty(Config.API_VERSION_CONST) + endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecifications);
    }

    @Override
    public ValidatableResponse post(BaseModel model) {
        var body = model == null ? "{}" : model;

        return given()
                .spec(requestSpecification)
                .when()
                .body(body)
                .post(Config.getProperty(Config.API_VERSION_CONST) + endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecifications);
    }

    @Override
    public ValidatableResponse post(BaseModel model, String uuid) {
        var body = model == null ? "{}" : model;

        return given()
                .spec(requestSpecification)
                .pathParam(PATH_PARAM_UUID, uuid)
                .when()
                .body(body)
                .post(Config.getProperty(Config.API_VERSION_CONST) + endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecifications);
    }

    @Override
    public void delete(String uuid) {
         given()
                .spec(requestSpecification)
                .pathParam(PATH_PARAM_UUID, uuid)
                .queryParam("purge", true)        // ← adds ?purge=true to the URL
                .when()
                .delete(Config.getProperty(Config.API_VERSION_CONST) + endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecifications);
    }

    public static class QueryBuilder {
        private final Map<String, Object> params = new HashMap<>();

        public QueryBuilder add(String key, Object value) {
            params.put(key, value);
            return this;
        }

        public Map<String, Object> build() {
            return params;
        }

        public QueryBuilder q(String query) {
            return add("q", query);
        }

        public QueryBuilder v(String view) {
            return add("v", view);
        }

        public QueryBuilder limit(int limit) {
            return add("limit", limit);
        }

        public QueryBuilder startIndex(int startIndex) {
            return add("startIndex", startIndex);
        }
    }
}
