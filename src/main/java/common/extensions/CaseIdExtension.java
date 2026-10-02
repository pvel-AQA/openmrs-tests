package common.extensions;

import common.annotations.CaseId;
import common.annotations.CaseIdParam;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterInfo;

import java.lang.reflect.Parameter;

/**
 * Устанавливает Allure label "caseId" перед каждым тестом.
 * <p>
 * Правила:
 * - для обычных тестов берётся @CaseId с метода;
 * - для параметризованных тестов приоритет у @CaseIdParam на параметре;
 *   если такой параметр есть, @CaseId с метода игнорируется.
 */
public class CaseIdExtension implements BeforeEachCallback {

    private static final String CASE_ID_LABEL = "caseId";

    @Override
    public void beforeEach(ExtensionContext context) {
        ParameterInfo paramInfo = ParameterInfo.get(context);
        if (paramInfo == null) {
            context.getTestMethod()
                    .map(m -> m.getAnnotation(CaseId.class))
                    .ifPresent(c ->
                            Allure.label(CASE_ID_LABEL, c.value())
                    );
            return;
        }

        Parameter[] parameters = context.getRequiredTestMethod().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(CaseIdParam.class)) {
                String caseId = paramInfo.getArguments().getString(i);
                if (caseId != null && !caseId.isBlank()) {
                    Allure.label(CASE_ID_LABEL, caseId);
                }
                return;
            }
        }
    }
}
