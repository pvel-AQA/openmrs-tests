package common.coverage;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CoverageCli {

    public static void main(String[] args) {
        String registryPath = "/testcases-registry.yaml";
        String allureDir = "target/allure-results";
        String htmlOutput = "target/coverage-report.html";

        for (String arg : args) {
            if (arg.startsWith("--registry=")) {
                registryPath = arg.substring("--registry=".length());
            } else if (arg.startsWith("--allure=")) {
                allureDir = arg.substring("--allure=".length());
            } else if (arg.startsWith("--html=")) {
                htmlOutput = arg.substring("--html=".length());
            }
        }

        Map<String, TestCase> registry = CaseRegistryLoader.load(registryPath);
        Set<String> registryIds = registry.keySet();

        List<AllureResult> results = AllureResultsParser.parseDirectory(Path.of(allureDir));
        double automated = CoverageCalculator.automatedPercent(registryIds, results);

        Set<String> notCovered = CoverageCalculator.notCoveredCaseIds(registryIds, results);
        Set<String> unknown = CoverageCalculator.unknownCaseIds(registryIds, results);
        int coveredCount = registryIds.size() - notCovered.size();

        System.out.printf("Registry size:  %d%n", registryIds.size());
        System.out.printf("Automated %%:    %.2f%%%n", automated);

        if (!unknown.isEmpty()) {
            System.out.printf("Cases in Allure not in registry: %s%n", unknown);
        }

        if (!notCovered.isEmpty()) {
            System.out.println("Not covered cases:");
            notCovered.forEach(id -> System.out.printf("  - %s : %s%n",
                    id, registry.get(id).title()));
        }

        CoverageReportWriter.writeHtml(
                Path.of(htmlOutput),
                registryIds.size(),
                coveredCount,
                notCovered.size(),
                automated,
                registry,
                notCovered.stream().toList(),
                unknown.stream().toList()
        );

        System.out.printf("HTML report:    %s%n", htmlOutput);
    }
}
