package common.extensions;

import common.annotations.CaseId;
import common.annotations.CaseIdParam;
import common.utils.AllureCaseId;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterInfo;

import java.lang.reflect.Parameter;

public class CaseIdExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        ParameterInfo paramInfo = ParameterInfo.get(context);
        if (paramInfo == null) {
            context.getTestMethod()
                    .map(m -> m.getAnnotation(CaseId.class))
                    .ifPresent(c -> AllureCaseId.set(c.value()));
            return;
        }

        Parameter[] parameters = context.getRequiredTestMethod().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(CaseIdParam.class)) {
                String caseId = paramInfo.getArguments().getString(i);
                if (caseId != null && !caseId.isBlank()) {
                    AllureCaseId.set(caseId);
                }
                return;
            }
        }
    }
}
