package common.utils;

import io.qameta.allure.Allure;
import io.qameta.allure.model.Label;

public final class AllureCaseId {

    private AllureCaseId() {
    }

    public static void set(String caseId) {
        Allure.getLifecycle().updateTestCase(testResult -> {
            testResult.getLabels().add(new Label().setName("tag").setValue("case-id"));
            testResult.getLabels().add(new Label().setName("tag").setValue(caseId));
        });
    }
}
