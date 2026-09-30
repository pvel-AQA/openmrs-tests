package common.annotations;

import common.extensions.RetryExtension;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@TestTemplate
@ExtendWith(RetryExtension.class)
public @interface TestWithRetry {
    /**
     * Max number of attempts (including first one).
     */
    int maxAttempts() default 3;

    /**
     * Delay between attempts in milliseconds.
     */
    long delayMs() default 0L;
}
