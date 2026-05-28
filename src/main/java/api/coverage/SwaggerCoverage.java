package api.coverage;

public class SwaggerCoverage {

    public static void mark(String method, String path) {
        CoverageExtension.mark(method, path);
    }

    public static void printReport() {
        CoverageExtension.printReport();
    }
}
