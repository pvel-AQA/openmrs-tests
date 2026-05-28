package coverage;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * REST Assured filter that records every outgoing request for coverage tracking.
 *
 * It is passive: it observes method + path, hands the data to SwaggerCoverage,
 * and lets the request proceed untouched. Register it ONCE in your shared client.
 */
public class CoverageFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification req,
                           FilterableResponseSpecification res,
                           FilterContext ctx) {
        // getDerivedPath() yields the concrete path with path-params substituted,
        // e.g. /openmrs/ws/rest/v1/patient/abc-uuid. SwaggerCoverage.normalize()
        // strips it down to /patient/abc-uuid and matches it to /patient/{uuid}.
        String path = req.getDerivedPath();
        if (path == null || path.isEmpty()) {
            path = req.getURI();   // fallback: full URI, normalize() handles it
        }
        SwaggerCoverage.record(req.getMethod(), path);
        return ctx.next(req, res);
    }
}
