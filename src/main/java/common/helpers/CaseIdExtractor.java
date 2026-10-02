package common.helpers;

import common.annotations.CaseId;
import common.annotations.CaseIdParam;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterInfo;

import java.lang.reflect.Parameter;

public final class CaseIdExtractor {

    private CaseIdExtractor() {
    }

    public static String extract(ExtensionContext context) {
        ParameterInfo paramInfo = ParameterInfo.get(context);

        if (paramInfo == null) {
            return context.getTestMethod()
                    .map(m -> m.getAnnotation(CaseId.class))
                    .map(CaseId::value)
                    .orElse(null);
        }

        Parameter[] parameters = context.getRequiredTestMethod().getParameters();

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(CaseIdParam.class)) {
                return paramInfo.getArguments().getString(i);
            }
        }

        return null;
    }
}
