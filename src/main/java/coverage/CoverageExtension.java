package coverage;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * JUnit 5 extension that wires coverage into your test run automatically.
 *
 * - On first test class start: loads the spec.
 * - On JVM shutdown (after ALL tests finish): writes the CSV + HTML reports.
 *
 * Using a shutdown hook (rather than @AfterAll) means it fires once for the whole
 * run no matter how many test classes you have, which is what you want for an
 * aggregate coverage report.
 *
 * Activate it on your base test class:
 *
 *     @ExtendWith(CoverageExtension.class)
 *     public abstract class BaseApiTest { ... }
 *
 * Or globally via src/test/resources/META-INF/services (see SETUP.md).
 */
public class CoverageExtension implements BeforeAllCallback {

    private static final AtomicBoolean STARTED = new AtomicBoolean(false);

    // Override via -Dcoverage.spec=... and -Dcoverage.out=... if you like.
    private static final String SPEC = System.getProperty(
            "coverage.spec", "openmrs-swagger-complete.json");
    private static final String OUT = System.getProperty(
            "coverage.out", "target/coverage");

    @Override
    public void beforeAll(ExtensionContext context) {
        if (STARTED.compareAndSet(false, true)) {
            SwaggerCoverage.init(SPEC);
            Runtime.getRuntime().addShutdownHook(new Thread(() ->
                    SwaggerCoverage.writeReports(OUT)));
        }
    }
}
