package common.coverage;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class CoverageCalculator {

    private CoverageCalculator() {
    }

    /** Automated % = доля кейсов из реестра, встретившихся в Allure-результатах. */
    public static double automatedPercent(Set<String> registryIds, List<AllureResult> results) {
        long covered = results.stream()
                .map(AllureResultsParser::extractCaseId)
                .filter(id -> id != null && registryIds.contains(id))
                .distinct()
                .count();
        return registryIds.isEmpty()
                ? 0.0
                : Math.round((covered * 10000.0) / registryIds.size()) / 100.0;
    }

    /** Case-id, которые есть в Allure, но отсутствуют в реестре. */
    public static Set<String> unknownCaseIds(Set<String> registryIds, List<AllureResult> results) {
        return results.stream()
                .map(AllureResultsParser::extractCaseId)
                .filter(id -> id != null && !registryIds.contains(id))
                .collect(Collectors.toSet());
    }

    /** Case-id, которые есть в реестре, но не встретились ни в одном Allure-результате. */
    public static Set<String> notCoveredCaseIds(Set<String> registryIds, List<AllureResult> results) {
        Set<String> covered = results.stream()
                .map(AllureResultsParser::extractCaseId)
                .filter(id -> id != null && registryIds.contains(id))
                .collect(Collectors.toSet());

        return registryIds.stream()
                .filter(id -> !covered.contains(id))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
