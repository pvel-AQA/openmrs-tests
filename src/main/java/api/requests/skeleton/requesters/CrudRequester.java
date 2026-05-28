package api.requests.skeleton.requesters;

import api.configs.Config;
import api.models.BaseModel;
import api.requests.Endpoint;
import api.requests.HttpRequest;
import api.coverage.CoverageHelper;
import api.requests.skeleton.interfaces.CrudEndpointInterface;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequest implements CrudEndpointInterface {
    private static final String PATH_PARAM_UUID = "uuid";
    private static final String PATH_PARAM_PURGE = "purge";

    public CrudRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification... responseSpecifications) {
        super(requestSpecification, endpoint, responseSpecifications);
    }

    @Override
    public ValidatableResponse get(String uuid, Class<?> clazz) {
     // return given()
     //         .spec(requestSpecification)
     //         .pathParams(PATH_PARAM_UUID, uuid)
     //         .when()
     //         .get(Config.getProperty(Config.API_VERSION_CONST) + endpoint.getUrl())
     //         .then()
     //         .assertThat()
     //         .spec(responseSpecifications);

       //Трекинг вызывается всегда, даже если запрос упал с ошибкой (например, 404, 500, таймаут и т.д.).
        //Если в .then().assertThat() тест упадёт — CoverageHelper.track()
        // всё равно выполнится, хотя запрос "не прошёл".

        //@Override
        //    public ValidatableResponse get(String uuid, Class<?> clazz) {
        //        CoverageHelper.track("GET", endpoint.getUrl());
        //        return given()
        //                .spec(requestSpecification)
        //                .pathParams(PATH_PARAM_UUID, uuid)
        //                .when()
        //                .get(Config.getProperty(Config.API_VERSION_CONST) + endpoint.getUrl())
        //                .then()
        //                .assertThat()
        //                .spec(responseSpecifications);
        //    }

        ValidatableResponse response = given()
                .spec(requestSpecification)
                .pathParams(PATH_PARAM_UUID, uuid)
                .when()
                .get(Config.getProperty(Config.API_VERSION_CONST) + endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecifications);

        // Трекинг покрытия
        CoverageHelper.track("GET", endpoint.getUrl());

        return response;
    }

    @Override
    public ValidatableResponse getAll(String uuid, Class<?> clazz) {
      //  return given()
      //          .spec(requestSpecification)
      //          .pathParams(PATH_PARAM_UUID, uuid)
      //          .when()
      //          .get(Config.getProperty(Config.API_VERSION_CONST) + endpoint.getUrl())
      //          .then()
      //          .assertThat()
      //          .spec(responseSpecifications);

        ValidatableResponse response = given()
                .spec(requestSpecification)
                .pathParams(PATH_PARAM_UUID, uuid)
                .when()
                .get(Config.getProperty(Config.API_VERSION_CONST) + endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecifications);

        CoverageHelper.track("GET", endpoint.getUrl());
        return response;
    }

    @Override
    public ValidatableResponse getAll(Map<String, Object> queryParams, Class<?> clazz) {
     //  return given()
     //          .spec(requestSpecification)
     //          .when()
     //          .queryParams(queryParams)
     //          .get(Config.getProperty(Config.API_VERSION_CONST) + endpoint.getUrl())
     //          .then()
     //          .assertThat()
     //          .spec(responseSpecifications);

        ValidatableResponse response = given()
                .spec(requestSpecification)
                .when()
                .queryParams(queryParams)
                .get(Config.getProperty(Config.API_VERSION_CONST) + endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecifications);

        CoverageHelper.track("GET", endpoint.getUrl());
        return response;
    }

    @Override
    public ValidatableResponse post(BaseModel model) {
     //   var body = model == null ? "{}" : model;
//
     //   return given()
     //           .spec(requestSpecification)
     //           .when()
     //           .body(body)
     //           .post(Config.getProperty(Config.API_VERSION_CONST) + endpoint.getUrl())
     //           .then()
     //           .assertThat()
     //           .spec(responseSpecifications);

        ValidatableResponse response = given()
                .spec(requestSpecification)
                .when()
                .body(model == null ? "{}" : model)
                .post(Config.getProperty(Config.API_VERSION_CONST) + endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecifications);

        CoverageHelper.track("POST", endpoint.getUrl());
        return response;
    }

    @Override
    public ValidatableResponse post(BaseModel model, String uuid) {
     //   var body = model == null ? "{}" : model;
//
     //   return given()
     //           .spec(requestSpecification)
     //           .pathParam(PATH_PARAM_UUID, uuid)
     //           .when()
     //           .body(body)
     //           .post(Config.getProperty(Config.API_VERSION_CONST) + endpoint.getUrl())
     //           .then()
     //           .assertThat()
     //           .spec(responseSpecifications);

        ValidatableResponse response = given()
                .spec(requestSpecification)
                .pathParam(PATH_PARAM_UUID, uuid)
                .when()
                .body(model == null ? "{}" : model)
                .post(Config.getProperty(Config.API_VERSION_CONST) + endpoint.getUrl())
                .then()
                .assertThat()
                .spec(responseSpecifications);

        CoverageHelper.track("POST", endpoint.getUrl());
        return response;
    }

    @Override
    public void delete(String uuid) {
        given()
                .spec(requestSpecification)
                .pathParam(PATH_PARAM_UUID, uuid)
                .when()
                .delete(Config.getProperty(Config.API_VERSION_CONST) + endpoint.getUrl());

        CoverageHelper.track("DELETE", endpoint.getUrl());
    }

    @Override
    public void delete(String uuid, Boolean purge) {
        given()
                .spec(requestSpecification)
                .pathParam(PATH_PARAM_UUID, uuid)
                .queryParam(PATH_PARAM_PURGE, purge)
                .when()
                .delete(Config.getProperty(Config.API_VERSION_CONST) + endpoint.getUrl());

        CoverageHelper.track("DELETE", endpoint.getUrl());
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

        public QueryBuilder vEqualsFull() {
            return v("full");
        }
    }
}
