package api.coverage;

public class CoverageHelper {

    public static void track(String method, String url) {
        SwaggerCoverage.mark(method, url);
    }

    public static void showReport() {
        SwaggerCoverage.printReport();
    }
}
