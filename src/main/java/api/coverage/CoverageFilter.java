package api.coverage;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class CoverageFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {

        String method = requestSpec.getMethod();
        String path = requestSpec.getDerivedPath();

        if (path == null || path.isEmpty()) {
            path = requestSpec.getURI();
        }

        CoverageHelper.track(method, path);

        return ctx.next(requestSpec, responseSpec);
    }
}
