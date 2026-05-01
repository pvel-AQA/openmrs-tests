package common.generators;

import api.models.Gender;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Random;

public class RandomGenerators {
    private static final Random RANDOM = new Random();
    private static final int MAX_AGE = 130;

    public static String randomString(int length){
        String letters = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(letters.charAt(RANDOM.nextInt(letters.length())));
        }
        return sb.toString();
    }

    public static Gender randomGender(){
        Gender[] genders = Gender.values();
        return genders[RANDOM.nextInt(genders.length)];
    }

    public static int randomAge(int minAge, int maxAge) {
        return RANDOM.nextInt(maxAge-minAge+1)+minAge;
    }

    public static String randomDateBetween(@NotNull LocalDate from, @NotNull LocalDate to) {
        long daysBetween = ChronoUnit.DAYS.between(from, to);
        LocalDate randomDate = from.plusDays(
                RANDOM.nextLong(daysBetween + 1));
        return randomDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        // produces "1990-06-15"
    }


}
