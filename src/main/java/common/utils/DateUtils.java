package common.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateUtils {
    private DateUtils() {

    }

    public static int calculateAge(String birthDate) {
        LocalDate birth = LocalDate.parse(birthDate, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate now = LocalDate.now();
        return Period.between(birth, now).getYears();
    }

    public static String convertDdMmYyyyToDdMmmYyyy(String dateInDdmmyyyy) {
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", java.util.Locale.ENGLISH);

            LocalDate date = LocalDate.parse(dateInDdmmyyyy, inputFormatter);
            String result = date.format(outputFormatter);

            result = result.replace("Sep", "Sept");

            return result;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Incorrect date format: " + dateInDdmmyyyy + ". Expected ddMMyyyy", e);
        }
    }
}
