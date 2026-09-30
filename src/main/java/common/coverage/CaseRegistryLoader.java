package common.coverage;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.yaml.YAMLFactory;

import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class CaseRegistryLoader {

    private static final ObjectMapper YAML = new ObjectMapper(new YAMLFactory());

    private CaseRegistryLoader() {
    }

    public static Map<String, TestCase> load(String resourcePath) {
        try (InputStream is = CaseRegistryLoader.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalStateException("Registry not found: " + resourcePath);
            }
            Registry registry = YAML.readValue(is, Registry.class);

            Map<String, TestCase> byId = new LinkedHashMap<>();
            Set<String> duplicates = new HashSet<>();

            for (TestCase tc : registry.cases()) {
                if (tc.id() == null || tc.id().isBlank()) {
                    throw new IllegalStateException("Case with blank id in registry");
                }
                if (byId.putIfAbsent(tc.id(), tc) != null) {
                    duplicates.add(tc.id());
                }
            }
            if (!duplicates.isEmpty()) {
                throw new IllegalStateException("Duplicate case IDs: " + duplicates);
            }
            return byId;

        } catch (java.io.IOException e) {
            // InputStream.close() может бросить IOException — ловим только его
            throw new java.io.UncheckedIOException(e);
        }
    }
}
