package common.coverage;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class CoverageCli {

    public static void main(String[] args) {
        String registryPath = "/testcases-registry.yaml";
        String allureDir = "target/allure-results";

        for (String arg : args) {
            if (arg.startsWith("--registry=")) {
                registryPath = arg.substring("--registry=".length());
            } else if (arg.startsWith("--allure=")) {
                allureDir = arg.substring("--allure=".length());
            }
        }

        Map<String, TestCase> registry = CaseRegistryLoader.load(registryPath);
        Set<String> registryIds = registry.keySet();

        List<AllureResult> results = AllureResultsParser.parseDirectory(Path.of(allureDir));
        double automated = CoverageCalculator.automatedPercent(registryIds, results);

        System.out.printf("Registry size:  %d%n", registryIds.size());
        System.out.printf("Automated %%:    %.2f%%%n", automated);

        Set<String> unknown = CoverageCalculator.unknownCaseIds(registryIds, results);
        if (!unknown.isEmpty()) {
            System.out.println("Cases in Allure not in registry: " + unknown);
        }

        Set<String> notCovered = CoverageCalculator.notCoveredCaseIds(registryIds, results);
        if (!notCovered.isEmpty()) {
            System.out.println("Not covered cases:");
            notCovered.forEach(id -> System.out.printf("  - %s : %s%n",
                    id, registry.get(id).title()));
        }
    }
}
