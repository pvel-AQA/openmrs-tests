package api.coverage;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class CoverageListener implements AfterAllCallback {

    @Override
    public void afterAll(ExtensionContext context) {
        System.out.println("\n[FINAL COVERAGE REPORT - AFTER TESTS]");
        CoverageHelper.showReport();
    }
}
