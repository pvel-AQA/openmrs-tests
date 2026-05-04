package common.generators;

import api.models.Gender;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class RandomDataGenerator {
    private static final Random RANDOM = new Random();
    private static final int MAX_AGE = 130;

    private RandomDataGenerator() {

    }

    public static String generateValidDate() {
        int year = 1900 + RANDOM.nextInt(126); // 1900-2025
        int month = 1 + RANDOM.nextInt(12);
        int day = 1 + RANDOM.nextInt(java.time.YearMonth.of(year, month).lengthOfMonth());
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    public static String randomString(int length){
        String letters = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(letters.charAt(RANDOM.nextInt(letters.length())));
        }
        return sb.toString();
    }

    public static String generateName() {
        int minLength = 3;
        int maxLength = 15;
        int length = RandomUtils.secure().randomInt(minLength, maxLength + 1);

        return RandomStringUtils.secure().nextAlphabetic(length);
    }

    public static Gender randomGender(){
        Gender[] genders = Gender.values();
        return genders[RANDOM.nextInt(genders.length)];
    }

    public static int randomAge(int minAge, int maxAge) {
        return RANDOM.nextInt(maxAge-minAge+1)+minAge;
    }

    public static String randomDateBetween(LocalDate from, LocalDate to) {
        long daysBetween = ChronoUnit.DAYS.between(from, to);
        LocalDate randomDate = from.plusDays(
                RANDOM.nextLong(daysBetween + 1));
        return randomDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        // produces "1990-06-15"
    }
}
