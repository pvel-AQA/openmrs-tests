package common.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private DateUtils() {

    }

    public static int calculateAge(String birthDate) {
        LocalDate birth = LocalDate.parse(birthDate, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate now = LocalDate.now();
        return Period.between(birth, now).getYears();
    }
}
