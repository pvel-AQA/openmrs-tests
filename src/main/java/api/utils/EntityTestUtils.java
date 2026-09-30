package api.utils;

import common.generators.PartialEntityGenerator;
import org.junit.jupiter.params.provider.Arguments;

import java.util.function.Consumer;

public final class EntityTestUtils {
    private EntityTestUtils() {

    }

    public static <T> Arguments entityWithError(
            String caseId,
            Class<T> clazz,
            String[] fieldsToGenerate,
            Consumer<T> customizer,
            String errorKey,
            String errorMessage) {
        T entity = PartialEntityGenerator.generate(clazz, fieldsToGenerate);
        customizer.accept(entity);

        return Arguments.of(caseId, entity, errorKey, errorMessage);
    }

    public static <T> Arguments entityWithValidValue(
            String caseId,
            Class<T> clazz,
            String[] fieldsToGenerate,
            Consumer<T> customizer) {
        T entity = PartialEntityGenerator.generate(clazz, fieldsToGenerate);
        customizer.accept(entity);

        return Arguments.of(caseId, entity);
    }


}
