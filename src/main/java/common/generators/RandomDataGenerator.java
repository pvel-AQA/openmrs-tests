package common.generators;

import java.util.Random;

public class RandomDataGenerator {
    private static final Random RANDOM = new Random();

    private RandomDataGenerator() {

    }

    public static String generateValidDate() {
        int year = 1900 + RANDOM.nextInt(126); // 1900-2025
        int month = 1 + RANDOM.nextInt(12);
        int day = 1 + RANDOM.nextInt(java.time.YearMonth.of(year, month).lengthOfMonth());
        return String.format("%04d-%02d-%02d", year, month, day);
    }
}
