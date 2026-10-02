package common.extensions;

import common.annotations.TestWithRetry;
import common.helpers.CaseIdExtractor;
import io.qameta.allure.Allure;
import io.qameta.allure.model.Label;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.opentest4j.TestAbortedException;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RetryExtension implements TestTemplateInvocationContextProvider {

    private static final String RETRY_LABEL = "retryAttempt";

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return AnnotationSupport.isAnnotated(context.getTestMethod(), TestWithRetry.class)
                || AnnotationSupport.isAnnotated(context.getTestClass(), TestWithRetry.class);
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        TestWithRetry retry = AnnotationSupport.findAnnotation(context.getTestMethod(), TestWithRetry.class)
                .or(() -> AnnotationSupport.findAnnotation(context.getTestClass(), TestWithRetry.class))
                .orElseThrow(() -> new IllegalStateException("@TestWithRetry not found"));

        int maxAttempts = retry.maxAttempts();
        long delayMs = retry.delayMs();

        return IntStream.range(0, maxAttempts)
                .mapToObj(attempt -> new RetryInvocationContext(attempt, maxAttempts, delayMs));
    }

    private record RetryInvocationContext(int attempt, int maxAttempts, long delayMs)
            implements TestTemplateInvocationContext {

        @Override
        public String getDisplayName(int invocationIndex) {
            return "attempt " + (attempt + 1) + "/" + maxAttempts;
        }

        @Override
        public List<Extension> getAdditionalExtensions() {
            return List.of(
                    new RetryExecutionHandler(attempt, maxAttempts, delayMs)
            );
        }
    }

    private record RetryExecutionHandler(int attempt, int maxAttempts, long delayMs)
            implements TestExecutionExceptionHandler, AfterTestExecutionCallback {

        @Override
        public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
            boolean isLastAttempt = (attempt + 1) >= maxAttempts;

            if (isLastAttempt) {
                throw throwable;
            }

            if (delayMs > 0) {
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            throw new TestAbortedException("Retry attempt " + (attempt + 1), throwable);
        }

        @Override
        public void afterTestExecution(ExtensionContext context) {
            String caseId = extractCaseId(context);
            String historyId = buildHistoryId(context, caseId);

            Allure.getLifecycle().updateTestCase(testCase -> {
                testCase.setHistoryId(historyId);
                testCase.getLabels().removeIf(l -> RETRY_LABEL.equals(l.getName()));
                testCase.getLabels().add(new Label().setName(RETRY_LABEL).setValue(String.valueOf(attempt + 1)));
            });
        }

        private String extractCaseId(ExtensionContext context) {
            return CaseIdExtractor.extract(context);
        }

        private String buildHistoryId(ExtensionContext context, String caseId) {
            String className = context.getTestClass()
                    .map(Class::getName)
                    .orElse("unknown");

            String methodName = context.getTestMethod()
                    .map(java.lang.reflect.Method::getName)
                    .orElse("unknown");

            if (caseId != null && !caseId.isBlank()) {
                return className + "#" + methodName + "#" + caseId;
            }

            return className + "#" + methodName;
        }
    }
}
