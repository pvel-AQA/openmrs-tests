package common.extensions;

import common.helpers.CaseIdExtractor;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Устанавливает Allure label "caseId" перед каждым тестом.
 * <p>
 * Правила:
 * - для обычных тестов берётся @CaseId с метода;
 * - для параметризованных тестов приоритет у @CaseIdParam на параметре;
 * если такой параметр есть, @CaseId с метода игнорируется.
 */
public class CaseIdExtension implements BeforeEachCallback {

    private static final String CASE_ID_LABEL = "caseId";

    @Override
    public void beforeEach(ExtensionContext context) {
        String caseId = CaseIdExtractor.extract(context);

        if (caseId != null && !caseId.isBlank()) {
            Allure.label(CASE_ID_LABEL, caseId);
        }
    }
}
