package common.coverage;

import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class AllureResultsParser {

    private static final ObjectMapper JSON = new ObjectMapper();

    private AllureResultsParser() {
    }

    public static List<AllureResult> parseDirectory(Path dir) {
        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException("Not a directory: " + dir);
        }
        List<AllureResult> results = new ArrayList<>();
        try (Stream<Path> files = Files.walk(dir)) {
            files.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith("-result.json"))
                    .forEach(p -> results.add(readOne(p)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return results;
    }

    private static AllureResult readOne(Path file) {
        return JSON.readValue(file.toFile(), AllureResult.class);
    }

    public static String extractCaseId(AllureResult result) {
        if (result.labels() == null) {
            return null;
        }
        boolean marker = false;
        for (AllureResult.Label l : result.labels()) {
            if (!"tag".equals(l.name())) {
                continue;
            }
            if (marker) {
                return l.value();
            }
            if ("case-id".equals(l.value())) {
                marker = true;
            }
        }
        return null;
    }
}
